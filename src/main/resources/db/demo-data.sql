-- =========================================================
-- TRAVEL BOOKING SYSTEM - COMPREHENSIVE DEMO DATA FOR H2
-- =========================================================
-- This script provides seed data for:
-- 1. Roles (ADMIN, USER)
-- 2. Payment status records
-- 3. Hotels with multiple bedrooms
-- 4. Flights with seat inventory
-- 5. Sample users
-- 6. Sample orders (bookings)
-- 7. Hotel bookings
-- 8. Reviews
-- 9. Contacts
--
-- Password for all users: "password123" (BCrypt encoded)
-- =========================================================

-- =========================================================
-- 1) ROLES seed data
-- =========================================================
MERGE INTO roles (role_code) KEY(role_code) VALUES ('USER');
MERGE INTO roles (role_code) KEY(role_code) VALUES ('ADMIN');

-- =========================================================
-- 2) PAYMENT STATUS seed data
-- =========================================================
-- Only one payment record per status (used as lookup/reference)
INSERT INTO payment (status) VALUES ('PAID');
INSERT INTO payment (status) VALUES ('UNPAID');
INSERT INTO payment (status) VALUES ('VERIFYING');
INSERT INTO payment (status) VALUES ('PAYMENT_FAILED');

-- =========================================================
-- 3) HOTELS seed data (21 hotels across 6 destinations)
-- =========================================================

-- Da Nang Hotels (IDs: 1-4)
INSERT INTO hotels (hotel_name, hotel_price, address, number_floor) VALUES
('Vinpearl Resort Da Nang', 2500000, 'Da Nang', 25),
('Furama Resort Da Nang', 3200000, 'Da Nang', 20),
('Hyatt Regency Da Nang', 2800000, 'Da Nang', 18),
('Sheraton Grand Da Nang', 3300000, 'Da Nang', 22);

-- Nha Trang Hotels (IDs: 5-8)
INSERT INTO hotels (hotel_name, hotel_price, address, number_floor) VALUES
('Vinpearl Nha Trang', 2000000, 'Nha Trang', 30),
('InterContinental Nha Trang', 3500000, 'Nha Trang', 28),
('Mia Resort Nha Trang', 1800000, 'Nha Trang', 5),
('Evason Ana Mandara', 2600000, 'Nha Trang', 3);

-- Phu Quoc Hotels (IDs: 9-12)
INSERT INTO hotels (hotel_name, hotel_price, address, number_floor) VALUES
('JW Marriott Phu Quoc', 5000000, 'Phu Quoc', 8),
('Vinpearl Resort Phu Quoc', 2800000, 'Phu Quoc', 12),
('Sol Beach House Phu Quoc', 2200000, 'Phu Quoc', 6),
('Salinda Resort Phu Quoc', 3000000, 'Phu Quoc', 5);

-- Ha Long Hotels (IDs: 13-15)
INSERT INTO hotels (hotel_name, hotel_price, address, number_floor) VALUES
('Vinpearl Ha Long', 2400000, 'Ha Long', 20),
('FLC Grand Hotel Ha Long', 1800000, 'Ha Long', 25),
('Wyndham Legend Ha Long', 2000000, 'Ha Long', 22);

-- Hoi An Hotels (IDs: 16-18)
INSERT INTO hotels (hotel_name, hotel_price, address, number_floor) VALUES
('Four Seasons Hoi An', 6000000, 'Hoi An', 4),
('Anantara Hoi An Resort', 3500000, 'Hoi An', 3),
('Victoria Hoi An Beach Resort', 2200000, 'Hoi An', 5);

-- Sapa Hotels (IDs: 19-21)
INSERT INTO hotels (hotel_name, hotel_price, address, number_floor) VALUES
('Hotel de la Coupole Sapa', 4500000, 'Sapa', 6),
('Silk Path Grand Resort Sapa', 2800000, 'Sapa', 8),
('Topas Ecolodge Sapa', 3200000, 'Sapa', 2);

-- =========================================================
-- 4) HOTEL BEDROOMS seed data
-- =========================================================

-- Vinpearl Resort Da Nang (hotel_id = 1)
INSERT INTO hotel_bedroom (room_number, price, room_type, hotel_id) VALUES
(101, 2500000, 'Standard', 1),
(102, 2500000, 'Standard', 1),
(103, 2500000, 'Standard', 1),
(201, 3500000, 'Deluxe', 1),
(202, 3500000, 'Deluxe', 1),
(301, 5000000, 'Suite', 1),
(401, 8000000, 'Presidential', 1);

-- Furama Resort Da Nang (hotel_id = 2)
INSERT INTO hotel_bedroom (room_number, price, room_type, hotel_id) VALUES
(101, 3200000, 'Standard', 2),
(102, 3200000, 'Standard', 2),
(201, 4500000, 'Deluxe', 2),
(301, 6500000, 'Suite', 2);

-- Hyatt Regency Da Nang (hotel_id = 3)
INSERT INTO hotel_bedroom (room_number, price, room_type, hotel_id) VALUES
(101, 2800000, 'Standard', 3),
(201, 4000000, 'Deluxe', 3),
(301, 5500000, 'Suite', 3);

-- Sheraton Grand Da Nang (hotel_id = 4)
INSERT INTO hotel_bedroom (room_number, price, room_type, hotel_id) VALUES
(101, 2200000, 'Standard', 4),
(102, 2200000, 'Standard', 4),
(201, 3200000, 'Deluxe', 4),
(301, 4800000, 'Suite', 4);

