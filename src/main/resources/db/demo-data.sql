-- =========================================================
-- DEMO SEED DATA FOR H2
-- =========================================================

-- (Optional nhưng rất nên có) seed roles để users(role_id) không bị fail
-- Dùng MERGE để không bị lỗi nếu RoleSeeder đã tạo trước đó
MERGE INTO roles (role_code) KEY(role_code) VALUES ('USER');
MERGE INTO roles (role_code) KEY(role_code) VALUES ('ADMIN');

-- =========================================================
-- Payment Status seed data
-- =========================================================
INSERT INTO payment (status) VALUES ('PAID');
INSERT INTO payment (status) VALUES ('UNPAID');
INSERT INTO payment (status) VALUES ('VERIFYING');
INSERT INTO payment (status) VALUES ('PAYMENT_FAILED');

-- =========================================================
-- Hotels seed data
-- =========================================================
INSERT INTO hotels (hotel_name, hotel_price, address, number_floor) VALUES
('Vinpearl Resort Da Nang', 2500000, 'Da Nang', 25),
('Furama Resort Da Nang', 3200000, 'Da Nang', 20),
('Hyatt Regency Da Nang', 2800000, 'Da Nang', 18),
('Sheraton Grand Da Nang', 2200000, 'Da Nang', 22);

INSERT INTO hotels (hotel_name, hotel_price, address, number_floor) VALUES
('Vinpearl Nha Trang', 2000000, 'Nha Trang', 30),
('InterContinental Nha Trang', 3500000, 'Nha Trang', 28),
('Mia Resort Nha Trang', 1800000, 'Nha Trang', 5),
('Evason Ana Mandara', 2600000, 'Nha Trang', 3);

INSERT INTO hotels (hotel_name, hotel_price, address, number_floor) VALUES
('JW Marriott Phu Quoc', 5000000, 'Phu Quoc', 8),
('Vinpearl Resort Phu Quoc', 2800000, 'Phu Quoc', 12),
('Sol Beach House Phu Quoc', 2200000, 'Phu Quoc', 6),
('Salinda Resort Phu Quoc', 3000000, 'Phu Quoc', 5);

INSERT INTO hotels (hotel_name, hotel_price, address, number_floor) VALUES
('Vinpearl Ha Long', 2400000, 'Ha Long', 20),
('FLC Grand Hotel Ha Long', 1800000, 'Ha Long', 25),
('Wyndham Legend Ha Long', 2000000, 'Ha Long', 22);

INSERT INTO hotels (hotel_name, hotel_price, address, number_floor) VALUES
('Four Seasons Hoi An', 6000000, 'Hoi An', 4),
('Anantara Hoi An Resort', 3500000, 'Hoi An', 3),
('Victoria Hoi An Beach Resort', 2200000, 'Hoi An', 5);

INSERT INTO hotels (hotel_name, hotel_price, address, number_floor) VALUES
('Hotel de la Coupole Sapa', 4500000, 'Sapa', 6),
('Silk Path Grand Resort Sapa', 2800000, 'Sapa', 8),
('Topas Ecolodge Sapa', 3200000, 'Sapa', 2);

-- =========================================================
-- Hotel Bedrooms seed data
-- =========================================================
INSERT INTO hotel_bedroom (room_number, price, room_type, hotel_id) VALUES
(101, 2500000, 'Standard', 1),
(102, 2500000, 'Standard', 1),
(201, 3500000, 'Deluxe', 1),
(202, 3500000, 'Deluxe', 1),
(301, 5000000, 'Suite', 1),
(401, 8000000, 'Presidential', 1);

INSERT INTO hotel_bedroom (room_number, price, room_type, hotel_id) VALUES
(101, 3200000, 'Standard', 2),
(102, 3200000, 'Standard', 2),
(201, 4500000, 'Deluxe', 2),
(301, 6500000, 'Suite', 2);

INSERT INTO hotel_bedroom (room_number, price, room_type, hotel_id) VALUES
(101, 2800000, 'Standard', 3),
(201, 4000000, 'Deluxe', 3),
(301, 5500000, 'Suite', 3);

INSERT INTO hotel_bedroom (room_number, price, room_type, hotel_id) VALUES
(101, 2200000, 'Standard', 4),
(102, 2200000, 'Standard', 4),
(201, 3200000, 'Deluxe', 4),
(301, 4800000, 'Suite', 4);

