-- Sample Users seed data
-- Note: Passwords are BCrypt hashed. All passwords below are "123456"
-- The hash was generated with BCrypt strength 10

-- Password hash for "123456": $2a$10$N9qo8uLOickgx2ZMRZoMy.MqrDqU6xLQ6K3IVwNGGfMmYjQnZGmDu

-- Sample regular users (role_id = 1 is USER, role_id = 2 is ADMIN)
-- Note: Admin user is created by RoleSeeder, these are additional demo users

INSERT INTO users (phone, password, full_name, email, birthday, status, role_id) VALUES
('0901234567', '$2a$10$N9qo8uLOickgx2ZMRZoMy.MqrDqU6xLQ6K3IVwNGGfMmYjQnZGmDu', 'Nguyen Van A', 'nguyenvana@gmail.com', '1990-05-15', true, 1),
('0912345678', '$2a$10$N9qo8uLOickgx2ZMRZoMy.MqrDqU6xLQ6K3IVwNGGfMmYjQnZGmDu', 'Tran Thi B', 'tranthib@gmail.com', '1992-08-20', true, 1),
('0923456789', '$2a$10$N9qo8uLOickgx2ZMRZoMy.MqrDqU6xLQ6K3IVwNGGfMmYjQnZGmDu', 'Le Van C', 'levanc@gmail.com', '1988-12-10', true, 1),
('0934567890', '$2a$10$N9qo8uLOickgx2ZMRZoMy.MqrDqU6xLQ6K3IVwNGGfMmYjQnZGmDu', 'Pham Thi D', 'phamthid@gmail.com', '1995-03-25', true, 1),
('0945678901', '$2a$10$N9qo8uLOickgx2ZMRZoMy.MqrDqU6xLQ6K3IVwNGGfMmYjQnZGmDu', 'Hoang Van E', 'hoangvane@gmail.com', '1991-07-08', true, 1),
('0956789012', '$2a$10$N9qo8uLOickgx2ZMRZoMy.MqrDqU6xLQ6K3IVwNGGfMmYjQnZGmDu', 'Vo Thi F', 'vothif@gmail.com', '1993-11-30', true, 1),
('0967890123', '$2a$10$N9qo8uLOickgx2ZMRZoMy.MqrDqU6xLQ6K3IVwNGGfMmYjQnZGmDu', 'Dang Van G', 'dangvang@gmail.com', '1989-02-14', true, 1),
('0978901234', '$2a$10$N9qo8uLOickgx2ZMRZoMy.MqrDqU6xLQ6K3IVwNGGfMmYjQnZGmDu', 'Bui Thi H', 'buithih@gmail.com', '1994-09-05', true, 1);

-- Inactive user example
INSERT INTO users (phone, password, full_name, email, birthday, status, role_id) VALUES
('0989012345', '$2a$10$N9qo8uLOickgx2ZMRZoMy.MqrDqU6xLQ6K3IVwNGGfMmYjQnZGmDu', 'Nguyen Thi I', 'nguyenthii@gmail.com', '1996-04-18', false, 1);