-- Vinpearl Nha Trang (hotel_id = 5)
INSERT INTO hotel_bedroom (room_number, price, room_type, hotel_id) VALUES
(101, 2000000, 'Standard', 5),
(102, 2000000, 'Standard', 5),
(103, 2000000, 'Standard', 5),
(201, 3000000, 'Deluxe', 5),
(301, 4500000, 'Suite', 5);

-- InterContinental Nha Trang (hotel_id = 6)
INSERT INTO hotel_bedroom (room_number, price, room_type, hotel_id) VALUES
(101, 3500000, 'Standard', 6),
(201, 5000000, 'Deluxe', 6),
(301, 7000000, 'Suite', 6),
(401, 12000000, 'Presidential', 6);

-- JW Marriott Phu Quoc (hotel_id = 9)
INSERT INTO hotel_bedroom (room_number, price, room_type, hotel_id) VALUES
(101, 5000000, 'Standard', 9),
(102, 5000000, 'Standard', 9),
(201, 7500000, 'Deluxe', 9),
(301, 10000000, 'Suite', 9),
(401, 20000000, 'Presidential', 9);

-- Vinpearl Resort Phu Quoc (hotel_id = 10)
INSERT INTO hotel_bedroom (room_number, price, room_type, hotel_id) VALUES
(101, 2800000, 'Standard', 10),
(102, 2800000, 'Standard', 10),
(201, 4000000, 'Deluxe', 10),
(301, 6000000, 'Suite', 10);

-- Vinpearl Ha Long (hotel_id = 13)
INSERT INTO hotel_bedroom (room_number, price, room_type, hotel_id) VALUES
(101, 2400000, 'Standard', 13),
(201, 3500000, 'Deluxe', 13),
(301, 5000000, 'Suite', 13);

-- Four Seasons Hoi An (hotel_id = 16)
INSERT INTO hotel_bedroom (room_number, price, room_type, hotel_id) VALUES
(101, 6000000, 'Standard', 16),
(201, 9000000, 'Deluxe', 16),
(301, 15000000, 'Suite', 16),
(401, 25000000, 'Presidential', 16);

-- Hotel de la Coupole Sapa (hotel_id = 19)
INSERT INTO hotel_bedroom (room_number, price, room_type, hotel_id) VALUES
(101, 4500000, 'Standard', 19),
(201, 6500000, 'Deluxe', 19),
(301, 9000000, 'Suite', 19);

-- =========================================================
-- 5) FLIGHTS seed data (21 flights)
-- =========================================================

-- Vietnam Airlines flights
INSERT INTO flight (ticket_class, airline_name, price, check_in_date, check_out_date, number_of_chairs, seats_available, version) VALUES
('BUSINESS_CLASS', 'Vietnam Airlines', 5500000, DATE '2026-02-01', DATE '2026-02-02', 20, 20, 0),
('NORMAL_CLASS',   'Vietnam Airlines', 2200000, DATE '2026-02-01', DATE '2026-02-02', 150, 150, 0),
('BUSINESS_CLASS', 'Vietnam Airlines', 6000000, DATE '2026-02-05', DATE '2026-02-06', 20, 20, 0),
('NORMAL_CLASS',   'Vietnam Airlines', 2500000, DATE '2026-02-05', DATE '2026-02-06', 150, 150, 0),
('BUSINESS_CLASS', 'Vietnam Airlines', 5800000, DATE '2026-02-10', DATE '2026-02-11', 20, 20, 0),
('NORMAL_CLASS',   'Vietnam Airlines', 2300000, DATE '2026-02-10', DATE '2026-02-11', 150, 150, 0);

-- VietJet Air flights
INSERT INTO flight (ticket_class, airline_name, price, check_in_date, check_out_date, number_of_chairs, seats_available, version) VALUES
('BUSINESS_CLASS', 'VietJet Air', 4000000, DATE '2026-02-02', DATE '2026-02-03', 15, 15, 0),
('NORMAL_CLASS',   'VietJet Air', 1500000, DATE '2026-02-02', DATE '2026-02-03', 180, 180, 0),
('BUSINESS_CLASS', 'VietJet Air', 4200000, DATE '2026-02-07', DATE '2026-02-08', 15, 15, 0),
('NORMAL_CLASS',   'VietJet Air', 1600000, DATE '2026-02-07', DATE '2026-02-08', 180, 180, 0),
('BUSINESS_CLASS', 'VietJet Air', 3800000, DATE '2026-02-12', DATE '2026-02-13', 15, 15, 0),
('NORMAL_CLASS',   'VietJet Air', 1400000, DATE '2026-02-12', DATE '2026-02-13', 180, 180, 0);

-- Bamboo Airways flights
INSERT INTO flight (ticket_class, airline_name, price, check_in_date, check_out_date, number_of_chairs, seats_available, version) VALUES
('BUSINESS_CLASS', 'Bamboo Airways', 4800000, DATE '2026-02-03', DATE '2026-02-04', 18, 18, 0),
('NORMAL_CLASS',   'Bamboo Airways', 1900000, DATE '2026-02-03', DATE '2026-02-04', 160, 160, 0),
('BUSINESS_CLASS', 'Bamboo Airways', 5000000, DATE '2026-02-08', DATE '2026-02-09', 18, 18, 0),
('NORMAL_CLASS',   'Bamboo Airways', 2000000, DATE '2026-02-08', DATE '2026-02-09', 160, 160, 0),
('BUSINESS_CLASS', 'Bamboo Airways', 4600000, DATE '2026-02-15', DATE '2026-02-16', 18, 18, 0),
('NORMAL_CLASS',   'Bamboo Airways', 1800000, DATE '2026-02-15', DATE '2026-02-16', 160, 160, 0);

