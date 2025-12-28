IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'idx_orders_user_id' AND object_id = OBJECT_ID('dbo.orders'))
CREATE INDEX idx_orders_user_id ON dbo.orders(user_id)@@

IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'idx_orders_flight_id' AND object_id = OBJECT_ID('dbo.orders'))
CREATE INDEX idx_orders_flight_id ON dbo.orders(flight_id)@@

IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'idx_orders_hotel_id' AND object_id = OBJECT_ID('dbo.orders'))
CREATE INDEX idx_orders_hotel_id ON dbo.orders(hotel_id)@@

IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'idx_orders_destination' AND object_id = OBJECT_ID('dbo.orders'))
CREATE INDEX idx_orders_destination ON dbo.orders(destination)@@

IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'idx_hb_hotel_bedroom_dates' AND object_id = OBJECT_ID('dbo.hotel_booking'))
CREATE INDEX idx_hb_hotel_bedroom_dates ON dbo.hotel_booking(hotel_id, hotel_bedroom_id, start_date, end_date)@@

IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'idx_bedroom_hotel_id' AND object_id = OBJECT_ID('dbo.hotel_bedroom'))
CREATE INDEX idx_bedroom_hotel_id ON dbo.hotel_bedroom(hotel_id)@@

IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = 'idx_payment_status' AND object_id = OBJECT_ID('dbo.payment'))
CREATE INDEX idx_payment_status ON dbo.payment(status)@@

IF NOT EXISTS (SELECT 1 FROM sys.key_constraints WHERE name = 'UQ_users_email')
AND NOT EXISTS (
    SELECT email FROM dbo.users GROUP BY email HAVING COUNT(*) > 1
)
BEGIN
ALTER TABLE dbo.users ADD CONSTRAINT UQ_users_email UNIQUE (email);
END@@

IF NOT EXISTS (
    SELECT 1
    FROM sys.key_constraints
    WHERE name IN ('UQ_roles_roleCode', 'UQ_roles_role_code', 'uq_roles_role_code')
)
AND NOT EXISTS (
    SELECT role_code FROM dbo.roles GROUP BY role_code HAVING COUNT(*) > 1
)
BEGIN
ALTER TABLE dbo.roles ADD CONSTRAINT uq_roles_role_code UNIQUE (role_code);
END@@

IF NOT EXISTS (SELECT 1 FROM sys.key_constraints WHERE name = 'UQ_bedroom_hotel_room')
AND NOT EXISTS (
    SELECT hotel_id, room_number
    FROM dbo.hotel_bedroom
    GROUP BY hotel_id, room_number
    HAVING COUNT(*) > 1
)
BEGIN
ALTER TABLE dbo.hotel_bedroom ADD CONSTRAINT UQ_bedroom_hotel_room UNIQUE (hotel_id, room_number);
END@@

IF NOT EXISTS (SELECT 1 FROM sys.check_constraints WHERE name = 'CK_hotel_booking_date_range')
AND NOT EXISTS (SELECT 1 FROM dbo.hotel_booking WHERE start_date >= end_date)
BEGIN
ALTER TABLE dbo.hotel_booking WITH CHECK
    ADD CONSTRAINT CK_hotel_booking_date_range CHECK (start_date < end_date);
END@@

IF NOT EXISTS (SELECT 1 FROM sys.check_constraints WHERE name = 'CK_orders_total_price')
AND NOT EXISTS (SELECT 1 FROM dbo.orders WHERE total_price < 0)
BEGIN
ALTER TABLE dbo.orders WITH CHECK
    ADD CONSTRAINT CK_orders_total_price CHECK (total_price >= 0);
END@@

IF NOT EXISTS (SELECT 1 FROM sys.check_constraints WHERE name = 'CK_flight_price')
AND NOT EXISTS (SELECT 1 FROM dbo.flight WHERE price < 0)
BEGIN
ALTER TABLE dbo.flight WITH CHECK
    ADD CONSTRAINT CK_flight_price CHECK (price >= 0);
END@@

IF NOT EXISTS (SELECT 1 FROM sys.check_constraints WHERE name = 'CK_flight_seats')
AND NOT EXISTS (
    SELECT 1 FROM dbo.flight
    WHERE [seat_available] < 0 OR [number_of_chairs] < 0 OR [seat_available] > [number_of_chairs]
)
BEGIN
ALTER TABLE dbo.flight WITH CHECK
    ADD CONSTRAINT CK_flight_seats
    CHECK ([seat_available] >= 0 AND [number_of_chairs] >= 0 AND [seat_available] <= [number_of_chairs]);
END@@

IF NOT EXISTS (SELECT 1 FROM sys.check_constraints WHERE name = 'CK_flight_date_range')
AND NOT EXISTS (SELECT 1 FROM dbo.flight WHERE check_in_date >= check_out_date)
BEGIN
ALTER TABLE dbo.flight WITH CHECK
    ADD CONSTRAINT CK_flight_date_range CHECK (check_in_date < check_out_date);