INSERT INTO hotel_bedroom (room_number, price, room_type, hotel_id) VALUES
(101, 2000000, 'Standard', 5),
(102, 2000000, 'Standard', 5),
(103, 2000000, 'Standard', 5),
(201, 3000000, 'Deluxe', 5),
(301, 4500000, 'Suite', 5);

INSERT INTO hotel_bedroom (room_number, price, room_type, hotel_id) VALUES
(101, 3500000, 'Standard', 6),
(201, 5000000, 'Deluxe', 6),
(301, 7000000, 'Suite', 6),
(401, 12000000, 'Presidential', 6);

INSERT INTO hotel_bedroom (room_number, price, room_type, hotel_id) VALUES
(101, 5000000, 'Standard', 9),
(102, 5000000, 'Standard', 9),
(201, 7500000, 'Deluxe', 9),
(301, 10000000, 'Suite', 9),
(401, 20000000, 'Presidential', 9);

INSERT INTO hotel_bedroom (room_number, price, room_type, hotel_id) VALUES
(101, 2800000, 'Standard', 10),
(102, 2800000, 'Standard', 10),
(201, 4000000, 'Deluxe', 10),
(301, 6000000, 'Suite', 10);

INSERT INTO hotel_bedroom (room_number, price, room_type, hotel_id) VALUES
(101, 2400000, 'Standard', 13),
(201, 3500000, 'Deluxe', 13),
(301, 5000000, 'Suite', 13);

INSERT INTO hotel_bedroom (room_number, price, room_type, hotel_id) VALUES
(101, 6000000, 'Standard', 16),
(201, 9000000, 'Deluxe', 16),
(301, 15000000, 'Suite', 16),
(401, 25000000, 'Presidential', 16);

INSERT INTO hotel_bedroom (room_number, price, room_type, hotel_id) VALUES
(101, 4500000, 'Standard', 19),
(201, 6500000, 'Deluxe', 19),
(301, 9000000, 'Suite', 19);

-- =========================================================
-- Flights seed data (DATE only, no time)
-- NOTE: check_out_date must be AFTER check_in_date (constraint)
-- =========================================================
INSERT INTO flight (ticket_class, airline_name, price, check_in_date, check_out_date, number_of_chairs, seats_available, version) VALUES
('BUSINESS_CLASS', 'Vietnam Airlines', 5500000, DATE '2026-02-01', DATE '2026-02-02', 20, 20, 0),
('NORMAL_CLASS',   'Vietnam Airlines', 2200000, DATE '2026-02-01', DATE '2026-02-02', 150, 150, 0),
('BUSINESS_CLASS', 'Vietnam Airlines', 6000000, DATE '2026-02-05', DATE '2026-02-06', 20, 20, 0),
('NORMAL_CLASS',   'Vietnam Airlines', 2500000, DATE '2026-02-05', DATE '2026-02-06', 150, 150, 0),
('BUSINESS_CLASS', 'Vietnam Airlines', 5800000, DATE '2026-02-10', DATE '2026-02-11', 20, 20, 0),
('NORMAL_CLASS',   'Vietnam Airlines', 2300000, DATE '2026-02-10', DATE '2026-02-11', 150, 150, 0);

INSERT INTO flight (ticket_class, airline_name, price, check_in_date, check_out_date, number_of_chairs, seats_available, version) VALUES
('BUSINESS_CLASS', 'VietJet Air', 4000000, DATE '2026-02-02', DATE '2026-02-03', 15, 15, 0),
('NORMAL_CLASS',   'VietJet Air', 1500000, DATE '2026-02-02', DATE '2026-02-03', 180, 180, 0),
('BUSINESS_CLASS', 'VietJet Air', 4200000, DATE '2026-02-07', DATE '2026-02-08', 15, 15, 0),
('NORMAL_CLASS',   'VietJet Air', 1600000, DATE '2026-02-07', DATE '2026-02-08', 180, 180, 0),
('BUSINESS_CLASS', 'VietJet Air', 3800000, DATE '2026-02-12', DATE '2026-02-13', 15, 15, 0),
('NORMAL_CLASS',   'VietJet Air', 1400000, DATE '2026-02-12', DATE '2026-02-13', 180, 180, 0);