-- Pacific Airlines flights
INSERT INTO flight (ticket_class, airline_name, price, check_in_date, check_out_date, number_of_chairs, seats_available, version) VALUES
('NORMAL_CLASS', 'Pacific Airlines', 1200000, DATE '2026-02-04', DATE '2026-02-05', 180, 180, 0),
('NORMAL_CLASS', 'Pacific Airlines', 1300000, DATE '2026-02-09', DATE '2026-02-10', 180, 180, 0),
('NORMAL_CLASS', 'Pacific Airlines', 1100000, DATE '2026-02-14', DATE '2026-02-15', 180, 180, 0);

-- =========================================================
-- 6) FLIGHT SEATS seed data (for first 3 flights)
-- =========================================================

-- Flight 1: Vietnam Airlines Business (flight_id = 1)
INSERT INTO flight_seats (flight_id, seat_number, is_booked, order_id, version) VALUES
(1, '1A', FALSE, NULL, 0), (1, '1B', FALSE, NULL, 0), (1, '1C', FALSE, NULL, 0), (1, '1D', FALSE, NULL, 0),
(1, '2A', FALSE, NULL, 0), (1, '2B', FALSE, NULL, 0), (1, '2C', FALSE, NULL, 0), (1, '2D', FALSE, NULL, 0),
(1, '3A', FALSE, NULL, 0), (1, '3B', FALSE, NULL, 0), (1, '3C', FALSE, NULL, 0), (1, '3D', FALSE, NULL, 0),
(1, '4A', FALSE, NULL, 0), (1, '4B', FALSE, NULL, 0), (1, '4C', FALSE, NULL, 0), (1, '4D', FALSE, NULL, 0),
(1, '5A', FALSE, NULL, 0), (1, '5B', FALSE, NULL, 0), (1, '5C', FALSE, NULL, 0), (1, '5D', FALSE, NULL, 0);

-- Flight 2: Vietnam Airlines Normal (flight_id = 2) - Sample seats
INSERT INTO flight_seats (flight_id, seat_number, is_booked, order_id, version) VALUES
(2, '1A', FALSE, NULL, 0), (2, '1B', FALSE, NULL, 0), (2, '1C', FALSE, NULL, 0), (2, '1D', FALSE, NULL, 0), (2, '1E', FALSE, NULL, 0), (2, '1F', FALSE, NULL, 0),
(2, '2A', FALSE, NULL, 0), (2, '2B', FALSE, NULL, 0), (2, '2C', FALSE, NULL, 0), (2, '2D', FALSE, NULL, 0), (2, '2E', FALSE, NULL, 0), (2, '2F', FALSE, NULL, 0),
(2, '3A', FALSE, NULL, 0), (2, '3B', FALSE, NULL, 0), (2, '3C', FALSE, NULL, 0), (2, '3D', FALSE, NULL, 0), (2, '3E', FALSE, NULL, 0), (2, '3F', FALSE, NULL, 0),
(2, '4A', FALSE, NULL, 0), (2, '4B', FALSE, NULL, 0), (2, '4C', FALSE, NULL, 0), (2, '4D', FALSE, NULL, 0), (2, '4E', FALSE, NULL, 0), (2, '4F', FALSE, NULL, 0),
(2, '5A', FALSE, NULL, 0), (2, '5B', FALSE, NULL, 0), (2, '5C', FALSE, NULL, 0), (2, '5D', FALSE, NULL, 0), (2, '5E', FALSE, NULL, 0), (2, '5F', FALSE, NULL, 0);

-- Flight 3: Vietnam Airlines Business (flight_id = 3)
INSERT INTO flight_seats (flight_id, seat_number, is_booked, order_id, version) VALUES
(3, '1A', FALSE, NULL, 0), (3, '1B', FALSE, NULL, 0), (3, '1C', FALSE, NULL, 0), (3, '1D', FALSE, NULL, 0),
(3, '2A', FALSE, NULL, 0), (3, '2B', FALSE, NULL, 0), (3, '2C', FALSE, NULL, 0), (3, '2D', FALSE, NULL, 0),
(3, '3A', FALSE, NULL, 0), (3, '3B', FALSE, NULL, 0), (3, '3C', FALSE, NULL, 0), (3, '3D', FALSE, NULL, 0),
(3, '4A', FALSE, NULL, 0), (3, '4B', FALSE, NULL, 0), (3, '4C', FALSE, NULL, 0), (3, '4D', FALSE, NULL, 0),
(3, '5A', FALSE, NULL, 0), (3, '5B', FALSE, NULL, 0), (3, '5C', FALSE, NULL, 0), (3, '5D', FALSE, NULL, 0);

-- =========================================================
-- 7) USERS seed data (10 users)
-- Password: "password123" encoded with BCrypt
-- =========================================================

