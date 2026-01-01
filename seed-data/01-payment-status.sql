-- Payment Status seed data
-- Run this after the application starts and tables are created

INSERT INTO payment (status) VALUES ('PAID');
INSERT INTO payment (status) VALUES ('UNPAID');
INSERT INTO payment (status) VALUES ('VERIFYING');
INSERT INTO payment (status) VALUES ('PAYMENT_FAILED');
