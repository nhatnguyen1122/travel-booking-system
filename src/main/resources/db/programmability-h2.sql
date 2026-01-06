/* ============================================================
   Travel Booking System (H2) - Views + Triggers + Routines
   ------------------------------------------------------------
   Database Programmability Features:
   
   1. VIEWS (7 total)
      - Reporting views for analytics
      - Denormalized views for performance
   
   2. STORED PROCEDURES (10 total)
      - Transactional business logic
      - CRUD operations with validation
      - Complex multi-table operations
   
   3. SCALAR FUNCTIONS (10 total)
      - Calculations and validations
      - Business rules
   
   4. TABLE-VALUED FUNCTIONS (4 total)
      - Dynamic queries returning result sets
   
   5. TRIGGERS (12 total)
      - Data integrity enforcement
      - Audit logging
      - Automatic calculations
   
   H2 Notes:
   - Views are standard (CREATE OR REPLACE VIEW)
   - Triggers require Java classes implementing org.h2.api.Trigger
   - Stored procedures & UDFs are done via CREATE ALIAS
   ============================================================ */

-- ============================================================
-- 1) REPORTING VIEWS
-- ============================================================

-- View 1: Comprehensive booking summary with all related data
CREATE OR REPLACE VIEW v_booking_summary AS
SELECT
    o.id                   AS order_id,
    o.order_date,
    u.id                   AS user_id,
    u.full_name            AS user_name,
    u.email                AS user_email,
    u.phone                AS user_phone,
    o.destination,
    o.number_of_people,
    o.total_price,
    p.id                   AS payment_id,
    p.status               AS payment_status,
    f.id                   AS flight_id,
    f.airline_name,
    f.ticket_class,
    f.price                AS flight_price,
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

-- View 2: Popular destinations analytics
CREATE OR REPLACE VIEW v_popular_destinations AS
SELECT
    o.destination,
    COUNT(*)              AS total_orders,
    SUM(o.total_price)    AS total_revenue,
    AVG(o.total_price)    AS avg_order_value,
    COUNT(DISTINCT o.user_id) AS unique_customers,
    MIN(o.order_date)     AS first_booking,
    MAX(o.order_date)     AS last_booking
FROM orders o
GROUP BY o.destination;

-- View 3: Monthly revenue report
CREATE OR REPLACE VIEW v_monthly_revenue AS
SELECT
    FORMATDATETIME(o.order_date, 'yyyy-MM') AS year_month,
    SUM(o.total_price)                      AS revenue,
    COUNT(*)                                AS total_orders,
    COUNT(DISTINCT o.user_id)               AS unique_customers,
    AVG(o.total_price)                      AS avg_order_value
FROM orders o
WHERE o.order_date IS NOT NULL
GROUP BY FORMATDATETIME(o.order_date, 'yyyy-MM')
ORDER BY year_month DESC;

-- View 4: Flight load factor (capacity utilization)
CREATE OR REPLACE VIEW v_flight_load_factor AS
SELECT
    f.id AS flight_id,
    f.airline_name,
    f.ticket_class,
    f.price,
    f.check_in_date,
    f.check_out_date,
    f.number_of_chairs,
    f.seats_available,
    (f.number_of_chairs - f.seats_available) AS seats_booked,
    CASE
        WHEN f.number_of_chairs = 0 THEN NULL
        ELSE ROUND((f.number_of_chairs - f.seats_available) * 100.0 / f.number_of_chairs, 2)
    END AS load_factor_percent,
    CASE
        WHEN f.seats_available = 0 THEN 'SOLD OUT'
        WHEN f.seats_available <= f.number_of_chairs * 0.2 THEN 'ALMOST FULL'
        WHEN f.seats_available <= f.number_of_chairs * 0.5 THEN 'FILLING UP'
        ELSE 'AVAILABLE'
    END AS availability_status
FROM flight f;

