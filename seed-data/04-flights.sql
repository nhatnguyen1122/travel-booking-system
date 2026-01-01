-- Flights seed data
-- Vietnamese airlines with routes

-- Vietnam Airlines flights
INSERT INTO flight (ticket_class, airline_name, price, check_in_date, check_out_date, number_of_chairs, seat_available, version) VALUES
('BUSINESS_CLASS', 'Vietnam Airlines', 5500000, '2025-02-01 08:00:00', '2025-02-01 10:30:00', 20, 20, 0),
('NORMAL_CLASS', 'Vietnam Airlines', 2200000, '2025-02-01 08:00:00', '2025-02-01 10:30:00', 150, 150, 0),
('BUSINESS_CLASS', 'Vietnam Airlines', 6000000, '2025-02-05 14:00:00', '2025-02-05 16:30:00', 20, 20, 0),
('NORMAL_CLASS', 'Vietnam Airlines', 2500000, '2025-02-05 14:00:00', '2025-02-05 16:30:00', 150, 150, 0),
('BUSINESS_CLASS', 'Vietnam Airlines', 5800000, '2025-02-10 06:00:00', '2025-02-10 08:30:00', 20, 20, 0),
('NORMAL_CLASS', 'Vietnam Airlines', 2300000, '2025-02-10 06:00:00', '2025-02-10 08:30:00', 150, 150, 0);

-- VietJet Air flights
INSERT INTO flight (ticket_class, airline_name, price, check_in_date, check_out_date, number_of_chairs, seat_available, version) VALUES
('BUSINESS_CLASS', 'VietJet Air', 4000000, '2025-02-02 09:00:00', '2025-02-02 11:00:00', 15, 15, 0),
('NORMAL_CLASS', 'VietJet Air', 1500000, '2025-02-02 09:00:00', '2025-02-02 11:00:00', 180, 180, 0),
('BUSINESS_CLASS', 'VietJet Air', 4200000, '2025-02-07 15:00:00', '2025-02-07 17:00:00', 15, 15, 0),
('NORMAL_CLASS', 'VietJet Air', 1600000, '2025-02-07 15:00:00', '2025-02-07 17:00:00', 180, 180, 0),
('BUSINESS_CLASS', 'VietJet Air', 3800000, '2025-02-12 07:30:00', '2025-02-12 09:30:00', 15, 15, 0),
('NORMAL_CLASS', 'VietJet Air', 1400000, '2025-02-12 07:30:00', '2025-02-12 09:30:00', 180, 180, 0);

-- Bamboo Airways flights
INSERT INTO flight (ticket_class, airline_name, price, check_in_date, check_out_date, number_of_chairs, seat_available, version) VALUES
('BUSINESS_CLASS', 'Bamboo Airways', 4800000, '2025-02-03 10:00:00', '2025-02-03 12:30:00', 18, 18, 0),
('NORMAL_CLASS', 'Bamboo Airways', 1900000, '2025-02-03 10:00:00', '2025-02-03 12:30:00', 160, 160, 0),
('BUSINESS_CLASS', 'Bamboo Airways', 5000000, '2025-02-08 16:00:00', '2025-02-08 18:30:00', 18, 18, 0),
('NORMAL_CLASS', 'Bamboo Airways', 2000000, '2025-02-08 16:00:00', '2025-02-08 18:30:00', 160, 160, 0),
('BUSINESS_CLASS', 'Bamboo Airways', 4600000, '2025-02-15 08:00:00', '2025-02-15 10:30:00', 18, 18, 0),
('NORMAL_CLASS', 'Bamboo Airways', 1800000, '2025-02-15 08:00:00', '2025-02-15 10:30:00', 160, 160, 0);

-- Pacific Airlines flights
INSERT INTO flight (ticket_class, airline_name, price, check_in_date, check_out_date, number_of_chairs, seat_available, version) VALUES
('NORMAL_CLASS', 'Pacific Airlines', 1200000, '2025-02-04 11:00:00', '2025-02-04 13:00:00', 180, 180, 0),
('NORMAL_CLASS', 'Pacific Airlines', 1300000, '2025-02-09 17:00:00', '2025-02-09 19:00:00', 180, 180, 0),
('NORMAL_CLASS', 'Pacific Airlines', 1100000, '2025-02-14 06:30:00', '2025-02-14 08:30:00', 180, 180, 0);
