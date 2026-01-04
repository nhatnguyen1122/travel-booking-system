package edu.hust.travelbookingsystem.db.trigger;

import org.h2.api.Trigger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class HotelBookingNoOverlapTrigger implements Trigger {

    @Override
    public void init(Connection conn, String schemaName, String triggerName,
                     String tableName, boolean before, int type) {
        // no-op
    }

    @Override
    public void fire(Connection conn, Object[] oldRow, Object[] newRow) throws SQLException {
        // hotel_booking columns (per schema-h2.sql):
        // 0=id, 1=order_id, 2=hotel_id, 3=hotel_bedroom_id, 4=start_date, 5=end_date
        if (newRow == null) return; // should not happen for BEFORE INSERT/UPDATE

        Long id = extractId(oldRow, newRow);

        long hotelId = requireLong(newRow[2], "hotel_id");
        long bedroomId = requireLong(newRow[3], "hotel_bedroom_id");

        LocalDate start = asLocalDate(newRow[4], "start_date");
        LocalDate end = asLocalDate(newRow[5], "end_date");

        if (start == null || end == null || !start.isBefore(end)) {
            throw new SQLException("Invalid date range in hotel_booking (start_date must be < end_date).");
        }

        String sql =
                "SELECT COUNT(*) FROM hotel_booking hb " +
                        "WHERE hb.hotel_id = ? AND hb.hotel_bedroom_id = ? " +
                        "  AND (? < hb.end_date) AND (? > hb.start_date) " +
                        (id == null ? "" : "  AND hb.id <> ?");

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            int i = 1;
            ps.setLong(i++, hotelId);
            ps.setLong(i++, bedroomId);

            // H2/JDBC hay đưa DATE về LocalDate -> convert sang java.sql.Date để bind cho chắc
            ps.setDate(i++, java.sql.Date.valueOf(start));
            ps.setDate(i++, java.sql.Date.valueOf(end));

            if (id != null) {
                ps.setLong(i, id);
            }

            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                if (rs.getInt(1) > 0) {
                    throw new SQLException("Room is not available: overlapping booking exists.");
                }
            }
        }
    }

    private static Long extractId(Object[] oldRow, Object[] newRow) {
        Object v = newRow[0] != null ? newRow[0] : (oldRow != null ? oldRow[0] : null);
        if (v == null) return null;
        return ((Number) v).longValue();
    }

    private static long requireLong(Object v, String col) throws SQLException {
        if (v == null) throw new SQLException("Missing required column: " + col);
        if (v instanceof Number n) return n.longValue();
        throw new SQLException("Invalid type for " + col + ": " + v.getClass());
    }

    private static LocalDate asLocalDate(Object v, String col) throws SQLException {
        if (v == null) return null;
        if (v instanceof LocalDate ld) return ld;
        if (v instanceof java.sql.Date d) return d.toLocalDate();
        throw new SQLException("Invalid type for " + col + ": " + v.getClass());
    }

    @Override public void close() { }
    @Override public void remove() { }
}
