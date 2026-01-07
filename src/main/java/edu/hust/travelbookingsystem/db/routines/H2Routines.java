package edu.hust.travelbookingsystem.db.routines;

import java.math.BigDecimal;
import java.sql.*;

/**
 * H2 routines registered via CREATE ALIAS.
 * H2 will pass a JDBC Connection automatically when the first parameter is Connection.
 * 
 * This class provides database programmability features:
 * - Scalar Functions (FN_*): Return single values for calculations/validations
 * - Table-Valued Functions: Return result sets
 * - Stored Procedures (SP_*): Execute business logic with transactions
 * 
 * @author Travel Booking System Team
 */
public class H2Routines {

    // ============================================================
    // SECTION 1: SCALAR FUNCTIONS (Return single values)
    // ============================================================

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

    /**
     * FN_CALCULATE_FLIGHT_PRICE: Calculate dynamic flight price based on demand.
     * Price increases as seats fill up (yield management simulation).
     * Formula: base_price * (1 + (booked_percentage * 0.5))
     */
    public static BigDecimal calculateFlightPrice(Connection conn, long flightId, int numSeats) throws SQLException {
        String sql = "SELECT price, number_of_chairs, seats_available FROM flight WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, flightId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) throw new SQLException("Flight not found: " + flightId);
                
                double basePrice = rs.getDouble("price");
                int totalChairs = rs.getInt("number_of_chairs");
                int available = rs.getInt("seats_available");
                
                if (numSeats > available) {
                    throw new SQLException("Not enough seats available. Requested: " + numSeats + ", Available: " + available);
                }
                
                // Calculate demand factor (0.0 to 1.0)
                double bookedPercentage = (double)(totalChairs - available) / totalChairs;
                double demandMultiplier = 1.0 + (bookedPercentage * 0.5);
                
                return BigDecimal.valueOf(basePrice * demandMultiplier * numSeats);
            }
        }
    }

    /**
     * FN_HOTEL_OCCUPANCY_RATE: Calculate hotel occupancy rate for a date range.
     * Returns percentage (0-100) of rooms booked.
     */
    public static BigDecimal hotelOccupancyRate(Connection conn, long hotelId, Date startDate, Date endDate) throws SQLException {
        if (startDate == null || endDate == null || !startDate.before(endDate)) {
            throw new SQLException("Invalid date range");
        }
        
        String totalRoomsSql = "SELECT COUNT(*) FROM hotel_bedroom WHERE hotel_id = ?";
        int totalRooms;
        try (PreparedStatement ps = conn.prepareStatement(totalRoomsSql)) {
            ps.setLong(1, hotelId);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                totalRooms = rs.getInt(1);
            }
        }
        
        if (totalRooms == 0) return BigDecimal.ZERO;
        
        String bookedSql =
            "SELECT COUNT(DISTINCT hb.hotel_bedroom_id) FROM hotel_booking hb " +
            "WHERE hb.hotel_id = ? AND (? < hb.end_date) AND (? > hb.start_date)";
        int bookedRooms;
        try (PreparedStatement ps = conn.prepareStatement(bookedSql)) {
            ps.setLong(1, hotelId);
            ps.setDate(2, startDate);
            ps.setDate(3, endDate);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                bookedRooms = rs.getInt(1);
            }
        }
        
        return BigDecimal.valueOf((double) bookedRooms / totalRooms * 100).setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    /**
     * FN_USER_LOYALTY_POINTS: Calculate loyalty points based on total spending.
     * 1 point per 100,000 VND spent.
     */
    public static int userLoyaltyPoints(Connection conn, long userId) throws SQLException {
        String sql = "SELECT COALESCE(SUM(total_price), 0) FROM orders o " +
                     "JOIN payment p ON o.payment_id = p.id " +
                     "WHERE o.user_id = ? AND p.status = 'PAID'";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                double totalSpent = rs.getDouble(1);
                return (int) (totalSpent / 100000);
            }
        }
    }

    /**
     * FN_DAYS_BETWEEN: Calculate number of days between two dates.
     */
    public static int daysBetween(Date startDate, Date endDate) throws SQLException {
        if (startDate == null || endDate == null) return 0;
        long diff = endDate.getTime() - startDate.getTime();
        return (int) (diff / (1000 * 60 * 60 * 24));
    }

    /**
     * FN_FORMAT_PRICE_VND: Format price as Vietnamese currency string.
     */
    public static String formatPriceVnd(double price) {
        java.text.NumberFormat formatter = java.text.NumberFormat.getInstance(new java.util.Locale("vi", "VN"));
        return formatter.format(price) + " VND";
    }

    /**
     * FN_VALIDATE_EMAIL: Validate email format using regex.
     */
    public static boolean validateEmail(String email) {
        if (email == null || email.isBlank()) return false;
        return email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    }

    /**
     * FN_VALIDATE_PHONE_VN: Validate Vietnamese phone number format.
     */
    public static boolean validatePhoneVn(String phone) {
        if (phone == null || phone.isBlank()) return false;
        // Vietnamese phone: starts with 0, 10-11 digits
        return phone.matches("^0[0-9]{9,10}$");
    }

    /**
     * FN_FLIGHT_DURATION_HOURS: Calculate estimated flight duration based on distance.
     * (Simplified simulation for academic purposes)
     */
    public static BigDecimal flightDurationHours(String origin, String destination) {
        // Simulated distances between major Vietnamese cities (in hours)
        java.util.Map<String, java.util.Map<String, Double>> distances = new java.util.HashMap<>();
        
        java.util.Map<String, Double> fromHanoi = new java.util.HashMap<>();
        fromHanoi.put("Ho Chi Minh", 2.0);
        fromHanoi.put("Da Nang", 1.25);
        fromHanoi.put("Nha Trang", 1.75);
        fromHanoi.put("Phu Quoc", 2.25);
        distances.put("Hanoi", fromHanoi);
        
        java.util.Map<String, Double> fromHCM = new java.util.HashMap<>();
        fromHCM.put("Hanoi", 2.0);
        fromHCM.put("Da Nang", 1.25);
        fromHCM.put("Nha Trang", 1.0);
        fromHCM.put("Phu Quoc", 1.0);
        distances.put("Ho Chi Minh", fromHCM);
        
        if (distances.containsKey(origin) && distances.get(origin).containsKey(destination)) {
            return BigDecimal.valueOf(distances.get(origin).get(destination));
        }
        return BigDecimal.valueOf(1.5); // Default duration
    }

    // ============================================================
    // SECTION 2: TABLE-VALUED FUNCTIONS (Return ResultSets)
    // ============================================================

    /**
     * FN_GET_AVAILABLE_ROOMS: Returns available rooms for a hotel in date range.
     * This is a table-valued function returning a ResultSet.
     */
    public static ResultSet getAvailableRooms(Connection conn, long hotelId, Date startDate, Date endDate) throws SQLException {
        String sql = 
            "SELECT hbr.id, hbr.room_number, hbr.price, hbr.room_type " +
            "FROM hotel_bedroom hbr " +
            "WHERE hbr.hotel_id = ? " +
            "AND NOT EXISTS (" +
            "    SELECT 1 FROM hotel_booking hb " +
            "    WHERE hb.hotel_bedroom_id = hbr.id " +
            "    AND hb.hotel_id = hbr.hotel_id " +
            "    AND (? < hb.end_date) AND (? > hb.start_date)" +
            ") ORDER BY hbr.room_number";
        
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setLong(1, hotelId);
        ps.setDate(2, startDate);
        ps.setDate(3, endDate);
        return ps.executeQuery();
    }

    /**
     * FN_SEARCH_FLIGHTS: Search flights by criteria.
     */
    public static ResultSet searchFlights(Connection conn, String ticketClass, Date departDate, double maxPrice) throws SQLException {
        StringBuilder sql = new StringBuilder(
            "SELECT id, ticket_class, airline_name, price, check_in_date, check_out_date, seats_available " +
            "FROM flight WHERE seats_available > 0"
        );
        
        if (ticketClass != null && !ticketClass.isBlank()) {
            sql.append(" AND ticket_class = ?");
        }
        if (departDate != null) {
            sql.append(" AND check_in_date = ?");
        }
        if (maxPrice > 0) {
            sql.append(" AND price <= ?");
        }
        sql.append(" ORDER BY check_in_date, price");
        
        PreparedStatement ps = conn.prepareStatement(sql.toString());
        int idx = 1;
        if (ticketClass != null && !ticketClass.isBlank()) ps.setString(idx++, ticketClass);
        if (departDate != null) ps.setDate(idx++, departDate);
        if (maxPrice > 0) ps.setDouble(idx++, maxPrice);
        
        return ps.executeQuery();
    }

    /**
     * FN_USER_BOOKING_HISTORY: Get user's booking history with details.
     */
    public static ResultSet userBookingHistory(Connection conn, long userId) throws SQLException {
        String sql = 
            "SELECT o.id AS order_id, o.destination, o.order_date, o.total_price, " +
            "       p.status AS payment_status, f.airline_name, h.hotel_name " +
            "FROM orders o " +
            "LEFT JOIN payment p ON o.payment_id = p.id " +
            "LEFT JOIN flight f ON o.flight_id = f.id " +
            "LEFT JOIN hotels h ON o.hotel_id = h.id " +
            "WHERE o.user_id = ? " +
            "ORDER BY o.order_date DESC";
        
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setLong(1, userId);
        return ps.executeQuery();
    }

    /**
     * FN_TOP_RATED_HOTELS: Get top-rated hotels with average rating.
     */
    public static ResultSet topRatedHotels(Connection conn, int limit) throws SQLException {
        String sql = 
            "SELECT h.id, h.hotel_name, h.address, h.hotel_price, " +
            "       COUNT(r.id) AS review_count, " +
            "       COALESCE(AVG(CAST(r.rating AS DOUBLE)), 0) AS avg_rating " +
            "FROM hotels h " +
            "LEFT JOIN reviews r ON h.id = r.hotel_id " +
            "GROUP BY h.id, h.hotel_name, h.address, h.hotel_price " +
            "ORDER BY avg_rating DESC, review_count DESC " +
            "LIMIT ?";
        
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, limit);
        return ps.executeQuery();
    }

    // ============================================================
    // SECTION 3: STORED PROCEDURES (Business Logic)
    // ============================================================
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

    // ============================================================
    // SECTION 4: ADDITIONAL STORED PROCEDURES
    // ============================================================

    /**
     * SP_CREATE_COMPLETE_BOOKING: Creates a complete booking with flight and hotel.
     * This is a comprehensive transaction demonstrating ACID properties.
     * 
     * @param userId User making the booking
     * @param destination Travel destination
     * @param numberOfPeople Number of travelers
     * @param flightId Flight to book (nullable)
     * @param seatNumbers Comma-separated seat numbers (e.g., "1A,1B,2A")
     * @param hotelId Hotel to book (nullable)
     * @param bedroomIds Comma-separated bedroom IDs
     * @param hotelStartDate Check-in date
     * @param hotelEndDate Check-out date
     * @return Order ID of created booking
     */
    public static long createCompleteBooking(Connection conn,
                                             long userId,
                                             String destination,
                                             int numberOfPeople,
                                             Long flightId,
                                             String seatNumbers,
                                             Long hotelId,
                                             String bedroomIds,
                                             Date hotelStartDate,
                                             Date hotelEndDate) throws SQLException {
        conn.setAutoCommit(false);
        long orderId = -1;
        
        try {
            conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            
            // Validate user exists
            requireExists(conn, "SELECT 1 FROM users WHERE id = ? AND status = TRUE", userId, "User does not exist or is inactive.");
            
            // Create payment record (initially UNPAID)
            long paymentId;
            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO payment (status) VALUES ('UNPAID')",
                    Statement.RETURN_GENERATED_KEYS)) {
                ps.executeUpdate();
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    rs.next();
                    paymentId = rs.getLong(1);
                }
            }
            
            // Create order
            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO orders (destination, number_of_people, user_id, payment_id, flight_id, hotel_id, " +
                    "check_in_date, check_out_date, start_hotel, end_hotel, total_price) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 0)",
                    Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, destination);
                ps.setInt(2, numberOfPeople);
                ps.setLong(3, userId);
                ps.setLong(4, paymentId);
                if (flightId != null) ps.setLong(5, flightId); else ps.setNull(5, Types.BIGINT);
                if (hotelId != null) ps.setLong(6, hotelId); else ps.setNull(6, Types.BIGINT);
                if (flightId != null) {
                    // Get flight dates
                    try (PreparedStatement fps = conn.prepareStatement(
                            "SELECT check_in_date, check_out_date FROM flight WHERE id = ?")) {
                        fps.setLong(1, flightId);
                        try (ResultSet frs = fps.executeQuery()) {
                            if (frs.next()) {
                                ps.setDate(7, frs.getDate(1));
                                ps.setDate(8, frs.getDate(2));
                            } else {
                                ps.setNull(7, Types.DATE);
                                ps.setNull(8, Types.DATE);
                            }
                        }
                    }
                } else {
                    ps.setNull(7, Types.DATE);
                    ps.setNull(8, Types.DATE);
                }
                if (hotelStartDate != null) ps.setDate(9, hotelStartDate); else ps.setNull(9, Types.DATE);
                if (hotelEndDate != null) ps.setDate(10, hotelEndDate); else ps.setNull(10, Types.DATE);
                
                ps.executeUpdate();
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    rs.next();
                    orderId = rs.getLong(1);
                }
            }
            
            double totalPrice = 0;
            
            // Book flight seats
            if (flightId != null && seatNumbers != null && !seatNumbers.isBlank()) {
                String[] seats = seatNumbers.split(",");
                for (String seat : seats) {
                    seat = seat.trim();
                    if (!seat.isEmpty()) {
                        bookFlightSeat(conn, orderId, flightId, seat);
                        // Add seat price to total
                        try (PreparedStatement ps = conn.prepareStatement(
                                "SELECT price FROM flight WHERE id = ?")) {
                            ps.setLong(1, flightId);
                            try (ResultSet rs = ps.executeQuery()) {
                                if (rs.next()) totalPrice += rs.getDouble(1);
                            }
                        }
                    }
                }
            }
            
            // Book hotel rooms
            if (hotelId != null && bedroomIds != null && !bedroomIds.isBlank() && 
                hotelStartDate != null && hotelEndDate != null) {
                String[] bedrooms = bedroomIds.split(",");
                int nights = daysBetween(hotelStartDate, hotelEndDate);
                
                for (String bedroomIdStr : bedrooms) {
                    long bedroomId = Long.parseLong(bedroomIdStr.trim());
                    createHotelBooking(conn, orderId, hotelId, bedroomId, hotelStartDate, hotelEndDate);
                    
                    // Add room price to total
                    try (PreparedStatement ps = conn.prepareStatement(
                            "SELECT price FROM hotel_bedroom WHERE id = ?")) {
                        ps.setLong(1, bedroomId);
                        try (ResultSet rs = ps.executeQuery()) {
                            if (rs.next()) totalPrice += rs.getDouble(1) * nights;
                        }
                    }
                }
            }
            
            // Update total price
            try (PreparedStatement ps = conn.prepareStatement(
                    "UPDATE orders SET total_price = ? WHERE id = ?")) {
                ps.setDouble(1, totalPrice);
                ps.setLong(2, orderId);
                ps.executeUpdate();
            }
            
            conn.commit();
            return orderId;
            
        } catch (SQLException ex) {
            conn.rollback();
            throw ex;
        } finally {
            conn.setAutoCommit(true);
        }
    }

    /**
     * SP_PROCESS_PAYMENT: Process payment for an order.
     * Validates order, updates payment status, and applies loyalty discount if eligible.
     */
    public static void processPayment(Connection conn, long orderId, String paymentMethod) throws SQLException {
        conn.setAutoCommit(false);
        try {
            // Get order and payment info
            Long paymentId = null;
            Long userId = null;
            double totalPrice = 0;
            
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT payment_id, user_id, total_price FROM orders WHERE id = ?")) {
                ps.setLong(1, orderId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) throw new SQLException("Order not found: " + orderId);
                    paymentId = rs.getLong("payment_id");
                    userId = rs.getLong("user_id");
                    totalPrice = rs.getDouble("total_price");
                }
            }
            
            // Check current payment status
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT status FROM payment WHERE id = ?")) {
                ps.setLong(1, paymentId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next() && "PAID".equals(rs.getString(1))) {
                        throw new SQLException("Order already paid.");
                    }
                }
            }
            
            // Apply loyalty discount (5% off for users with 50+ points)
            int loyaltyPoints = userLoyaltyPoints(conn, userId);
            if (loyaltyPoints >= 50) {
                totalPrice = totalPrice * 0.95; // 5% discount
                try (PreparedStatement ps = conn.prepareStatement(
                        "UPDATE orders SET total_price = ? WHERE id = ?")) {
                    ps.setDouble(1, totalPrice);
                    ps.setLong(2, orderId);
                    ps.executeUpdate();
                }
            }
            
            // Update payment status to PAID
            try (PreparedStatement ps = conn.prepareStatement(
                    "UPDATE payment SET status = 'PAID' WHERE id = ?")) {
                ps.setLong(1, paymentId);
                ps.executeUpdate();
            }
            
            conn.commit();
        } catch (SQLException ex) {
            conn.rollback();
            throw ex;
        } finally {
            conn.setAutoCommit(true);
        }
    }

    /**
     * SP_SUBMIT_REVIEW: Submit a review for a hotel stay.
     * Validates that user has a completed (paid) order for this hotel.
     */
    public static long submitReview(Connection conn, long orderId, int rating, String comment) throws SQLException {
        if (rating < 1 || rating > 5) {
            throw new SQLException("Rating must be between 1 and 5");
        }
        
        conn.setAutoCommit(false);
        try {
            // Get order details and verify it's paid
            Long userId = null;
            Long hotelId = null;
            
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT o.user_id, o.hotel_id, p.status " +
                    "FROM orders o JOIN payment p ON o.payment_id = p.id " +
                    "WHERE o.id = ?")) {
                ps.setLong(1, orderId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) throw new SQLException("Order not found");
                    if (!"PAID".equals(rs.getString("status"))) {
                        throw new SQLException("Cannot review unpaid order");
                    }
                    userId = rs.getLong("user_id");
                    hotelId = rs.getLong("hotel_id");
                    if (rs.wasNull()) throw new SQLException("Order has no hotel booking");
                }
            }
            
            // Check if review already exists
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT 1 FROM reviews WHERE order_id = ?")) {
                ps.setLong(1, orderId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) throw new SQLException("Review already exists for this order");
                }
            }
            
            // Insert review
            long reviewId;
            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO reviews (rating, comment, user_id, hotel_id, order_id) VALUES (?, ?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, rating);
                ps.setString(2, comment);
                ps.setLong(3, userId);
                ps.setLong(4, hotelId);
                ps.setLong(5, orderId);
                ps.executeUpdate();
                
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    rs.next();
                    reviewId = rs.getLong(1);
                }
            }
            
            conn.commit();
            return reviewId;
            
        } catch (SQLException ex) {
            conn.rollback();
            throw ex;
        } finally {
            conn.setAutoCommit(true);
        }
    }

    /**
     * SP_GENERATE_REVENUE_REPORT: Generate revenue statistics for a date range.
     * Returns aggregated revenue data.
     */
    public static ResultSet generateRevenueReport(Connection conn, Date startDate, Date endDate) throws SQLException {
        String sql = 
            "SELECT " +
            "  COALESCE(SUM(o.total_price), 0) AS total_revenue, " +
            "  COUNT(o.id) AS total_orders, " +
            "  COUNT(DISTINCT o.user_id) AS unique_customers, " +
            "  COALESCE(AVG(o.total_price), 0) AS avg_order_value, " +
            "  COALESCE(MAX(o.total_price), 0) AS max_order_value, " +
            "  COALESCE(MIN(o.total_price), 0) AS min_order_value " +
            "FROM orders o " +
            "JOIN payment p ON o.payment_id = p.id " +
            "WHERE p.status = 'PAID' " +
            "AND o.order_date BETWEEN ? AND ?";
        
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setDate(1, startDate);
        ps.setDate(2, endDate);
        return ps.executeQuery();
    }

    /**
     * SP_UPDATE_FLIGHT_SEATS: Bulk update flight seat availability.
     * Used for managing flight capacity.
     */
    public static void updateFlightSeats(Connection conn, long flightId, int newTotalSeats) throws SQLException {
        conn.setAutoCommit(false);
        try {
            // Validate flight exists
            requireExists(conn, "SELECT 1 FROM flight WHERE id = ?", flightId, "Flight not found");
            
            // Get current booked seat count
            int bookedCount;
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT COUNT(*) FROM flight_seats WHERE flight_id = ? AND is_booked = TRUE")) {
                ps.setLong(1, flightId);
                try (ResultSet rs = ps.executeQuery()) {
                    rs.next();
                    bookedCount = rs.getInt(1);
                }
            }
            
            if (newTotalSeats < bookedCount) {
                throw new SQLException("Cannot reduce seats below booked count: " + bookedCount);
            }
            
            // Update flight capacity
            try (PreparedStatement ps = conn.prepareStatement(
                    "UPDATE flight SET number_of_chairs = ?, seats_available = ? WHERE id = ?")) {
                ps.setInt(1, newTotalSeats);
                ps.setInt(2, newTotalSeats - bookedCount);
                ps.setLong(3, flightId);
                ps.executeUpdate();
            }
            
            conn.commit();
        } catch (SQLException ex) {
            conn.rollback();
            throw ex;
        } finally {
            conn.setAutoCommit(true);
        }
    }

    /**
     * SP_TRANSFER_BOOKING: Transfer a booking from one user to another.
     * Demonstrates complex business logic with multiple table updates.
     */
    public static void transferBooking(Connection conn, long orderId, long newUserId) throws SQLException {
        conn.setAutoCommit(false);
        try {
            // Validate order exists and is not paid
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT p.status FROM orders o JOIN payment p ON o.payment_id = p.id WHERE o.id = ?")) {
                ps.setLong(1, orderId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) throw new SQLException("Order not found");
                    if ("PAID".equals(rs.getString(1))) {
                        throw new SQLException("Cannot transfer paid booking");
                    }
                }
            }
            
            // Validate new user exists and is active
            requireExists(conn, "SELECT 1 FROM users WHERE id = ? AND status = TRUE", 
                         newUserId, "New user does not exist or is inactive");
            
            // Transfer order
            try (PreparedStatement ps = conn.prepareStatement(
                    "UPDATE orders SET user_id = ? WHERE id = ?")) {
                ps.setLong(1, newUserId);
                ps.setLong(2, orderId);
                ps.executeUpdate();
            }
            
            conn.commit();
        } catch (SQLException ex) {
            conn.rollback();
            throw ex;
        } finally {
            conn.setAutoCommit(true);
        }
    }

    /**
     * SP_CLEANUP_EXPIRED_TOKENS: Clean up expired password reset tokens.
     * Demonstrates maintenance procedure.
     */
    public static int cleanupExpiredTokens(Connection conn) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(
                "DELETE FROM password_reset_tokens WHERE expiry_date < CURRENT_TIMESTAMP OR used = TRUE")) {
            return ps.executeUpdate();
        }
    }

    /**
     * SP_GET_HOTEL_STATISTICS: Get comprehensive hotel statistics.
     */
    public static ResultSet getHotelStatistics(Connection conn, long hotelId) throws SQLException {
        String sql = 
            "SELECT " +
            "  h.hotel_name, " +
            "  h.address, " +
            "  (SELECT COUNT(*) FROM hotel_bedroom WHERE hotel_id = h.id) AS total_rooms, " +
            "  (SELECT COUNT(*) FROM orders WHERE hotel_id = h.id) AS total_bookings, " +
            "  (SELECT COALESCE(SUM(o.total_price), 0) FROM orders o " +
            "   JOIN payment p ON o.payment_id = p.id " +
            "   WHERE o.hotel_id = h.id AND p.status = 'PAID') AS total_revenue, " +
            "  (SELECT COALESCE(AVG(CAST(r.rating AS DOUBLE)), 0) FROM reviews r WHERE r.hotel_id = h.id) AS avg_rating, " +
            "  (SELECT COUNT(*) FROM reviews WHERE hotel_id = h.id) AS review_count " +
            "FROM hotels h WHERE h.id = ?";
        
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setLong(1, hotelId);
        return ps.executeQuery();
    }
}

