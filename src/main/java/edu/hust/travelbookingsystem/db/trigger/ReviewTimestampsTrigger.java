package edu.hust.travelbookingsystem.db.trigger;

import org.h2.api.Trigger;

import java.sql.Timestamp;
import java.sql.Connection;
import java.sql.SQLException;

public class ReviewTimestampsTrigger implements Trigger {

    @Override
    public void init(Connection conn, String schemaName, String triggerName,
                     String tableName, boolean before, int type) { }

    @Override
    public void fire(Connection conn, Object[] oldRow, Object[] newRow) throws SQLException {
        Timestamp now = new Timestamp(System.currentTimeMillis());

        // Insert: oldRow == null
        if (oldRow == null) {
            if (newRow[3] == null) newRow[3] = now; // created_at
            newRow[4] = now;                        // updated_at
        } else {
            // Update: always bump updated_at
            newRow[4] = now;
        }
    }

    @Override public void close() { }
    @Override public void remove() { }
}