-- View 5: Hotel ratings and statistics
CREATE OR REPLACE VIEW v_hotel_ratings AS
SELECT
    h.id AS hotel_id,
    h.hotel_name,
    h.address,
    h.hotel_price AS base_price,
    h.number_floor,
    (SELECT COUNT(*) FROM hotel_bedroom hb WHERE hb.hotel_id = h.id) AS total_rooms,
    COALESCE((SELECT AVG(CAST(r.rating AS DOUBLE)) FROM reviews r WHERE r.hotel_id = h.id), 0) AS avg_rating,
    (SELECT COUNT(*) FROM reviews r WHERE r.hotel_id = h.id) AS review_count,
    (SELECT COUNT(*) FROM orders o WHERE o.hotel_id = h.id) AS booking_count,
    COALESCE((SELECT SUM(o.total_price) FROM orders o 
              JOIN payment p ON o.payment_id = p.id 
              WHERE o.hotel_id = h.id AND p.status = 'PAID'), 0) AS total_revenue
FROM hotels h;

-- View 6: User activity summary
CREATE OR REPLACE VIEW v_user_activity AS
SELECT
    u.id AS user_id,
    u.full_name,
    u.email,
    u.phone,
    u.status AS is_active,
    r.role_code,
    (SELECT COUNT(*) FROM orders o WHERE o.user_id = u.id) AS total_orders,
    (SELECT COALESCE(SUM(o.total_price), 0) FROM orders o 
     JOIN payment p ON o.payment_id = p.id 
     WHERE o.user_id = u.id AND p.status = 'PAID') AS total_spent,
    (SELECT COUNT(*) FROM reviews rv WHERE rv.user_id = u.id) AS reviews_written,
    (SELECT MIN(o.order_date) FROM orders o WHERE o.user_id = u.id) AS first_order,
    (SELECT MAX(o.order_date) FROM orders o WHERE o.user_id = u.id) AS last_order,
    CASE 
        WHEN (SELECT COALESCE(SUM(o.total_price), 0) FROM orders o 
              JOIN payment p ON o.payment_id = p.id 
              WHERE o.user_id = u.id AND p.status = 'PAID') >= 50000000 THEN 'PLATINUM'
        WHEN (SELECT COALESCE(SUM(o.total_price), 0) FROM orders o 
              JOIN payment p ON o.payment_id = p.id 
              WHERE o.user_id = u.id AND p.status = 'PAID') >= 20000000 THEN 'GOLD'
        WHEN (SELECT COALESCE(SUM(o.total_price), 0) FROM orders o 
              JOIN payment p ON o.payment_id = p.id 
              WHERE o.user_id = u.id AND p.status = 'PAID') >= 5000000 THEN 'SILVER'
        ELSE 'BRONZE'
    END AS loyalty_tier
FROM users u
JOIN roles r ON u.role_id = r.id;

-- View 7: Daily booking statistics
CREATE OR REPLACE VIEW v_daily_booking_stats AS
SELECT
    CAST(o.order_date AS DATE) AS booking_date,
    COUNT(*) AS total_bookings,
    SUM(o.total_price) AS total_revenue,
    AVG(o.total_price) AS avg_booking_value,
    SUM(CASE WHEN f.id IS NOT NULL THEN 1 ELSE 0 END) AS flight_bookings,
    SUM(CASE WHEN h.id IS NOT NULL THEN 1 ELSE 0 END) AS hotel_bookings,
    SUM(CASE WHEN f.id IS NOT NULL AND h.id IS NOT NULL THEN 1 ELSE 0 END) AS combo_bookings
FROM orders o
LEFT JOIN flight f ON o.flight_id = f.id
LEFT JOIN hotels h ON o.hotel_id = h.id
WHERE o.order_date IS NOT NULL
GROUP BY CAST(o.order_date AS DATE)
ORDER BY booking_date DESC;


-- ============================================================
-- 2) SCALAR FUNCTIONS (Java-backed via CREATE ALIAS)
-- ============================================================

-- FN_ROOM_AVAILABLE: Check if room is available for date range
CREATE ALIAS IF NOT EXISTS FN_ROOM_AVAILABLE
    FOR "edu.hust.travelbookingsystem.db.routines.H2Routines.roomAvailable";

-- FN_TOTAL_REVENUE_PER_USER: Calculate total revenue from a user
CREATE ALIAS IF NOT EXISTS FN_TOTAL_REVENUE_PER_USER
    FOR "edu.hust.travelbookingsystem.db.routines.H2Routines.totalRevenuePerUser";

-- FN_CALCULATE_FLIGHT_PRICE: Dynamic pricing based on demand
CREATE ALIAS IF NOT EXISTS FN_CALCULATE_FLIGHT_PRICE
    FOR "edu.hust.travelbookingsystem.db.routines.H2Routines.calculateFlightPrice";

