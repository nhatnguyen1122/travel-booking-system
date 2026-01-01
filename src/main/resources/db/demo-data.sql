-- Demo Data for Travel Booking System (H2 Database)
-- Run this in H2 Console after your application starts

-- 1. Insert Roles
INSERT INTO roles (id, code, name) VALUES
(1, 'ADMIN', 'Administrator'),
(2, 'USER', 'Regular User'),
(3, 'EMPLOYEE', 'Employee');

-- 2. Insert Users (passwords are hashed)
-- Password for all users: "password123"
INSERT INTO users (id, email, full_name, phone, birthday, password) VALUES
(1, 'admin@hustwonder.com', 'Admin User', '0123456789', '1990-01-01', '$2a$10$8X7QqX7QqX7QqX7QqX7QqO'),
(2, 'john.doe@example.com', 'John Doe', '0987654321', '1995-05-15', '$2a$10$8X7QqX7QqX7QqX7QqX7QqO'),
(3, 'jane.smith@example.com', 'Jane Smith', '0912345678', '1992-08-20', '$2a$10$8X7QqX7QqX7QqX7QqX7QqO'),
(4, 'bob.wilson@example.com', 'Bob Wilson', '0901234567', '1988-03-10', '$2a$10$8X7QqX7QqX7QqX7QqX7QqO');

-- 3. Assign roles to users
INSERT INTO user_roles (user_id, role_id) VALUES
(1, 1), -- Admin has ADMIN role
(2, 2), -- John has USER role
(3, 2), -- Jane has USER role
(4, 2); -- Bob has USER role

-- 4. Insert Payment Methods
INSERT INTO payment (id, payment_name, status) VALUES
(1, 'Credit Card', 'AVAILABLE'),
(2, 'Debit Card', 'AVAILABLE'),
(3, 'PayPal', 'AVAILABLE'),
(4, 'Bank Transfer', 'AVAILABLE');

-- 5. Insert Hotels
INSERT INTO hotel (id, hotel_name, location, description, star_rating, amenities, contact_email, contact_phone, address, city, country, image_url) VALUES
(1, 'Grand Palace Hotel', 'Hanoi', 'Luxury 5-star hotel in the heart of Hanoi', 5, 'WiFi, Pool, Spa, Restaurant, Gym', 'contact@grandpalace.com', '+84-24-1234-5678', '123 Hoan Kiem District', 'Hanoi', 'Vietnam', '/images/hotel1.jpg'),
(2, 'Seaside Resort', 'Da Nang', 'Beautiful beachfront resort with ocean views', 4, 'WiFi, Beach Access, Pool, Restaurant', 'info@seasideresort.com', '+84-236-1234-567', '456 Beach Road', 'Da Nang', 'Vietnam', '/images/hotel2.jpg'),
(3, 'Mountain View Lodge', 'Sapa', 'Cozy mountain lodge with stunning views', 3, 'WiFi, Fireplace, Restaurant, Hiking Tours', 'stay@mountainview.com', '+84-214-1234-567', '789 Mountain Path', 'Sapa', 'Vietnam', '/images/hotel3.jpg'),
(4, 'City Center Inn', 'Ho Chi Minh City', 'Modern hotel in downtown HCMC', 4, 'WiFi, Business Center, Restaurant, Gym', 'booking@citycenter.com', '+84-28-1234-5678', '321 Nguyen Hue Boulevard', 'Ho Chi Minh City', 'Vietnam', '/images/hotel4.jpg');

-- 6. Insert Hotel Bedrooms
INSERT INTO hotel_bedroom (id, hotel_id, room_number, room_type, bed_type, capacity, price_per_night, status, description) VALUES
-- Grand Palace Hotel rooms
(1, 1, '101', 'DELUXE', 'KING', 2, 150.00, 'AVAILABLE', 'Deluxe King Room with city view'),
(2, 1, '102', 'SUITE', 'KING', 2, 250.00, 'AVAILABLE', 'Executive Suite with balcony'),
(3, 1, '201', 'STANDARD', 'DOUBLE', 2, 100.00, 'AVAILABLE', 'Standard Double Room'),
(4, 1, '202', 'DELUXE', 'TWIN', 2, 150.00, 'AVAILABLE', 'Deluxe Twin Room'),
-- Seaside Resort rooms
(5, 2, '101', 'SUITE', 'KING', 2, 200.00, 'AVAILABLE', 'Ocean View Suite'),
(6, 2, '102', 'DELUXE', 'QUEEN', 2, 140.00, 'AVAILABLE', 'Deluxe Queen Room'),
(7, 2, '201', 'STANDARD', 'DOUBLE', 2, 90.00, 'AVAILABLE', 'Standard Room'),
-- Mountain View Lodge rooms
(8, 3, 'A1', 'STANDARD', 'DOUBLE', 2, 80.00, 'AVAILABLE', 'Cozy Mountain Room'),
(9, 3, 'A2', 'DELUXE', 'KING', 2, 110.00, 'AVAILABLE', 'Premium Mountain View'),
-- City Center Inn rooms
(10, 4, '501', 'DELUXE', 'KING', 2, 130.00, 'AVAILABLE', 'Business Class Room'),
(11, 4, '502', 'SUITE', 'KING', 3, 220.00, 'AVAILABLE', 'Junior Suite'),
(12, 4, '601', 'STANDARD', 'TWIN', 2, 95.00, 'AVAILABLE', 'Standard Twin Room');