-- Regular users (role = USER)
INSERT INTO users (phone, password, full_name, email, birthday, status, role_id) VALUES
('0901234567', '$2a$10$1lG3SlnGQtBMe2K1HWFWSulygkHyMsCS..Dq2I5aUEB2sKdredTc2', 'Nguyen Van A', 'nguyenvana@gmail.com', DATE '1990-05-15', TRUE, (SELECT id FROM roles WHERE role_code='USER')),
('0912345678', '$2a$10$1lG3SlnGQtBMe2K1HWFWSulygkHyMsCS..Dq2I5aUEB2sKdredTc2', 'Tran Thi B', 'tranthib@gmail.com', DATE '1992-08-20', TRUE, (SELECT id FROM roles WHERE role_code='USER')),
('0923456789', '$2a$10$1lG3SlnGQtBMe2K1HWFWSulygkHyMsCS..Dq2I5aUEB2sKdredTc2', 'Le Van C', 'levanc@gmail.com', DATE '1988-12-10', TRUE, (SELECT id FROM roles WHERE role_code='USER')),
('0934567890', '$2a$10$1lG3SlnGQtBMe2K1HWFWSulygkHyMsCS..Dq2I5aUEB2sKdredTc2', 'Pham Thi D', 'phamthid@gmail.com', DATE '1995-03-25', TRUE, (SELECT id FROM roles WHERE role_code='USER')),
('0945678901', '$2a$10$1lG3SlnGQtBMe2K1HWFWSulygkHyMsCS..Dq2I5aUEB2sKdredTc2', 'Hoang Van E', 'hoangvane@gmail.com', DATE '1991-07-08', TRUE, (SELECT id FROM roles WHERE role_code='USER')),
('0956789012', '$2a$10$1lG3SlnGQtBMe2K1HWFWSulygkHyMsCS..Dq2I5aUEB2sKdredTc2', 'Vo Thi F', 'vothif@gmail.com', DATE '1993-11-30', TRUE, (SELECT id FROM roles WHERE role_code='USER')),
('0967890123', '$2a$10$1lG3SlnGQtBMe2K1HWFWSulygkHyMsCS..Dq2I5aUEB2sKdredTc2', 'Dang Van G', 'dangvang@gmail.com', DATE '1989-02-14', TRUE, (SELECT id FROM roles WHERE role_code='USER')),
('0978901234', '$2a$10$1lG3SlnGQtBMe2K1HWFWSulygkHyMsCS..Dq2I5aUEB2sKdredTc2', 'Bui Thi H', 'buithih@gmail.com', DATE '1994-09-05', TRUE, (SELECT id FROM roles WHERE role_code='USER'));

-- Inactive user
INSERT INTO users (phone, password, full_name, email, birthday, status, role_id) VALUES
('0989012345', '$2a$10$1lG3SlnGQtBMe2K1HWFWSulygkHyMsCS..Dq2I5aUEB2sKdredTc2', 'Nguyen Thi I', 'nguyenthii@gmail.com', DATE '1996-04-18', FALSE, (SELECT id FROM roles WHERE role_code='USER'));

-- Admin user
INSERT INTO users (phone, password, full_name, email, birthday, status, role_id) VALUES
('0990123456', '$2a$10$1lG3SlnGQtBMe2K1HWFWSulygkHyMsCS..Dq2I5aUEB2sKdredTc2', 'Admin User', 'admin@travelbooking.com', DATE '1985-01-01', TRUE, (SELECT id FROM roles WHERE role_code='ADMIN'));

-- =========================================================
-- 8) ORDERS seed data (6 sample orders)
-- =========================================================

-- Order 1: User 1 - Da Nang trip with flight + hotel (PAID)
INSERT INTO orders (destination, number_of_people, order_date, check_in_date, check_out_date, start_hotel, end_hotel, total_price, user_id, payment_id, flight_id, hotel_id, bedrooms) VALUES
('Da Nang', 2, TIMESTAMP '2026-01-10 10:30:00', TIMESTAMP '2026-02-01 08:00:00', TIMESTAMP '2026-02-02 22:00:00', TIMESTAMP '2026-02-01 14:00:00', TIMESTAMP '2026-02-04 12:00:00', 15500000, 1, 1, 1, 1, '101,102');

-- Order 2: User 2 - Phu Quoc trip (PAID)
INSERT INTO orders (destination, number_of_people, order_date, check_in_date, check_out_date, start_hotel, end_hotel, total_price, user_id, payment_id, flight_id, hotel_id, bedrooms) VALUES
('Phu Quoc', 4, TIMESTAMP '2026-01-12 14:15:00', TIMESTAMP '2026-02-03 09:00:00', TIMESTAMP '2026-02-04 21:00:00', TIMESTAMP '2026-02-03 14:00:00', TIMESTAMP '2026-02-07 12:00:00', 45000000, 2, 1, 13, 9, '101,102');

-- Order 3: User 3 - Nha Trang (UNPAID)
INSERT INTO orders (destination, number_of_people, order_date, check_in_date, check_out_date, start_hotel, end_hotel, total_price, user_id, payment_id, flight_id, hotel_id, bedrooms) VALUES
('Nha Trang', 2, TIMESTAMP '2026-01-15 09:45:00', TIMESTAMP '2026-02-05 07:30:00', TIMESTAMP '2026-02-06 20:00:00', TIMESTAMP '2026-02-05 14:00:00', TIMESTAMP '2026-02-08 12:00:00', 12000000, 3, 2, 3, 5, '101');

-- Order 4: User 4 - Ha Long trip (PAID)
INSERT INTO orders (destination, number_of_people, order_date, check_in_date, check_out_date, start_hotel, end_hotel, total_price, user_id, payment_id, flight_id, hotel_id, bedrooms) VALUES
('Ha Long', 3, TIMESTAMP '2026-01-18 16:20:00', NULL, NULL, TIMESTAMP '2026-02-10 14:00:00', TIMESTAMP '2026-02-12 12:00:00', 8400000, 4, 1, NULL, 13, '101,201');

