# Seed Data for Travel Booking System

This folder contains SQL files to populate the database with demo data.

## How to Use

### Option 1: Via H2 Console (Recommended)
1. Start the application: `./mvnw spring-boot:run`
2. Open H2 Console: http://localhost:8080/h2-console
3. Connect with:
   - JDBC URL: `jdbc:h2:file:./data/traveldb`
   - Username: `sa`
   - Password: (leave empty)
4. Run SQL files in order (01, 02, 03, etc.)

### Option 2: Automatic Loading
Add this to `application.properties`:
```properties
spring.sql.init.mode=always
spring.sql.init.data-locations=classpath:seed-data/*.sql
```
Then copy the SQL files to `src/main/resources/seed-data/`

## Files

| File | Description |
|------|-------------|
| `01-payment-status.sql` | Payment status records (PAID, UNPAID, etc.) |
| `02-hotels.sql` | Hotels in Vietnamese destinations |
| `03-hotel-bedrooms.sql` | Room data for each hotel |
| `04-flights.sql` | Flight schedules with airlines |
| `05-users.sql` | Sample user accounts |

## Demo Accounts

| Role | Phone | Password |
|------|-------|----------|
| Admin | 0123456789 | 123456 |
| User | 0901234567 | 123456 |
| User | 0912345678 | 123456 |

## Notes

- Run files in numerical order (01 first, then 02, etc.)
- The admin account is auto-created by `RoleSeeder` on first startup
- All user passwords are "123456" (BCrypt hashed)
- Hotel IDs in `03-hotel-bedrooms.sql` reference `02-hotels.sql`