-- 7. Insert Flights
INSERT INTO flight (id, airline_name, flight_number, departure_city, arrival_city, departure_time, arrival_time, ticket_class, price, available_seats) VALUES
(1, 'Vietnam Airlines', 'VN101', 'Hanoi', 'Ho Chi Minh City', '08:00:00', '10:00:00', 'ECONOMY', 100.00, 150),
(2, 'Vietnam Airlines', 'VN102', 'Ho Chi Minh City', 'Hanoi', '14:00:00', '16:00:00', 'ECONOMY', 100.00, 150),
(3, 'VietJet Air', 'VJ201', 'Hanoi', 'Da Nang', '09:30:00', '10:45:00', 'ECONOMY', 75.00, 180),
(4, 'VietJet Air', 'VJ202', 'Da Nang', 'Hanoi', '17:00:00', '18:15:00', 'ECONOMY', 75.00, 180),
(5, 'Bamboo Airways', 'QH301', 'Hanoi', 'Phu Quoc', '11:00:00', '13:00:00', 'BUSINESS', 200.00, 50),
(6, 'Bamboo Airways', 'QH302', 'Phu Quoc', 'Hanoi', '15:30:00', '17:30:00', 'BUSINESS', 200.00, 50),
(7, 'Vietnam Airlines', 'VN103', 'Ho Chi Minh City', 'Da Nang', '07:00:00', '08:15:00', 'ECONOMY', 85.00, 160),
(8, 'Vietnam Airlines', 'VN104', 'Da Nang', 'Ho Chi Minh City', '19:00:00', '20:15:00', 'ECONOMY', 85.00, 160);

-- 8. Insert Sample Orders with Payments
INSERT INTO orders (id, user_id, destination, number_of_people, checkin_date, checkout_date, total_price, hotel_id, flight_id) VALUES
(1, 2, 'Hanoi', 2, '2026-02-15', '2026-02-18', 550.00, 1, 1),
(2, 3, 'Da Nang', 2, '2026-03-10', '2026-03-15', 870.00, 2, 3),
(3, 4, 'Sapa', 2, '2026-01-20', '2026-01-23', 390.00, 3, NULL),
(4, 2, 'Ho Chi Minh City', 2, '2026-04-01', '2026-04-05', 720.00, 4, 2);

-- 9. Insert Hotel Bookings
INSERT INTO hotel_booking (id, order_id, hotel_id, hotel_bedroom_id, start_date, end_date, number_of_nights, total_price) VALUES
(1, 1, 1, 1, '2026-02-15', '2026-02-18', 3, 450.00),
(2, 2, 2, 5, '2026-03-10', '2026-03-15', 5, 1000.00),
(3, 3, 3, 8, '2026-01-20', '2026-01-23', 3, 240.00),
(4, 4, 4, 10, '2026-04-01', '2026-04-05', 4, 520.00);

-- 10. Insert Payment records for orders
INSERT INTO payment (id, order_id, payment_name, amount, payment_date, status) VALUES
(10, 1, 'Credit Card', 550.00, CURRENT_TIMESTAMP, 'PAID'),
(11, 2, 'PayPal', 870.00, CURRENT_TIMESTAMP, 'PAID'),
(12, 3, 'Bank Transfer', 390.00, CURRENT_TIMESTAMP, 'PAID'),
(13, 4, 'Credit Card', 720.00, CURRENT_TIMESTAMP, 'PENDING');

-- 11. Insert Sample Reviews for completed bookings
INSERT INTO reviews (id, rating, comment, created_at, updated_at, user_id, hotel_id, order_id) VALUES
(1, 5, 'Absolutely amazing hotel! The staff was incredibly friendly and the room was spotless. The city view from our room was breathtaking. Highly recommend!', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 2, 1, 1),
(2, 4, 'Great beachfront location with stunning ocean views. The pool area was fantastic and the restaurant had delicious seafood. Would definitely come back!', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 3, 2, 2),
(3, 5, 'Perfect mountain getaway! The lodge was cozy and warm, with incredible views of the terraced rice fields. The staff helped organize wonderful hiking tours.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 4, 3, 3);

-- Commit all changes
COMMIT;