-- Order 5: User 5 - Hoi An trip (VERIFYING)
INSERT INTO orders (destination, number_of_people, order_date, check_in_date, check_out_date, start_hotel, end_hotel, total_price, user_id, payment_id, flight_id, hotel_id, bedrooms) VALUES
('Hoi An', 2, TIMESTAMP '2026-01-20 11:00:00', TIMESTAMP '2026-02-08 08:00:00', TIMESTAMP '2026-02-09 20:00:00', TIMESTAMP '2026-02-08 14:00:00', TIMESTAMP '2026-02-11 12:00:00', 32000000, 5, 3, 15, 16, '101');

-- Order 6: User 1 - Sapa trip (PAID) - second order
INSERT INTO orders (destination, number_of_people, order_date, check_in_date, check_out_date, start_hotel, end_hotel, total_price, user_id, payment_id, flight_id, hotel_id, bedrooms) VALUES
('Sapa', 2, TIMESTAMP '2026-01-22 13:30:00', NULL, NULL, TIMESTAMP '2026-02-15 14:00:00', TIMESTAMP '2026-02-18 12:00:00', 19500000, 1, 1, NULL, 19, '201');

-- =========================================================
-- 9) HOTEL BOOKINGS seed data
-- =========================================================

-- Hotel booking for Order 1 (Vinpearl Da Nang, rooms 101 and 102)
INSERT INTO hotel_booking (order_id, hotel_id, hotel_bedroom_id, start_date, end_date) VALUES
(1, 1, 1, DATE '2026-02-01', DATE '2026-02-04'),
(1, 1, 2, DATE '2026-02-01', DATE '2026-02-04');

-- Hotel booking for Order 2 (JW Marriott Phu Quoc, rooms 101 and 102)
INSERT INTO hotel_booking (order_id, hotel_id, hotel_bedroom_id, start_date, end_date) VALUES
(2, 9, 27, DATE '2026-02-03', DATE '2026-02-07'),
(2, 9, 28, DATE '2026-02-03', DATE '2026-02-07');

-- Hotel booking for Order 4 (Vinpearl Ha Long, rooms 101 and 201)
INSERT INTO hotel_booking (order_id, hotel_id, hotel_bedroom_id, start_date, end_date) VALUES
(4, 13, 37, DATE '2026-02-10', DATE '2026-02-12'),
(4, 13, 38, DATE '2026-02-10', DATE '2026-02-12');

-- Hotel booking for Order 5 (Four Seasons Hoi An, room 101)
INSERT INTO hotel_booking (order_id, hotel_id, hotel_bedroom_id, start_date, end_date) VALUES
(5, 16, 40, DATE '2026-02-08', DATE '2026-02-11');

-- Hotel booking for Order 6 (Hotel de la Coupole Sapa, room 201)
INSERT INTO hotel_booking (order_id, hotel_id, hotel_bedroom_id, start_date, end_date) VALUES
(6, 19, 45, DATE '2026-02-15', DATE '2026-02-18');

-- =========================================================
-- 10) BOOK FLIGHT SEATS for orders with flights
-- =========================================================

-- Order 1: Book seats 1A, 1B on Flight 1
UPDATE flight_seats SET is_booked = TRUE, order_id = 1 WHERE flight_id = 1 AND seat_number IN ('1A', '1B');

-- Order 2: Book seats 1A, 1B, 1C, 1D on Flight 13
-- First add seats for flight 13
INSERT INTO flight_seats (flight_id, seat_number, is_booked, order_id, version) VALUES
(13, '1A', TRUE, 2, 0), (13, '1B', TRUE, 2, 0), (13, '1C', TRUE, 2, 0), (13, '1D', TRUE, 2, 0),
(13, '2A', FALSE, NULL, 0), (13, '2B', FALSE, NULL, 0), (13, '2C', FALSE, NULL, 0), (13, '2D', FALSE, NULL, 0);

-- Order 3: Book seats 1A, 1B on Flight 3
UPDATE flight_seats SET is_booked = TRUE, order_id = 3 WHERE flight_id = 3 AND seat_number IN ('1A', '1B');

-- Order 5: Book seats on Flight 15
INSERT INTO flight_seats (flight_id, seat_number, is_booked, order_id, version) VALUES
(15, '1A', TRUE, 5, 0), (15, '1B', TRUE, 5, 0),
(15, '2A', FALSE, NULL, 0), (15, '2B', FALSE, NULL, 0), (15, '2C', FALSE, NULL, 0), (15, '2D', FALSE, NULL, 0);

-- =========================================================
-- 11) REVIEWS seed data (for paid orders)
-- =========================================================

-- Review for Order 1 (Vinpearl Da Nang)
INSERT INTO reviews (rating, comment, created_at, updated_at, user_id, hotel_id, order_id) VALUES
(5, 'Excellent service and beautiful beach view! The staff was very friendly and helpful. Will definitely come back.', TIMESTAMP '2026-02-05 10:00:00', TIMESTAMP '2026-02-05 10:00:00', 1, 1, 1);

-- Review for Order 2 (JW Marriott Phu Quoc)
INSERT INTO reviews (rating, comment, created_at, updated_at, user_id, hotel_id, order_id) VALUES
(4, 'Great resort with amazing facilities. The pool area was fantastic. Food could be better but overall a wonderful experience.', TIMESTAMP '2026-02-08 15:30:00', TIMESTAMP '2026-02-08 15:30:00', 2, 9, 2);

-- Review for Order 4 (Vinpearl Ha Long)
INSERT INTO reviews (rating, comment, created_at, updated_at, user_id, hotel_id, order_id) VALUES
(5, 'Breathtaking view of Ha Long Bay! The room was spacious and clean. Highly recommended for families.', TIMESTAMP '2026-02-13 09:00:00', TIMESTAMP '2026-02-13 09:00:00', 4, 13, 4);

