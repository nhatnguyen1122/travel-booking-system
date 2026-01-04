/* ============================================================
   Travel Booking System (H2) - Views + Triggers + Routines
   ------------------------------------------------------------
   H2 differences vs SQL Server:
   - Views are standard (CREATE OR REPLACE VIEW)
   - Triggers require Java classes implementing org.h2.api.Trigger
   - Stored procedures & UDFs are best done via CREATE ALIAS
   ============================================================ */

-- ============================================================
-- 1) Reporting Views
-- ============================================================

CREATE OR REPLACE VIEW v_booking_summary AS
SELECT
    o.id                   AS order_id,
    o.order_date,
    u.id                   AS user_id,
    u.full_name            AS user_name,
    u.email                AS user_email,
    o.destination,
    o.number_of_people,
    o.total_price,
    p.id                   AS payment_id,
    p.status               AS payment_status,
    f.id                   AS flight_id,
    f.airline_name,
    f.ticket_class,
    f.check_in_date        AS flight_depart_date,
    f.check_out_date       AS flight_return_date,
    h.id                   AS hotel_id,
    h.hotel_name,
    h.address              AS hotel_address,
    o.start_hotel,
    o.end_hotel,

    (SELECT COUNT(*)
     FROM flight_seats fs
     WHERE fs.order_id = o.id AND fs.is_booked = TRUE) AS booked_seats,

    CASE
        WHEN o.start_hotel IS NULL OR o.end_hotel IS NULL THEN NULL
        ELSE DATEDIFF('DAY', o.start_hotel, o.end_hotel)
        END AS hotel_nights
FROM orders o
         LEFT JOIN users   u ON u.id = o.user_id
         LEFT JOIN payment p ON p.id = o.payment_id
         LEFT JOIN flight  f ON f.id = o.flight_id
         LEFT JOIN hotels  h ON h.id = o.hotel_id;

CREATE OR REPLACE VIEW v_popular_destinations AS
SELECT
    o.destination,
    COUNT(*)           AS total_orders,
    SUM(o.total_price) AS total_revenue,
    AVG(o.total_price) AS avg_order_value
FROM orders o
GROUP BY o.destination;

CREATE OR REPLACE VIEW v_monthly_revenue AS
SELECT
    FORMATDATETIME(o.order_date, 'yyyy-MM') AS year_month,
    SUM(o.total_price)                     AS revenue,
    COUNT(*)                               AS total_orders
FROM orders o
WHERE o.order_date IS NOT NULL
GROUP BY FORMATDATETIME(o.order_date, 'yyyy-MM');

CREATE OR REPLACE VIEW v_flight_load_factor AS
SELECT
    f.id AS flight_id,
    f.airline_name,
    f.ticket_class,
    f.check_in_date,
    f.check_out_date,
    f.number_of_chairs,
    f.seats_available,
    (f.number_of_chairs - f.seats_available) AS seats_booked,
    CASE
        WHEN f.number_of_chairs = 0 THEN NULL
        ELSE ROUND((f.number_of_chairs - f.seats_available) * 100.0 / f.number_of_chairs, 2)
        END AS load_factor_percent
FROM flight f;

-- ============================================================
-- 2) Java-backed UDFs / Stored Procedures (H2 ALIAS)
--    These are callable in SQL like:
--      SELECT FN_TOTAL_REVENUE_PER_USER(1);
--      CALL SP_CREATE_HOTEL_BOOKING(1,2,3, DATE '2026-01-01', DATE '2026-01-03');
-- ============================================================

CREATE ALIAS IF NOT EXISTS FN_ROOM_AVAILABLE
FOR "edu.hust.travelbookingsystem.db.routines.H2Routines.roomAvailable";

CREATE ALIAS IF NOT EXISTS FN_TOTAL_REVENUE_PER_USER
FOR "edu.hust.travelbookingsystem.db.routines.H2Routines.totalRevenuePerUser";

CREATE ALIAS IF NOT EXISTS SP_CREATE_HOTEL_BOOKING
FOR "edu.hust.travelbookingsystem.db.routines.H2Routines.createHotelBooking";

CREATE ALIAS IF NOT EXISTS SP_BOOK_FLIGHT_SEAT
FOR "edu.hust.travelbookingsystem.db.routines.H2Routines.bookFlightSeat";

CREATE ALIAS IF NOT EXISTS SP_CANCEL_BOOKING
FOR "edu.hust.travelbookingsystem.db.routines.H2Routines.cancelBooking";

-- ============================================================
-- 3) Triggers
--    H2 trigger syntax supports one event per trigger.
--    We DROP first to survive restarts.
-- ============================================================

-- 3.1 Prevent overlapping hotel bookings (business integrity)
DROP TRIGGER IF EXISTS trg_hotel_booking_no_overlap_ins;
CREATE TRIGGER trg_hotel_booking_no_overlap_ins
    BEFORE INSERT ON hotel_booking
    FOR EACH ROW
    CALL "edu.hust.travelbookingsystem.db.trigger.HotelBookingNoOverlapTrigger";