-- FN_HOTEL_OCCUPANCY_RATE: Calculate hotel occupancy percentage
CREATE ALIAS IF NOT EXISTS FN_HOTEL_OCCUPANCY_RATE
    FOR "edu.hust.travelbookingsystem.db.routines.H2Routines.hotelOccupancyRate";

-- FN_USER_LOYALTY_POINTS: Calculate loyalty points based on spending
CREATE ALIAS IF NOT EXISTS FN_USER_LOYALTY_POINTS
    FOR "edu.hust.travelbookingsystem.db.routines.H2Routines.userLoyaltyPoints";

-- FN_DAYS_BETWEEN: Calculate days between two dates
CREATE ALIAS IF NOT EXISTS FN_DAYS_BETWEEN
    FOR "edu.hust.travelbookingsystem.db.routines.H2Routines.daysBetween";

-- FN_FORMAT_PRICE_VND: Format price as Vietnamese currency
CREATE ALIAS IF NOT EXISTS FN_FORMAT_PRICE_VND
    FOR "edu.hust.travelbookingsystem.db.routines.H2Routines.formatPriceVnd";

-- FN_VALIDATE_EMAIL: Validate email format
CREATE ALIAS IF NOT EXISTS FN_VALIDATE_EMAIL
    FOR "edu.hust.travelbookingsystem.db.routines.H2Routines.validateEmail";

-- FN_VALIDATE_PHONE_VN: Validate Vietnamese phone format
CREATE ALIAS IF NOT EXISTS FN_VALIDATE_PHONE_VN
    FOR "edu.hust.travelbookingsystem.db.routines.H2Routines.validatePhoneVn";

-- FN_FLIGHT_DURATION_HOURS: Estimate flight duration
CREATE ALIAS IF NOT EXISTS FN_FLIGHT_DURATION_HOURS
    FOR "edu.hust.travelbookingsystem.db.routines.H2Routines.flightDurationHours";


-- ============================================================
-- 3) TABLE-VALUED FUNCTIONS (Return ResultSets)
-- ============================================================

-- FN_GET_AVAILABLE_ROOMS: Get available rooms for hotel in date range
CREATE ALIAS IF NOT EXISTS FN_GET_AVAILABLE_ROOMS
    FOR "edu.hust.travelbookingsystem.db.routines.H2Routines.getAvailableRooms";

-- FN_SEARCH_FLIGHTS: Search flights with filters
CREATE ALIAS IF NOT EXISTS FN_SEARCH_FLIGHTS
    FOR "edu.hust.travelbookingsystem.db.routines.H2Routines.searchFlights";

-- FN_USER_BOOKING_HISTORY: Get user's booking history
CREATE ALIAS IF NOT EXISTS FN_USER_BOOKING_HISTORY
    FOR "edu.hust.travelbookingsystem.db.routines.H2Routines.userBookingHistory";

-- FN_TOP_RATED_HOTELS: Get top-rated hotels
CREATE ALIAS IF NOT EXISTS FN_TOP_RATED_HOTELS
    FOR "edu.hust.travelbookingsystem.db.routines.H2Routines.topRatedHotels";


-- ============================================================
-- 4) STORED PROCEDURES (Business Logic)
-- ============================================================

-- SP_CREATE_HOTEL_BOOKING: Create hotel booking with overlap protection
CREATE ALIAS IF NOT EXISTS SP_CREATE_HOTEL_BOOKING
    FOR "edu.hust.travelbookingsystem.db.routines.H2Routines.createHotelBooking";

-- SP_BOOK_FLIGHT_SEAT: Atomic seat booking
CREATE ALIAS IF NOT EXISTS SP_BOOK_FLIGHT_SEAT
    FOR "edu.hust.travelbookingsystem.db.routines.H2Routines.bookFlightSeat";

-- SP_CANCEL_BOOKING: Cancel booking and free resources
CREATE ALIAS IF NOT EXISTS SP_CANCEL_BOOKING
    FOR "edu.hust.travelbookingsystem.db.routines.H2Routines.cancelBooking";