-- Review for Order 6 (Hotel de la Coupole Sapa)
INSERT INTO reviews (rating, comment, created_at, updated_at, user_id, hotel_id, order_id) VALUES
(4, 'Unique architecture and beautiful mountain views. The weather was perfect for trekking. A memorable stay.', TIMESTAMP '2026-02-19 11:00:00', TIMESTAMP '2026-02-19 11:00:00', 1, 19, 6);

-- =========================================================
-- 12) CONTACTS seed data (sample inquiries)
-- =========================================================

INSERT INTO contacts (full_name, email, subject, message, created_at, is_read) VALUES
('Nguyen Van Test', 'test@example.com', 'Booking Inquiry', 'I would like to know more about group discounts for 10+ people traveling to Da Nang.', TIMESTAMP '2026-01-05 08:30:00', TRUE),
('Tran Thi Customer', 'customer@gmail.com', 'Cancellation Policy', 'What is your cancellation policy for hotel bookings? I might need to change my travel dates.', TIMESTAMP '2026-01-08 14:00:00', TRUE),
('Le Van Visitor', 'visitor@yahoo.com', 'Payment Options', 'Do you accept international credit cards? I am traveling from abroad.', TIMESTAMP '2026-01-10 10:15:00', FALSE),
('Pham Thi Feedback', 'feedback@hotmail.com', 'Great Service', 'Just wanted to say thank you for the excellent service during my recent trip to Phu Quoc!', TIMESTAMP '2026-01-12 16:45:00', FALSE),
('Hoang Van Question', 'question@gmail.com', 'Special Requests', 'Is it possible to request a room with mountain view for the Sapa hotel?', TIMESTAMP '2026-01-15 09:00:00', FALSE);

-- =========================================================
-- 13) Update flight seats_available based on booked seats
-- =========================================================

UPDATE flight SET seats_available = seats_available - 2 WHERE id = 1;
UPDATE flight SET seats_available = seats_available - 2 WHERE id = 3;
UPDATE flight SET seats_available = seats_available - 2 WHERE id = 15;
UPDATE flight SET seats_available = seats_available - 4 WHERE id = 13;

-- =========================================================
-- 14) PROMOTIONS seed data (discount codes and campaigns)
-- =========================================================

INSERT INTO promotions (promo_code, promo_name, description, discount_type, discount_value, min_order_value, max_discount, start_date, end_date, usage_limit, usage_count, is_active) VALUES
('WELCOME10', 'Welcome Discount', 'Get 10% off on your first booking!', 'PERCENTAGE', 10, 1000000, 500000, DATE '2026-01-01', DATE '2026-12-31', 1000, 45, TRUE),
('SUMMER2026', 'Summer Sale 2026', 'Summer special - 15% off all destinations', 'PERCENTAGE', 15, 5000000, 2000000, DATE '2026-06-01', DATE '2026-08-31', 500, 0, TRUE),
('VIP500K', 'VIP Fixed Discount', 'VIP members get 500,000 VND off', 'FIXED_AMOUNT', 500000, 3000000, NULL, DATE '2026-01-01', DATE '2026-12-31', NULL, 128, TRUE),
('NEWYEAR25', 'New Year Special', '25% off for New Year bookings', 'PERCENTAGE', 25, 10000000, 5000000, DATE '2026-01-01', DATE '2026-01-31', 200, 87, TRUE),
('FLASH100K', 'Flash Sale', 'Quick discount - 100,000 VND off', 'FIXED_AMOUNT', 100000, 500000, NULL, DATE '2026-01-15', DATE '2026-01-20', 100, 100, FALSE),
('DANANG20', 'Da Nang Special', '20% off Da Nang packages', 'PERCENTAGE', 20, 2000000, 1000000, DATE '2026-02-01', DATE '2026-03-31', 300, 0, TRUE),
('LOYALTY15', 'Loyalty Reward', 'For customers with 5+ bookings', 'PERCENTAGE', 15, 0, 1500000, DATE '2026-01-01', DATE '2026-12-31', NULL, 23, TRUE);

-- =========================================================
-- 15) PROMOTION USAGE seed data (track which promos were used)
-- =========================================================

INSERT INTO promotion_usage (promotion_id, order_id, user_id, discount_applied, used_at) VALUES
(1, 1, 1, 500000, TIMESTAMP '2026-01-10 10:35:00'),
(4, 2, 2, 5000000, TIMESTAMP '2026-01-12 14:20:00'),
(3, 3, 3, 500000, TIMESTAMP '2026-01-15 09:50:00');

-- =========================================================
-- 16) LOYALTY POINTS seed data (customer loyalty program)
-- =========================================================

INSERT INTO loyalty_points (user_id, points_earned, points_spent, last_updated) VALUES
(1, 15500, 2000, TIMESTAMP '2026-01-22 13:30:00'),
(2, 45000, 5000, TIMESTAMP '2026-01-12 14:15:00'),
(3, 12000, 0, TIMESTAMP '2026-01-15 09:45:00'),
(4, 8400, 1000, TIMESTAMP '2026-01-18 16:20:00'),
(5, 32000, 10000, TIMESTAMP '2026-01-20 11:00:00'),
(6, 5000, 0, TIMESTAMP '2026-01-05 09:00:00'),
(7, 2500, 500, TIMESTAMP '2026-01-08 14:30:00'),
(8, 7800, 0, TIMESTAMP '2026-01-10 16:00:00');

-- =========================================================
-- 17) LOYALTY TRANSACTIONS seed data (points history)
-- =========================================================