DROP TRIGGER IF EXISTS trg_hotel_booking_no_overlap_upd;
CREATE TRIGGER trg_hotel_booking_no_overlap_upd
    BEFORE UPDATE ON hotel_booking
    FOR EACH ROW
    CALL "edu.hust.travelbookingsystem.db.trigger.HotelBookingNoOverlapTrigger";

-- 3.2 Maintain flight.seats_available after seat booking changes
DROP TRIGGER IF EXISTS trg_flight_seats_avail_ins;
CREATE TRIGGER trg_flight_seats_avail_ins
    AFTER INSERT ON flight_seats
    FOR EACH ROW
    CALL "edu.hust.travelbookingsystem.db.trigger.FlightSeatsAvailabilityTrigger";

DROP TRIGGER IF EXISTS trg_flight_seats_avail_upd;
CREATE TRIGGER trg_flight_seats_avail_upd
    AFTER UPDATE ON flight_seats
    FOR EACH ROW
    CALL "edu.hust.travelbookingsystem.db.trigger.FlightSeatsAvailabilityTrigger";

DROP TRIGGER IF EXISTS trg_flight_seats_avail_del;
CREATE TRIGGER trg_flight_seats_avail_del
    AFTER DELETE ON flight_seats
    FOR EACH ROW
    CALL "edu.hust.travelbookingsystem.db.trigger.FlightSeatsAvailabilityTrigger";

-- 3.3 Keep reviews.updated_at correct (DB-side automation)
DROP TRIGGER IF EXISTS trg_reviews_ts_ins;
CREATE TRIGGER trg_reviews_ts_ins
    BEFORE INSERT ON reviews
    FOR EACH ROW
    CALL "edu.hust.travelbookingsystem.db.trigger.ReviewTimestampsTrigger";

DROP TRIGGER IF EXISTS trg_reviews_ts_upd;
CREATE TRIGGER trg_reviews_ts_upd
    BEFORE UPDATE ON reviews
    FOR EACH ROW
    CALL "edu.hust.travelbookingsystem.db.trigger.ReviewTimestampsTrigger";

-- 3.4 Audit triggers (INSERT/UPDATE/DELETE) for key tables
-- Orders
DROP TRIGGER IF EXISTS trg_orders_audit_ins;
CREATE TRIGGER trg_orders_audit_ins AFTER INSERT ON orders FOR EACH ROW
    CALL "edu.hust.travelbookingsystem.db.trigger.AuditTrigger";
DROP TRIGGER IF EXISTS trg_orders_audit_upd;
CREATE TRIGGER trg_orders_audit_upd AFTER UPDATE ON orders FOR EACH ROW
    CALL "edu.hust.travelbookingsystem.db.trigger.AuditTrigger";
DROP TRIGGER IF EXISTS trg_orders_audit_del;
CREATE TRIGGER trg_orders_audit_del AFTER DELETE ON orders FOR EACH ROW
    CALL "edu.hust.travelbookingsystem.db.trigger.AuditTrigger";

-- Payment
DROP TRIGGER IF EXISTS trg_payment_audit_ins;
CREATE TRIGGER trg_payment_audit_ins AFTER INSERT ON payment FOR EACH ROW
    CALL "edu.hust.travelbookingsystem.db.trigger.AuditTrigger";
DROP TRIGGER IF EXISTS trg_payment_audit_upd;
CREATE TRIGGER trg_payment_audit_upd AFTER UPDATE ON payment FOR EACH ROW
    CALL "edu.hust.travelbookingsystem.db.trigger.AuditTrigger";
DROP TRIGGER IF EXISTS trg_payment_audit_del;
CREATE TRIGGER trg_payment_audit_del AFTER DELETE ON payment FOR EACH ROW
    CALL "edu.hust.travelbookingsystem.db.trigger.AuditTrigger";

-- Hotel booking
DROP TRIGGER IF EXISTS trg_hotel_booking_audit_ins;
CREATE TRIGGER trg_hotel_booking_audit_ins AFTER INSERT ON hotel_booking FOR EACH ROW
    CALL "edu.hust.travelbookingsystem.db.trigger.AuditTrigger";
DROP TRIGGER IF EXISTS trg_hotel_booking_audit_upd;
CREATE TRIGGER trg_hotel_booking_audit_upd AFTER UPDATE ON hotel_booking FOR EACH ROW
    CALL "edu.hust.travelbookingsystem.db.trigger.AuditTrigger";
DROP TRIGGER IF EXISTS trg_hotel_booking_audit_del;
CREATE TRIGGER trg_hotel_booking_audit_del AFTER DELETE ON hotel_booking FOR EACH ROW
    CALL "edu.hust.travelbookingsystem.db.trigger.AuditTrigger";