-- SP_CREATE_COMPLETE_BOOKING: Create complete booking with flight and hotel
CREATE ALIAS IF NOT EXISTS SP_CREATE_COMPLETE_BOOKING
    FOR "edu.hust.travelbookingsystem.db.routines.H2Routines.createCompleteBooking";

-- SP_PROCESS_PAYMENT: Process payment with loyalty discount
CREATE ALIAS IF NOT EXISTS SP_PROCESS_PAYMENT
    FOR "edu.hust.travelbookingsystem.db.routines.H2Routines.processPayment";

-- SP_SUBMIT_REVIEW: Submit hotel review
CREATE ALIAS IF NOT EXISTS SP_SUBMIT_REVIEW
    FOR "edu.hust.travelbookingsystem.db.routines.H2Routines.submitReview";

-- SP_GENERATE_REVENUE_REPORT: Generate revenue statistics
CREATE ALIAS IF NOT EXISTS SP_GENERATE_REVENUE_REPORT
    FOR "edu.hust.travelbookingsystem.db.routines.H2Routines.generateRevenueReport";

-- SP_UPDATE_FLIGHT_SEATS: Update flight capacity
CREATE ALIAS IF NOT EXISTS SP_UPDATE_FLIGHT_SEATS
    FOR "edu.hust.travelbookingsystem.db.routines.H2Routines.updateFlightSeats";

-- SP_TRANSFER_BOOKING: Transfer booking to another user
CREATE ALIAS IF NOT EXISTS SP_TRANSFER_BOOKING
    FOR "edu.hust.travelbookingsystem.db.routines.H2Routines.transferBooking";

-- SP_CLEANUP_EXPIRED_TOKENS: Maintenance procedure
CREATE ALIAS IF NOT EXISTS SP_CLEANUP_EXPIRED_TOKENS
    FOR "edu.hust.travelbookingsystem.db.routines.H2Routines.cleanupExpiredTokens";

-- SP_GET_HOTEL_STATISTICS: Get hotel statistics
CREATE ALIAS IF NOT EXISTS SP_GET_HOTEL_STATISTICS
    FOR "edu.hust.travelbookingsystem.db.routines.H2Routines.getHotelStatistics";


-- ============================================================
-- 5) TRIGGERS (Data Integrity & Automation)
-- ============================================================

-- 5.1 Prevent overlapping hotel bookings (BEFORE INSERT/UPDATE)
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

-- 5.2 Maintain flight.seats_available (AFTER INSERT/UPDATE/DELETE on flight_seats)
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

-- 5.3 Maintain reviews timestamps (BEFORE INSERT/UPDATE)
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

-- 5.4 Audit triggers for orders table
DROP TRIGGER IF EXISTS trg_orders_audit_ins;
CREATE TRIGGER trg_orders_audit_ins AFTER INSERT ON orders FOR EACH ROW
    CALL "edu.hust.travelbookingsystem.db.trigger.AuditTrigger";

DROP TRIGGER IF EXISTS trg_orders_audit_upd;
CREATE TRIGGER trg_orders_audit_upd AFTER UPDATE ON orders FOR EACH ROW
    CALL "edu.hust.travelbookingsystem.db.trigger.AuditTrigger";

DROP TRIGGER IF EXISTS trg_orders_audit_del;
CREATE TRIGGER trg_orders_audit_del AFTER DELETE ON orders FOR EACH ROW
    CALL "edu.hust.travelbookingsystem.db.trigger.AuditTrigger";

-- 5.5 Audit triggers for payment table
DROP TRIGGER IF EXISTS trg_payment_audit_ins;
CREATE TRIGGER trg_payment_audit_ins AFTER INSERT ON payment FOR EACH ROW
    CALL "edu.hust.travelbookingsystem.db.trigger.AuditTrigger";

DROP TRIGGER IF EXISTS trg_payment_audit_upd;
CREATE TRIGGER trg_payment_audit_upd AFTER UPDATE ON payment FOR EACH ROW
    CALL "edu.hust.travelbookingsystem.db.trigger.AuditTrigger";

DROP TRIGGER IF EXISTS trg_payment_audit_del;
CREATE TRIGGER trg_payment_audit_del AFTER DELETE ON payment FOR EACH ROW
    CALL "edu.hust.travelbookingsystem.db.trigger.AuditTrigger";

