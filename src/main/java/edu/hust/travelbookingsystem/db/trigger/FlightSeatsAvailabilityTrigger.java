package edu.hust.travelbookingsystem.db.trigger;

import org.h2.api.Trigger;

import java.sql.*;

public class FlightSeatsAvailabilityTrigger implements Trigger {

    @Override
    public void init(Connection conn, String schemaName, String triggerName,
                     String tableName, boolean before, int type) { }

    @Override
    public void fire(Connection conn, Object[] oldRow, Object[] newRow) throws SQLException {
        // flight_seats columns:
        // 0=id, 1=flight_id, 2=seat_number, 3=is_booked, 4=order_id, 5=version
        Object[] row = (newRow != null) ? newRow : oldRow;
        long flightId = ((Number) row[1]).longValue();

        int chairs;
        try (PreparedStatement ps = conn.prepareStatement("SELECT number_of_chairs FROM flight WHERE id = ?")) {
            ps.setLong(1, flightId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return; // flight missing should be impossible due to FK
                chairs = rs.getInt(1);
            }
        }

        int booked;
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT COUNT(*) FROM flight_seats WHERE flight_id = ? AND is_booked = TRUE"
        )) {
            ps.setLong(1, flightId);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                booked = rs.getInt(1);
            }
        }

        int available = chairs - booked;
        if (available < 0) throw new SQLException("Inconsistent availability: seats_available would be negative.");

        try (PreparedStatement ps = conn.prepareStatement(
                "UPDATE flight SET seats_available = ? WHERE id = ?"
        )) {
            ps.setInt(1, available);
            ps.setLong(2, flightId);
            ps.executeUpdate();
        }
    }

    @Override public void close() { }
    @Override public void remove() { }
}