END@@

CREATE OR ALTER VIEW dbo.v_monthly_revenue AS
SELECT
    CONVERT(char(7), o.order_date, 120) AS year_month,
    SUM(o.total_price) AS revenue,
    COUNT(*) AS total_orders
FROM dbo.orders o
GROUP BY CONVERT(char(7), o.order_date, 120)@@

    CREATE OR ALTER FUNCTION dbo.fn_room_available
    (
    @hotel_id BIGINT,
    @hotel_bedroom_id BIGINT,
    @start_date DATE,
    @end_date DATE
    )
    RETURNS BIT
    AS
BEGIN
    DECLARE @cnt INT;

SELECT @cnt = COUNT(1)
FROM dbo.hotel_booking hb
WHERE hb.hotel_id = @hotel_id
  AND hb.hotel_bedroom_id = @hotel_bedroom_id
  AND (@start_date < hb.end_date AND @end_date > hb.start_date);

RETURN CASE WHEN @cnt = 0 THEN 1 ELSE 0 END;
END@@

IF OBJECT_ID('dbo.trg_hotel_booking_no_overlap_ins', 'TR') IS NOT NULL
DROP TRIGGER dbo.trg_hotel_booking_no_overlap_ins@@

CREATE TRIGGER dbo.trg_hotel_booking_no_overlap_ins
ON dbo.hotel_booking
INSTEAD OF INSERT
AS
BEGIN
    SET NOCOUNT ON;

    IF EXISTS (SELECT 1 FROM inserted WHERE start_date >= end_date)
BEGIN
        RAISERROR('Invalid date range (start_date must be < end_date).', 16, 1);
ROLLBACK TRANSACTION;
RETURN;
END
    IF EXISTS (
        SELECT 1
        FROM inserted i
        JOIN dbo.hotel_booking hb
          ON hb.hotel_id = i.hotel_id
         AND hb.hotel_bedroom_id = i.hotel_bedroom_id
         AND (i.start_date < hb.end_date AND i.end_date > hb.start_date)
    )
BEGIN
        RAISERROR('Room is not available (overlapping booking).', 16, 1);
ROLLBACK TRANSACTION;
RETURN;
END

INSERT INTO dbo.hotel_booking(order_id, hotel_id, hotel_bedroom_id, start_date, end_date)
SELECT order_id, hotel_id, hotel_bedroom_id, start_date, end_date
FROM inserted;
END@@

IF OBJECT_ID('dbo.trg_hotel_booking_no_overlap_upd', 'TR') IS NOT NULL
DROP TRIGGER dbo.trg_hotel_booking_no_overlap_upd@@

CREATE TRIGGER dbo.trg_hotel_booking_no_overlap_upd
ON dbo.hotel_booking
INSTEAD OF UPDATE
AS
BEGIN
    SET NOCOUNT ON;

    IF EXISTS (SELECT 1 FROM inserted WHERE start_date >= end_date)
BEGIN
        RAISERROR('Invalid date range (start_date must be < end_date).', 16, 1);
ROLLBACK TRANSACTION;
RETURN;
END

    IF EXISTS (
        SELECT 1
        FROM inserted i
        JOIN dbo.hotel_booking hb
          ON hb.id <> i.id
         AND hb.hotel_id = i.hotel_id
         AND hb.hotel_bedroom_id = i.hotel_bedroom_id
         AND (i.start_date < hb.end_date AND i.end_date > hb.start_date)
    )
BEGIN
        RAISERROR('Room is not available (overlapping booking).', 16, 1);
ROLLBACK TRANSACTION;
RETURN;
END

UPDATE hb
SET hb.order_id = i.order_id,
    hb.hotel_id = i.hotel_id,
    hb.hotel_bedroom_id = i.hotel_bedroom_id,
    hb.start_date = i.start_date,
    hb.end_date = i.end_date
    FROM dbo.hotel_booking hb
    JOIN inserted i ON i.id = hb.id;
END@@

CREATE OR ALTER PROCEDURE dbo.sp_create_hotel_booking
    @order_id BIGINT,
    @hotel_id BIGINT,
    @hotel_bedroom_id BIGINT,
    @start_date DATE,
    @end_date DATE
AS
BEGIN
    SET NOCOUNT ON;

    IF @start_date >= @end_date
BEGIN
        RAISERROR('Invalid date range.', 16, 1);
        RETURN;
END

    IF dbo.fn_room_available(@hotel_id, @hotel_bedroom_id, @start_date, @end_date) = 0
BEGIN
        RAISERROR('Room is not available.', 16, 1);
        RETURN;
END

INSERT INTO dbo.hotel_booking(order_id, hotel_id, hotel_bedroom_id, start_date, end_date)
VALUES (@order_id, @hotel_id, @hotel_bedroom_id, @start_date, @end_date);
END@@