-- 5.6 Audit triggers for hotel_booking table
DROP TRIGGER IF EXISTS trg_hotel_booking_audit_ins;
CREATE TRIGGER trg_hotel_booking_audit_ins AFTER INSERT ON hotel_booking FOR EACH ROW
    CALL "edu.hust.travelbookingsystem.db.trigger.AuditTrigger";

DROP TRIGGER IF EXISTS trg_hotel_booking_audit_upd;
CREATE TRIGGER trg_hotel_booking_audit_upd AFTER UPDATE ON hotel_booking FOR EACH ROW
    CALL "edu.hust.travelbookingsystem.db.trigger.AuditTrigger";

DROP TRIGGER IF EXISTS trg_hotel_booking_audit_del;
CREATE TRIGGER trg_hotel_booking_audit_del AFTER DELETE ON hotel_booking FOR EACH ROW
    CALL "edu.hust.travelbookingsystem.db.trigger.AuditTrigger";

-- ============================================================
-- 6) VIEWS FOR NEW TABLES
-- ============================================================

-- View 8: Active promotions with usage statistics
CREATE OR REPLACE VIEW v_active_promotions AS
SELECT
    p.id AS promo_id,
    p.promo_code,
    p.promo_name,
    p.discount_type,
    p.discount_value,
    CASE 
        WHEN p.discount_type = 'PERCENTAGE' THEN CONCAT(CAST(p.discount_value AS VARCHAR), '%')
        ELSE CONCAT(CAST(p.discount_value AS VARCHAR), ' VND')
    END AS discount_display,
    p.min_order_value,
    p.max_discount,
    p.start_date,
    p.end_date,
    p.usage_limit,
    p.usage_count,
    CASE 
        WHEN p.usage_limit IS NULL THEN 'Unlimited'
        ELSE CONCAT(CAST((p.usage_limit - p.usage_count) AS VARCHAR), ' remaining')
    END AS availability,
    p.is_active,
    DATEDIFF('DAY', CURRENT_DATE, p.end_date) AS days_until_expiry
FROM promotions p
WHERE p.is_active = TRUE 
  AND p.start_date <= CURRENT_DATE 
  AND p.end_date >= CURRENT_DATE
  AND (p.usage_limit IS NULL OR p.usage_count < p.usage_limit);

-- View 9: Customer loyalty summary
CREATE OR REPLACE VIEW v_customer_loyalty AS
SELECT
    u.id AS user_id,
    u.full_name,
    u.email,
    u.phone,
    COALESCE(lp.points_earned, 0) AS total_points_earned,
    COALESCE(lp.points_spent, 0) AS total_points_spent,
    COALESCE(lp.points_earned - lp.points_spent, 0) AS current_balance,
    CASE 
        WHEN COALESCE(lp.points_earned, 0) >= 100000 THEN 'PLATINUM'
        WHEN COALESCE(lp.points_earned, 0) >= 50000 THEN 'GOLD'
        WHEN COALESCE(lp.points_earned, 0) >= 20000 THEN 'SILVER'
        ELSE 'BRONZE'
    END AS loyalty_tier,
    (SELECT COUNT(*) FROM orders o WHERE o.user_id = u.id) AS total_bookings,
    (SELECT SUM(o.total_price) FROM orders o WHERE o.user_id = u.id) AS lifetime_spending,
    lp.last_updated
FROM users u
LEFT JOIN loyalty_points lp ON lp.user_id = u.id
WHERE u.status = TRUE;

-- View 10: Unread notifications per user
CREATE OR REPLACE VIEW v_unread_notifications AS
SELECT
    n.user_id,
    u.full_name,
    u.email,
    COUNT(*) AS unread_count,
    MIN(n.created_at) AS oldest_unread,
    MAX(n.created_at) AS newest_unread
FROM notifications n
JOIN users u ON u.id = n.user_id
WHERE n.is_read = FALSE
GROUP BY n.user_id, u.full_name, u.email;

-- View 11: Promotion effectiveness report
CREATE OR REPLACE VIEW v_promotion_effectiveness AS
SELECT
    p.id AS promo_id,
    p.promo_code,
    p.promo_name,
    p.discount_type,
    p.discount_value,
    COUNT(pu.id) AS times_used,
    SUM(pu.discount_applied) AS total_discount_given,
    AVG(pu.discount_applied) AS avg_discount_per_use,
    COUNT(DISTINCT pu.user_id) AS unique_users,
    CASE 
        WHEN p.usage_limit IS NOT NULL THEN 
            ROUND((CAST(p.usage_count AS DOUBLE) / p.usage_limit) * 100, 2)
        ELSE NULL
    END AS usage_rate_percent
