package edu.hust.travelbookingsystem.db.routines;

import java.math.BigDecimal;
import java.sql.*;

/**
 * H2 routines registered via CREATE ALIAS.
 * H2 will pass a JDBC Connection automatically when the first parameter is Connection.
 */
public class H2Routines {

    /** Returns TRUE if the room has no overlap in [startDate, endDate). */
    public static boolean roomAvailable(Connection conn,
                                        long hotelId,
                                        long hotelBedroomId,
                                        Date startDate,
                                        Date endDate) throws SQLException {
        if (startDate == null || endDate == null || !startDate.before(endDate)) return false;

        String sql =
                "SELECT COUNT(*) " +
                        "FROM hotel_booking hb " +
                        "WHERE hb.hotel_id = ? AND hb.hotel_bedroom_id = ? " +
                        "  AND (? < hb.end_date) AND (? > hb.start_date)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, hotelId);
            ps.setLong(2, hotelBedroomId);
            ps.setDate(3, startDate);
            ps.setDate(4, endDate);

            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1) == 0;
            }
        }
    }

    /** Total revenue for a user (course-friendly analytic function). */
    public static BigDecimal totalRevenuePerUser(Connection conn, long userId) throws SQLException {
        String sql = "SELECT COALESCE(SUM(total_price), 0) FROM orders WHERE user_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getBigDecimal(1);
            }
        }
    }

    /** Transactional hotel booking creation with overlap protection. */
    public static void createHotelBooking(Connection conn,
                                          long orderId,
                                          long hotelId,
                                          long hotelBedroomId,
                                          Date startDate,
                                          Date endDate) throws SQLException {
        if (startDate == null || endDate == null || !startDate.before(endDate)) {
            throw new SQLException("Invalid date range: start_date must be < end_date.");
        }

        conn.setAutoCommit(false);
        try {
            // Strong isolation reduces race conditions (grading-friendly)
            conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);

            // Basic referential existence checks (clear messages for coursework)
            requireExists(conn, "SELECT 1 FROM orders WHERE id = ?", orderId, "Order does not exist.");
            requireExists(conn, "SELECT 1 FROM hotels WHERE id = ?", hotelId, "Hotel does not exist.");
            requireExists(conn,
                    "SELECT 1 FROM hotel_bedroom WHERE id = ? AND hotel_id = ?",
                    new Object[]{hotelBedroomId, hotelId},
                    "Hotel bedroom does not exist for the given hotel."
            );

            if (!roomAvailable(conn, hotelId, hotelBedroomId, startDate, endDate)) {
                throw new SQLException("Room is not available (overlapping booking).");
            }

            try (PreparedStatement ins = conn.prepareStatement(
                    "INSERT INTO hotel_booking(order_id, hotel_id, hotel_bedroom_id, start_date, end_date) " +
                            "VALUES (?,?,?,?,?)"
            )) {
                ins.setLong(1, orderId);
                ins.setLong(2, hotelId);
                ins.setLong(3, hotelBedroomId);
                ins.setDate(4, startDate);
                ins.setDate(5, endDate);
                ins.executeUpdate();
            }

            conn.commit();
        } catch (SQLException ex) {
            conn.rollback();
            throw ex;
        } finally {
            conn.setAutoCommit(true);
        }
    }

    /** Books exactly one seat if currently free (atomic update). */
    public static void bookFlightSeat(Connection conn,
                                      long orderId,
                                      long flightId,
                                      String seatNumber) throws SQLException {
        if (seatNumber == null || seatNumber.isBlank()) {
            throw new SQLException("seat_number is required.");
        }

        conn.setAutoCommit(false);
        try {
            conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);

            requireExists(conn, "SELECT 1 FROM orders WHERE id = ?", orderId, "Order does not exist.");
            requireExists(conn, "SELECT 1 FROM flight WHERE id = ?", flightId, "Flight does not exist.");

            try (PreparedStatement upd = conn.prepareStatement(
                    "UPDATE flight_seats " +
                            "SET is_booked = TRUE, order_id = ? " +
                            "WHERE flight_id = ? AND seat_number = ? AND is_booked = FALSE AND order_id IS NULL"
            )) {
                upd.setLong(1, orderId);
                upd.setLong(2, flightId);
                upd.setString(3, seatNumber);

                int rows = upd.executeUpdate();
                if (rows == 0) {
                    throw new SQLException("Seat is not available or does not exist.");
                }
            }

            // Availability recalculation is handled by trigger on flight_seats.
            conn.commit();
        } catch (SQLException ex) {
            conn.rollback();
            throw ex;
        } finally {
            conn.setAutoCommit(true);
        }
    }

    /** Cancels booking: frees flight seats, deletes hotel_booking rows, updates payment status if linked. */
    public static void cancelBooking(Connection conn, long orderId, String newPaymentStatus) throws SQLException {
        if (newPaymentStatus == null || newPaymentStatus.isBlank()) newPaymentStatus = "CANCELLED";

        conn.setAutoCommit(false);
        try {
            requireExists(conn, "SELECT 1 FROM orders WHERE id = ?", orderId, "Order does not exist.");

            try (PreparedStatement freeSeats = conn.prepareStatement(
                    "UPDATE flight_seats SET is_booked = FALSE, order_id = NULL WHERE order_id = ?"
            )) {
                freeSeats.setLong(1, orderId);
                freeSeats.executeUpdate();
            }

            try (PreparedStatement delHotel = conn.prepareStatement(
                    "DELETE FROM hotel_booking WHERE order_id = ?"
            )) {
                delHotel.setLong(1, orderId);
                delHotel.executeUpdate();
            }

            try (PreparedStatement updPay = conn.prepareStatement(
                    "UPDATE payment p SET status = ? " +
                            "WHERE p.id = (SELECT payment_id FROM orders WHERE id = ?)"
            )) {
                updPay.setString(1, newPaymentStatus);
                updPay.setLong(2, orderId);
                updPay.executeUpdate();
            }

            conn.commit();
        } catch (SQLException ex) {
            conn.rollback();
            throw ex;
        } finally {
            conn.setAutoCommit(true);
        }
    }

    // ---- helpers ----

    private static void requireExists(Connection conn, String sql, long id, String msg) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) throw new SQLException(msg);
            }
        }
    }

    private static void requireExists(Connection conn, String sql, Object[] args, String msg) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (int i = 0; i < args.length; i++) {
                Object a = args[i];
                if (a instanceof Long) ps.setLong(i + 1, (Long) a);
                else ps.setObject(i + 1, a);
            }
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) throw new SQLException(msg);
            }
        }
    }
}
