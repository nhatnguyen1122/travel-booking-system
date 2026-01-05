# Travel Booking System

A Spring Boot travel booking application with hotel, flight, and trip management features.

## Prerequisites

- **Java 17+** (the project uses Java 25, but 17+ should work)
- **Maven** (or use the included Maven wrapper `./mvnw`)

## Quick Start

### 1. Run the Application

```bash
# Using Maven wrapper (recommended)
./mvnw spring-boot:run

# Or if you have Maven installed
mvn spring-boot:run
```

The application will start at: **http://localhost:8080**

### 2. Access the Application

| URL | Description |
|-----|-------------|
| http://localhost:8080 | Home page |
| http://localhost:8080/user | Login page |
| http://localhost:8080/h2-console | Database console |

### 3. Default Account

| Role | Phone | Password |
|------|-------|----------|
| Admin | 0123456789 | 123456 |

New user accounts can be registered through the application or added via the seed data files.

## Database

This project uses **H2 Database** (in-memory) for easy development without external database setup.

> **Important**: The database uses in-memory mode, so all data is lost when the application stops.

### H2 Console Access

1. Go to: http://localhost:8080/h2-console
2. Connection settings:
   - **JDBC URL**: `jdbc:h2:mem:db`
   - **Username**: `sa`
   - **Password**: *(leave empty)*
3. Click "Connect"

## Features

### User Features
- **Booking**: Create trip bookings with destination, dates, and guest count
- **Hotels**: Browse and select hotels with room options
- **Flights**: View and book flights
- **Payment**: QR code payment with status tracking
- **Reviews**: Rate and review hotels after completing a stay
- **Profile**: Manage user profile and view booking history
- **Forgot Password**: Email-based password reset

### Admin Features
- **Dashboard**: Overview of system statistics
- **User Management**: View and manage user accounts
- **Hotel Management**: Add, edit, delete hotels
- **Room Management**: Manage rooms within each hotel
- **Flight Management**: Manage flight schedules
- **Order Management**: View and manage all bookings

## Project Structure

```
src/main/java/edu/hust/travelbookingsystem/
├── controller/          # REST and web controllers
│   └── admin/          # Admin-specific controllers
├── entity/             # JPA entities
├── enums/              # Enumerations (ErrorCode, PaymentStatus, etc.)
├── exception/          # Custom exceptions
├── model/              # DTOs (request/response)
├── repository/         # Spring Data JPA repositories
├── service/            # Business logic
│   └── implementation/ # Service implementations
└── seeder/             # Initial data seeders

src/main/resources/
├── templates/          # Thymeleaf HTML templates
├── static/
│   ├── css/           # Stylesheets
│   ├── js/            # JavaScript files
│   └── images/        # Static images
└── application.properties
```

## Configuration

### Email (for Password Reset)

Edit `src/main/resources/application.properties`:

```properties
spring.mail.username=your-email@gmail.com
spring.mail.password=your-app-password
app.email.sender=your-email@gmail.com
```

For Gmail, use an [App Password](https://support.google.com/accounts/answer/185833) instead of your regular password.

### Change Server Port

```properties
server.port=8080
```

## Troubleshooting

### Application won't start
- Ensure Java 17+ is installed: `java -version`
- Delete `./data` folder and restart to reset database
- Check if port 8080 is in use: `lsof -i :8080`

### Can't login
- Make sure you're using the correct credentials
- Try resetting the database (delete `./data` folder)
- The `RoleSeeder` auto-creates the admin account on startup

### Database locked error
- Stop all running instances of the application
- Delete `./data/traveldb.mv.db.lock` if it exists
- Restart the application

## Development

### Build the project
```bash
./mvnw clean package
```

### Run tests
```bash
./mvnw test
```

### Skip tests during build
```bash
./mvnw clean package -DskipTests
```

---

Developed by Group 14 - HUST