FROM promotions p
LEFT JOIN promotion_usage pu ON pu.promotion_id = p.id
GROUP BY p.id, p.promo_code, p.promo_name, p.discount_type, p.discount_value, p.usage_limit, p.usage_count;

-- View 12: User activity summary for security monitoring
CREATE OR REPLACE VIEW v_user_activity_summary AS
SELECT
    ual.user_id,
    u.full_name,
    u.email,
    COUNT(*) AS total_activities,
    SUM(CASE WHEN ual.activity_type = 'LOGIN' THEN 1 ELSE 0 END) AS login_count,
    SUM(CASE WHEN ual.activity_type = 'BOOKING_CREATED' THEN 1 ELSE 0 END) AS bookings_created,
    SUM(CASE WHEN ual.activity_type = 'PAYMENT_MADE' THEN 1 ELSE 0 END) AS payments_made,
    SUM(CASE WHEN ual.activity_type = 'REVIEW_SUBMITTED' THEN 1 ELSE 0 END) AS reviews_submitted,
    MIN(ual.created_at) AS first_activity,
    MAX(ual.created_at) AS last_activity,
    COUNT(DISTINCT ual.ip_address) AS unique_ips
FROM user_activity_log ual
LEFT JOIN users u ON u.id = ual.user_id
GROUP BY ual.user_id, u.full_name, u.email;

-- View 13: System configuration as key-value pairs
CREATE OR REPLACE VIEW v_system_settings AS
SELECT
    config_key,
    config_value,
    config_type,
    description,
    is_editable,
    updated_at
FROM system_config
ORDER BY config_key;

-- ============================================================
-- 7) AUDIT TRIGGERS FOR NEW TABLES
-- ============================================================

-- 7.1 Audit triggers for promotions table
DROP TRIGGER IF EXISTS trg_promotions_audit_ins;
CREATE TRIGGER trg_promotions_audit_ins AFTER INSERT ON promotions FOR EACH ROW
    CALL "edu.hust.travelbookingsystem.db.trigger.AuditTrigger";

DROP TRIGGER IF EXISTS trg_promotions_audit_upd;
CREATE TRIGGER trg_promotions_audit_upd AFTER UPDATE ON promotions FOR EACH ROW
    CALL "edu.hust.travelbookingsystem.db.trigger.AuditTrigger";

DROP TRIGGER IF EXISTS trg_promotions_audit_del;
CREATE TRIGGER trg_promotions_audit_del AFTER DELETE ON promotions FOR EACH ROW
    CALL "edu.hust.travelbookingsystem.db.trigger.AuditTrigger";

-- 7.2 Audit triggers for loyalty_points table
DROP TRIGGER IF EXISTS trg_loyalty_points_audit_ins;
CREATE TRIGGER trg_loyalty_points_audit_ins AFTER INSERT ON loyalty_points FOR EACH ROW
    CALL "edu.hust.travelbookingsystem.db.trigger.AuditTrigger";

DROP TRIGGER IF EXISTS trg_loyalty_points_audit_upd;
CREATE TRIGGER trg_loyalty_points_audit_upd AFTER UPDATE ON loyalty_points FOR EACH ROW
    CALL "edu.hust.travelbookingsystem.db.trigger.AuditTrigger";

-- 7.3 Audit triggers for notifications table
DROP TRIGGER IF EXISTS trg_notifications_audit_ins;
CREATE TRIGGER trg_notifications_audit_ins AFTER INSERT ON notifications FOR EACH ROW
    CALL "edu.hust.travelbookingsystem.db.trigger.AuditTrigger";

DROP TRIGGER IF EXISTS trg_notifications_audit_upd;
CREATE TRIGGER trg_notifications_audit_upd AFTER UPDATE ON notifications FOR EACH ROW
    CALL "edu.hust.travelbookingsystem.db.trigger.AuditTrigger";

-- ============================================================
-- End of Programmability Script
-- ============================================================
