package edu.hust.travelbookingsystem.db.trigger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.h2.api.Trigger;

import java.sql.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class AuditTrigger implements Trigger {

    private static final ObjectMapper MAPPER =
            new ObjectMapper()
                    .findAndRegisterModules()
                    .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    private String tableName;
    private String[] columns;

    @Override
    public void init(Connection conn, String schemaName, String triggerName,
                     String tableName, boolean before, int type) throws SQLException {
        this.tableName = tableName;

        // Capture column names once using metadata
        try (PreparedStatement ps = conn.prepareStatement("SELECT * FROM " + tableName + " WHERE 1=0")) {
            ResultSetMetaData md = ps.getMetaData();
            columns = new String[md.getColumnCount()];
            for (int i = 1; i <= md.getColumnCount(); i++) {
                columns[i - 1] = md.getColumnLabel(i);
            }
        }
    }

    @Override
    public void fire(Connection conn, Object[] oldRow, Object[] newRow) throws SQLException {
        String op;
        if (oldRow == null && newRow != null) op = "INSERT";
        else if (oldRow != null && newRow == null) op = "DELETE";
        else op = "UPDATE";

        String pk = "id=" + ((Number)((newRow != null) ? newRow[0] : oldRow[0])).longValue();
        String changedBy = conn.getMetaData().getUserName(); // typically "SA"

        String oldJson = (oldRow == null) ? null : toJson(oldRow);
        String newJson = (newRow == null) ? null : toJson(newRow);

        try (PreparedStatement ins = conn.prepareStatement(
                "INSERT INTO audit_log(changed_at, changed_by, table_name, operation, primary_key, old_data, new_data) " +
                        "VALUES (CURRENT_TIMESTAMP, ?, ?, ?, ?, ?, ?)"
        )) {
            ins.setString(1, changedBy);
            ins.setString(2, this.tableName);
            ins.setString(3, op);
            ins.setString(4, pk);
            ins.setString(5, oldJson);
            ins.setString(6, newJson);
            ins.executeUpdate();
        }
    }

    private String toJson(Object[] row) throws SQLException {
        try {
            Map<String, Object> m = new LinkedHashMap<>();
            for (int i = 0; i < columns.length && i < row.length; i++) {
                m.put(columns[i], row[i]);
            }
            return MAPPER.writeValueAsString(m);
        } catch (Exception e) {
            throw new SQLException("Failed to serialize audit JSON", e);
        }
    }

    @Override public void close() { }
    @Override public void remove() { }
}
