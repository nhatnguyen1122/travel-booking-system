-- H2-compatible schema for Travel Booking System
-- This replaces the SQL Server-specific schema-sqlserver.sql

-- Indexes (H2 creates indexes automatically for foreign keys, but explicit ones are fine)
CREATE INDEX IF NOT EXISTS idx_orders_user_id ON orders(user_id);
CREATE INDEX IF NOT EXISTS idx_orders_flight_id ON orders(flight_id);
CREATE INDEX IF NOT EXISTS idx_orders_hotel_id ON orders(hotel_id);
CREATE INDEX IF NOT EXISTS idx_orders_destination ON orders(destination);
CREATE INDEX IF NOT EXISTS idx_hb_hotel_bedroom_dates ON hotel_booking(hotel_id, hotel_bedroom_id, start_date, end_date);
CREATE INDEX IF NOT EXISTS idx_bedroom_hotel_id ON hotel_bedroom(hotel_id);
CREATE INDEX IF NOT EXISTS idx_payment_status ON payment(status);

-- Note: Unique constraints and check constraints are handled by JPA entity annotations
-- The SQL Server-specific triggers and stored procedures are not needed with H2
-- Business logic for room availability and booking overlap should be handled in Java service layer