INSERT INTO flight (ticket_class, airline_name, price, check_in_date, check_out_date, number_of_chairs, seats_available, version) VALUES
('BUSINESS_CLASS', 'Bamboo Airways', 4800000, DATE '2026-02-03', DATE '2026-02-04', 18, 18, 0),
('NORMAL_CLASS',   'Bamboo Airways', 1900000, DATE '2026-02-03', DATE '2026-02-04', 160, 160, 0),
('BUSINESS_CLASS', 'Bamboo Airways', 5000000, DATE '2026-02-08', DATE '2026-02-09', 18, 18, 0),
('NORMAL_CLASS',   'Bamboo Airways', 2000000, DATE '2026-02-08', DATE '2026-02-09', 160, 160, 0),
('BUSINESS_CLASS', 'Bamboo Airways', 4600000, DATE '2026-02-15', DATE '2026-02-16', 18, 18, 0),
('NORMAL_CLASS',   'Bamboo Airways', 1800000, DATE '2026-02-15', DATE '2026-02-16', 160, 160, 0);

INSERT INTO flight (ticket_class, airline_name, price, check_in_date, check_out_date, number_of_chairs, seats_available, version) VALUES
('NORMAL_CLASS', 'Pacific Airlines', 1200000, DATE '2026-02-04', DATE '2026-02-05', 180, 180, 0),
('NORMAL_CLASS', 'Pacific Airlines', 1300000, DATE '2026-02-09', DATE '2026-02-10', 180, 180, 0),
('NORMAL_CLASS', 'Pacific Airlines', 1100000, DATE '2026-02-14', DATE '2026-02-15', 180, 180, 0);

-- =========================================================
-- Sample Users seed data
-- role_id lấy theo role_code (không hardcode 1/2 để khỏi lệch ID)
-- =========================================================
INSERT INTO users (phone, password, full_name, email, birthday, status, role_id) VALUES
('0901234567', '$2a$10$1lG3SlnGQtBMe2K1HWFWSulygkHyMsCS..Dq2I5aUEB2sKdredTc2', 'Nguyen Van A', 'nguyenvana@gmail.com', DATE '1990-05-15', TRUE, (SELECT id FROM roles WHERE role_code='USER')),
('0912345678', '$2a$10$1lG3SlnGQtBMe2K1HWFWSulygkHyMsCS..Dq2I5aUEB2sKdredTc2', 'Tran Thi B',  'tranthib@gmail.com',  DATE '1992-08-20', TRUE, (SELECT id FROM roles WHERE role_code='USER')),
('0923456789', '$2a$10$1lG3SlnGQtBMe2K1HWFWSulygkHyMsCS..Dq2I5aUEB2sKdredTc2', 'Le Van C',    'levanc@gmail.com',    DATE '1988-12-10', TRUE, (SELECT id FROM roles WHERE role_code='USER')),
('0934567890', '$2a$10$1lG3SlnGQtBMe2K1HWFWSulygkHyMsCS..Dq2I5aUEB2sKdredTc2', 'Pham Thi D',  'phamthid@gmail.com',  DATE '1995-03-25', TRUE, (SELECT id FROM roles WHERE role_code='USER')),
('0945678901', '$2a$10$1lG3SlnGQtBMe2K1HWFWSulygkHyMsCS..Dq2I5aUEB2sKdredTc2', 'Hoang Van E', 'hoangvane@gmail.com', DATE '1991-07-08', TRUE, (SELECT id FROM roles WHERE role_code='USER')),
('0956789012', '$2a$10$1lG3SlnGQtBMe2K1HWFWSulygkHyMsCS..Dq2I5aUEB2sKdredTc2', 'Vo Thi F',    'vothif@gmail.com',    DATE '1993-11-30', TRUE, (SELECT id FROM roles WHERE role_code='USER')),
('0967890123', '$2a$10$1lG3SlnGQtBMe2K1HWFWSulygkHyMsCS..Dq2I5aUEB2sKdredTc2', 'Dang Van G',  'dangvang@gmail.com',  DATE '1989-02-14', TRUE, (SELECT id FROM roles WHERE role_code='USER')),
('0978901234', '$2a$10$1lG3SlnGQtBMe2K1HWFWSulygkHyMsCS..Dq2I5aUEB2sKdredTc2', 'Bui Thi H',   'buithih@gmail.com',   DATE '1994-09-05', TRUE, (SELECT id FROM roles WHERE role_code='USER'));

INSERT INTO users (phone, password, full_name, email, birthday, status, role_id) VALUES
('0989012345', '$2a$10$1lG3SlnGQtBMe2K1HWFWSulygkHyMsCS..Dq2I5aUEB2sKdredTc2', 'Nguyen Thi I', 'nguyenthii@gmail.com', DATE '1996-04-18', FALSE, (SELECT id FROM roles WHERE role_code='USER'));