INSERT INTO loyalty_transactions (user_id, order_id, transaction_type, points_amount, description, created_at) VALUES
-- User 1 transactions
(1, 1, 'EARN', 15500, 'Points earned from Da Nang booking', TIMESTAMP '2026-01-10 10:30:00'),
(1, NULL, 'REDEEM', -2000, 'Redeemed for flight upgrade', TIMESTAMP '2026-01-15 14:00:00'),
(1, 6, 'EARN', 19500, 'Points earned from Sapa booking', TIMESTAMP '2026-01-22 13:30:00'),
-- User 2 transactions
(2, 2, 'EARN', 45000, 'Points earned from Phu Quoc booking', TIMESTAMP '2026-01-12 14:15:00'),
(2, NULL, 'REDEEM', -5000, 'Redeemed for spa voucher', TIMESTAMP '2026-01-20 10:00:00'),
-- User 3 transactions
(3, 3, 'EARN', 12000, 'Points earned from Nha Trang booking', TIMESTAMP '2026-01-15 09:45:00'),
-- User 4 transactions
(4, 4, 'EARN', 8400, 'Points earned from Ha Long booking', TIMESTAMP '2026-01-18 16:20:00'),
(4, NULL, 'REDEEM', -1000, 'Redeemed for restaurant discount', TIMESTAMP '2026-01-25 12:00:00'),
-- User 5 transactions
(5, 5, 'EARN', 32000, 'Points earned from Hoi An booking', TIMESTAMP '2026-01-20 11:00:00'),
(5, NULL, 'REDEEM', -10000, 'Redeemed for free night stay', TIMESTAMP '2026-01-28 09:00:00');

-- =========================================================
-- 18) NOTIFICATIONS seed data (user notifications)
-- =========================================================

INSERT INTO notifications (user_id, notification_type, title, message, is_read, created_at, read_at) VALUES
-- User 1 notifications
(1, 'BOOKING_CONFIRMED', 'Booking Confirmed!', 'Your Da Nang trip has been confirmed. Booking reference: #BK100001. Have a great trip!', TRUE, TIMESTAMP '2026-01-10 10:35:00', TIMESTAMP '2026-01-10 10:40:00'),
(1, 'LOYALTY_POINTS', 'Points Earned', 'Congratulations! You earned 15,500 loyalty points from your recent booking.', TRUE, TIMESTAMP '2026-01-10 10:36:00', TIMESTAMP '2026-01-10 11:00:00'),
(1, 'PROMOTION', 'Special Offer Just For You!', 'Use code LOYALTY15 to get 15% off your next booking. Valid until Dec 31, 2026.', FALSE, TIMESTAMP '2026-01-15 09:00:00', NULL),
(1, 'FLIGHT_REMINDER', 'Flight Tomorrow!', 'Reminder: Your flight VN101 to Da Nang departs tomorrow at 08:00 AM.', FALSE, TIMESTAMP '2026-01-31 18:00:00', NULL),
-- User 2 notifications
(2, 'BOOKING_CONFIRMED', 'Phu Quoc Adventure Confirmed', 'Your Phu Quoc booking is confirmed! Get ready for an amazing beach vacation.', TRUE, TIMESTAMP '2026-01-12 14:20:00', TIMESTAMP '2026-01-12 14:25:00'),
(2, 'PAYMENT_RECEIVED', 'Payment Successful', 'We have received your payment of 45,000,000 VND. Thank you!', TRUE, TIMESTAMP '2026-01-12 14:21:00', TIMESTAMP '2026-01-12 14:25:00'),
(2, 'REVIEW_REMINDER', 'How Was Your Stay?', 'We hope you enjoyed Vinpearl Phu Quoc. Please take a moment to leave a review.', FALSE, TIMESTAMP '2026-02-08 10:00:00', NULL),
-- User 3 notifications
(3, 'BOOKING_CONFIRMED', 'Nha Trang Trip Confirmed', 'Your Nha Trang beach getaway is all set! Confirmation #BK100003.', TRUE, TIMESTAMP '2026-01-15 09:50:00', TIMESTAMP '2026-01-15 10:00:00'),
-- User 4 notifications
(4, 'BOOKING_CONFIRMED', 'Ha Long Bay Adventure Ready', 'Your Ha Long Bay cruise booking is confirmed. Get ready for breathtaking views!', TRUE, TIMESTAMP '2026-01-18 16:25:00', TIMESTAMP '2026-01-18 16:30:00'),
-- System notifications
(1, 'SYSTEM', 'System Maintenance Notice', 'Scheduled maintenance on Jan 30, 2026 from 2:00 AM to 4:00 AM. Some services may be unavailable.', FALSE, TIMESTAMP '2026-01-25 10:00:00', NULL),
(2, 'SYSTEM', 'System Maintenance Notice', 'Scheduled maintenance on Jan 30, 2026 from 2:00 AM to 4:00 AM. Some services may be unavailable.', FALSE, TIMESTAMP '2026-01-25 10:00:00', NULL);

-- =========================================================
-- 19) SYSTEM CONFIG seed data (application settings)
-- =========================================================

INSERT INTO system_config (config_key, config_value, config_type, description, is_editable) VALUES
('POINTS_PER_VND', '1000', 'NUMBER', 'How many VND spent to earn 1 loyalty point', TRUE),
('POINTS_TO_VND_RATIO', '100', 'NUMBER', 'Value of 1 loyalty point in VND when redeeming', TRUE),
('MAX_BOOKING_ADVANCE_DAYS', '365', 'NUMBER', 'Maximum days in advance a booking can be made', TRUE),
('MIN_BOOKING_ADVANCE_HOURS', '24', 'NUMBER', 'Minimum hours before flight/hotel for booking', TRUE),
('CANCELLATION_FEE_PERCENT', '10', 'NUMBER', 'Cancellation fee as percentage of total', TRUE),
('FREE_CANCELLATION_HOURS', '48', 'NUMBER', 'Hours before check-in for free cancellation', TRUE),
('DEFAULT_CURRENCY', 'VND', 'STRING', 'Default currency for all prices', FALSE),
('BOOKING_EMAIL_ENABLED', 'true', 'BOOLEAN', 'Whether to send booking confirmation emails', TRUE),
('REVIEW_REMINDER_DAYS', '3', 'NUMBER', 'Days after checkout to send review reminder', TRUE),
('MAX_ROOMS_PER_BOOKING', '5', 'NUMBER', 'Maximum number of rooms in single booking', TRUE),
('SUPPORT_EMAIL', 'support@travelbooking.vn', 'STRING', 'Customer support email address', TRUE),
('SUPPORT_PHONE', '1900-xxxx', 'STRING', 'Customer support hotline', TRUE);

-- =========================================================
-- 20) USER ACTIVITY LOG seed data (security & analytics)
-- =========================================================

INSERT INTO user_activity_log (user_id, activity_type, activity_description, ip_address, user_agent, created_at) VALUES
-- Login activities
(1, 'LOGIN', 'User logged in successfully', '192.168.1.100', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) Chrome/120.0', TIMESTAMP '2026-01-10 10:00:00'),
(2, 'LOGIN', 'User logged in successfully', '192.168.1.101', 'Mozilla/5.0 (iPhone; CPU iPhone OS 17_0) Safari/605.1', TIMESTAMP '2026-01-12 14:00:00'),
(3, 'LOGIN', 'User logged in successfully', '192.168.1.102', 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) Safari/605.1', TIMESTAMP '2026-01-15 09:30:00'),
-- Booking activities
(1, 'BOOKING_CREATED', 'Created booking #1 for Da Nang', '192.168.1.100', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) Chrome/120.0', TIMESTAMP '2026-01-10 10:30:00'),
(2, 'BOOKING_CREATED', 'Created booking #2 for Phu Quoc', '192.168.1.101', 'Mozilla/5.0 (iPhone; CPU iPhone OS 17_0) Safari/605.1', TIMESTAMP '2026-01-12 14:15:00'),
(3, 'BOOKING_CREATED', 'Created booking #3 for Nha Trang', '192.168.1.102', 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) Safari/605.1', TIMESTAMP '2026-01-15 09:45:00'),
-- Payment activities
(1, 'PAYMENT_MADE', 'Payment of 15,500,000 VND processed', '192.168.1.100', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) Chrome/120.0', TIMESTAMP '2026-01-10 10:32:00'),
(2, 'PAYMENT_MADE', 'Payment of 45,000,000 VND processed', '192.168.1.101', 'Mozilla/5.0 (iPhone; CPU iPhone OS 17_0) Safari/605.1', TIMESTAMP '2026-01-12 14:17:00'),
-- Review activities
(1, 'REVIEW_SUBMITTED', 'Submitted 5-star review for Vinpearl Da Nang', '192.168.1.100', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) Chrome/120.0', TIMESTAMP '2026-02-05 10:00:00'),
(2, 'REVIEW_SUBMITTED', 'Submitted 4-star review for Vinpearl Phu Quoc', '192.168.1.101', 'Mozilla/5.0 (iPhone; CPU iPhone OS 17_0) Safari/605.1', TIMESTAMP '2026-02-08 15:30:00'),
-- Profile updates
(5, 'PROFILE_UPDATED', 'Updated phone number and email preferences', '192.168.1.105', 'Mozilla/5.0 (Linux; Android 14) Chrome/120.0', TIMESTAMP '2026-01-18 11:00:00'),
(6, 'PASSWORD_CHANGED', 'Password changed successfully', '192.168.1.106', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) Firefox/121.0', TIMESTAMP '2026-01-20 15:30:00'),
-- Logout activities
(1, 'LOGOUT', 'User logged out', '192.168.1.100', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) Chrome/120.0', TIMESTAMP '2026-01-10 12:00:00'),
(2, 'LOGOUT', 'User logged out', '192.168.1.101', 'Mozilla/5.0 (iPhone; CPU iPhone OS 17_0) Safari/605.1', TIMESTAMP '2026-01-12 16:00:00');

-- =========================================================
-- 21) REVENUE SUMMARY seed data (pre-aggregated for reports)
-- =========================================================

INSERT INTO revenue_summary (summary_date, destination, total_orders, total_revenue, flight_revenue, hotel_revenue, avg_order_value) VALUES
(DATE '2026-01-10', 'Da Nang', 1, 15500000, 5000000, 10500000, 15500000),
(DATE '2026-01-12', 'Phu Quoc', 1, 45000000, 12000000, 33000000, 45000000),
(DATE '2026-01-15', 'Nha Trang', 1, 12000000, 3500000, 8500000, 12000000),
(DATE '2026-01-18', 'Ha Long', 1, 8400000, 0, 8400000, 8400000),
(DATE '2026-01-20', 'Hoi An', 1, 32000000, 8000000, 24000000, 32000000),
(DATE '2026-01-22', 'Sapa', 1, 19500000, 0, 19500000, 19500000);

-- =========================================================
-- END OF DEMO DATA
-- =========================================================
