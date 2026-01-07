# Travel Booking System

A comprehensive Spring Boot travel booking application for managing trips to popular Vietnamese destinations. Features include hotel and flight booking with real-time seat selection, AI-powered travel assistant, payment processing, review system, and full admin management panel.

## Table of Contents

- [Features](#features)
- [Technologies](#technologies)
- [Prerequisites](#prerequisites)
- [Quick Start](#quick-start)
- [Configuration](#configuration)
- [Project Structure](#project-structure)
- [Database](#database)
- [API Endpoints](#api-endpoints)
- [AI Chatbot](#ai-chatbot)
- [Development](#development)
- [Troubleshooting](#troubleshooting)

## Features

### User Features

**Trip Planning & Booking**
- Browse destinations: Da Nang, Nha Trang, Phu Quoc, Ha Long, Hoi An, Sapa
- Create trip orders with destination, dates, and guest count
- Search and select hotels with room options
- Browse and book flights with real-time seat selection
- Seat availability checking with optimistic locking
- Booking history with pagination

**Payment System**
- QR code payment interface
- Payment status tracking (UNPAID, VERIFYING, PAID, PAYMENT_FAILED)
- Order total calculation (flights + hotels × nights)
- Payment confirmation workflow

**Reviews & Ratings**
- Leave hotel reviews after stay completion
- 1-5 star rating system with comments
- One review per order constraint

**Account Management**
- User registration with phone and email
- Login with phone number
- Password reset via email (token-based, 24h expiration)
- Password change functionality
- Profile management

**AI Travel Assistant**
- Natural language conversation interface
- RAG-powered responses using knowledge base
- Real-time database queries (hotels, flights, rooms, seats)
- Web search for travel information
- Contextual navigation suggestions
- 20-message conversation history

### Admin Features

**Dashboard & Analytics**
- System statistics overview
- Revenue reports
- Hotel occupancy rates
- User activity metrics

**Management Panels**
- User Management: View, search, activate/deactivate users, create admin accounts
- Hotel Management: Add, edit, delete hotels and rooms
- Flight Management: Add, edit, delete flights and manage seats
- Order Management: View all bookings, advanced search, cancel orders, update payment status
- Contact Management: View customer inquiries, mark as read

## Technologies

### Backend

- **Spring Boot 3.4.3** - Main framework
- **Spring Data JPA** - Data persistence
- **Spring Web** - REST APIs
- **Spring Security Crypto** - Password encryption (BCrypt)
- **Spring Mail** - Email functionality
- **Spring Validation** - Data validation
- **H2 Database** - In-memory database (development)
- **Lombok** - Boilerplate reduction
- **Maven** - Build tool

### AI & Machine Learning

- **LangChain4j 0.36.2** - AI orchestration framework
- **Mistral AI** - LLM provider (mistral-small-latest)
- **AllMiniLM-L6-V2** - Local embedding model for RAG
- **Jsoup 1.17.2** - Web scraping for chatbot searches

### Frontend

- **Thymeleaf** - Server-side templating
- **Vanilla JavaScript** - Client-side interactivity
- **CSS3** - Custom styling (24+ CSS files)
- **Font Awesome** - Icons

### Database Features

- **20+ Stored Procedures** - Complex business logic (booking, payment, reports)
- **Scalar Functions** - Price calculations, validation, formatting
- **Table-Valued Functions** - Advanced search and reporting
- **4 Database Triggers** - Data integrity (seat availability, booking overlap, audit)
- **30+ Indexes** - Query optimization
- **Optimistic Locking** - Concurrency control for seat booking

## Prerequisites

- **Java 17+** (project uses Java 25)
- **Maven 3.6+** (or use included Maven wrapper)
- **Port 8080** available

## Quick Start

### 1. Clone and Navigate

```bash
git clone <repository-url>
cd travel-booking-system
```

### 2. Run the Application

```bash
# Using Maven wrapper (recommended)
./mvnw spring-boot:run

# Or with Maven installed
mvn spring-boot:run
```

or click the **RUN** button in IntelliJ.

The application will start at: **http://localhost:8080**

### 3. Access the Application

| URL | Description |
|-----|-------------|
| http://localhost:8080 | Home page |
| http://localhost:8080/user | Login/Registration |
| http://localhost:8080/chatbot | AI Travel Assistant |
| http://localhost:8080/h2-console | Database Console (Dev) |

### 4. Default Credentials

| Role | Phone | Password |
|------|-------|----------|
| Admin | 0123456789 | 123456 |

New user accounts can be registered through the registration page or created by admins.

## Configuration

### Email Setup (Required for Password Reset)

Edit `src/main/resources/application.properties`:

```properties
spring.mail.username=your-email@gmail.com
spring.mail.password=your-app-password
app.email.sender=your-email@gmail.com
```

For Gmail, generate an [App Password](https://support.google.com/accounts/answer/185833) instead of using your regular password.

### AI Chatbot Configuration

The chatbot uses Mistral AI. Update the API key in `application.properties`:

```properties
mistral.api.key=your-mistral-api-key
mistral.model=mistral-small-latest
```

Get your API key from [Mistral AI Console](https://console.mistral.ai/).

### Database Configuration

**Development (H2 In-Memory)**:
```properties
spring.datasource.url=jdbc:h2:mem:db
spring.datasource.username=sa
spring.datasource.password=
spring.jpa.hibernate.ddl-auto=create-drop
```

**Production (SQL Server)** - Uncomment in pom.xml and configure:
```properties
spring.datasource.url=jdbc:sqlserver://localhost:1433;databaseName=travel_booking
spring.datasource.username=your-username
spring.datasource.password=your-password
spring.jpa.hibernate.ddl-auto=update
```

### Other Settings

```properties
# Server Port
server.port=8080

# Thymeleaf Hot Reload
spring.thymeleaf.cache=false

# Show SQL Queries (Development)
spring.jpa.show-sql=true
```

## Project Structure

```
src/main/java/edu/hust/travelbookingsystem/
├── config/              # Spring configuration
│   ├── ChatbotConfig.java    # AI chatbot setup
│   ├── RoleSeeder.java       # Auto-create admin account
│   └── DataSeeder.java       # Sample data loader
├── controller/          # REST and web controllers
│   ├── admin/          # Admin-specific endpoints
│   ├── UserController.java
│   ├── OrderController.java
│   ├── WebController.java    # Thymeleaf page routes
│   ├── ChatbotController.java
│   └── ReviewController.java
├── entity/              # JPA entities (12 tables)
│   ├── User.java
│   ├── Order.java
│   ├── Hotel.java
│   ├── Flight.java
│   ├── Review.java
│   └── ...
├── repository/          # Spring Data JPA repositories (13)
├── service/             # Business logic layer
│   ├── implementation/  # Service implementations
│   ├── UserService.java
│   ├── OrderService.java
│   ├── ChatbotService.java
│   └── ...
├── model/               # DTOs (Data Transfer Objects)
│   ├── request/        # Request DTOs
│   └── response/       # Response DTOs
├── enums/               # Enumerations
│   ├── RoleCode.java   # ADMIN, USER
│   ├── PaymentStatus.java
│   └── ...
├── exception/           # Custom exceptions
├── db/                  # Database programmability
│   ├── routines/       # Stored procedures & functions
│   │   └── H2Routines.java  # 20+ procedures
│   └── trigger/        # Database triggers (4 triggers)
└── convert/             # Data converters

src/main/resources/
├── templates/           # Thymeleaf HTML templates (26 pages)
│   ├── home.html       # Landing page
│   ├── user.html       # Login/Registration
│   ├── booking.html    # Trip booking
│   ├── hotel.html      # Hotel selection
│   ├── flight.html     # Flight booking with seats
│   ├── chatbot.html    # AI assistant
│   ├── profile.html    # User profile & history
│   ├── admin_*.html    # Admin pages (8 pages)
│   └── ...
├── static/
│   ├── css/            # Stylesheets (24+ files)
│   ├── js/             # JavaScript (25+ files)
│   └── images/         # Static assets
├── knowledge/           # AI chatbot knowledge base
│   └── travel-info.txt
└── application.properties
```

## Database

### Schema Overview

**12 Core Tables:**

- **users** - User accounts (phone, email, password, role)
- **roles** - User roles (ADMIN, USER)
- **orders** - Trip bookings (destination, dates, guests, totals)
- **payment** - Payment status tracking
- **hotels** - Hotel information
- **hotel_bedroom** - Hotel rooms (room number, type, price)
- **hotel_booking** - Hotel reservations
- **flight** - Flight schedules (airline, price, seats)
- **flight_seats** - Individual seat management
- **reviews** - Hotel reviews and ratings
- **contacts** - Customer inquiries
- **password_reset_tokens** - Password reset workflow

### H2 Console Access

1. Navigate to: http://localhost:8080/h2-console
2. Use these settings:
    - **JDBC URL**: `jdbc:h2:mem:db`
    - **Username**: `sa`
    - **Password**: *(leave empty)*
3. Click "Connect"

### Database Features

**Stored Procedures (20+)**:
- `createCompleteBooking()` - Complete booking transaction
- `processPayment()` - Handle payments with loyalty discounts
- `submitReview()` - Validate and create reviews
- `calculateFlightPrice()` - Dynamic pricing
- `hotelOccupancyRate()` - Calculate occupancy
- `generateRevenueReport()` - Business intelligence
- `transferBooking()` - Transfer orders between users
- And many more...

**Database Triggers (4)**:
- `FlightSeatsAvailabilityTrigger` - Auto-update available seats count
- `HotelBookingNoOverlapTrigger` - Prevent double-booking
- `ReviewTimestampsTrigger` - Auto-update timestamps
- `AuditTrigger` - System auditing

**Key Features**:
- Optimistic locking on flights and seats (prevents race conditions)
- 30+ indexes for performance optimization
- Unique constraints (phone, email, flight seats)
- Foreign key relationships with cascading
- Temporal fields with auto-timestamps

## API Endpoints

### User Management

```
POST   /user/create              - Register new user
POST   /user/login               - User authentication
PATCH  /user/changePassword      - Change password
GET    /user/allUsers            - List users (paginated)
GET    /user/searchUser          - Search users
GET    /user/{id}                - Get user by ID
PUT    /user/update/{id}         - Update user profile
PATCH  /user/changeStatus/{id}   - Toggle user status
POST   /user/forgot-password     - Request password reset
GET    /user/validate-reset-token - Validate reset token
POST   /user/reset-password      - Reset password
```

### Order Management

```
POST   /order/create/{userId}                      - Create new order
POST   /order/chooseHotel/{orderId}/{hotelId}      - Select hotel
POST   /order/chooseFlight/{orderId}/{flightId}    - Select flight
POST   /order/chooseFlightWithSeats/{orderId}/{flightId} - Select with seats
GET    /order/{userId}                             - Get user orders
GET    /order/single/{orderId}                     - Get single order
GET    /order/getAllOrder                          - Get all orders (admin)
DELETE /order/{id}                                 - Cancel order
PUT    /order/cancelFlight/{id}                    - Cancel flight only
POST   /order/{orderId}/confirm-payment            - Confirm payment
POST   /order/{orderId}/verifying-payment          - Set verifying status
POST   /order/{orderId}/payment-falled             - Mark payment failed
```

### Other APIs

```
POST   /api/chatbot/message      - Chat with AI assistant
GET    /api/chatbot/welcome      - Get welcome message
POST   /review/submit            - Submit hotel review
POST   /contact/submit           - Submit contact form
GET    /seats/available/{flightId} - Get available seats
```

### Web Pages (26 Routes)

**User Pages**: `/home`, `/user`, `/booking`, `/hotel`, `/flight`, `/profile`, `/review`, `/contact`, `/chatbot`, `/trending`, `/package`, `/services`, `/gallery`, `/news`, `/plan_trip`, `/forgot_password`, `/reset_password`, `/change_password`

**Admin Pages**: `/admin_booking`, `/admin_account`, `/admin_hotel`, `/admin_room`, `/admin_flight`, `/admin_seats`, `/admin_contact`, `/create_employee`

## AI Chatbot

### Features

The AI travel assistant uses RAG (Retrieval-Augmented Generation) architecture combining:
- **Knowledge base** - Curated travel information about Vietnamese destinations
- **Real-time database** - Live hotel, flight, and seat availability
- **Web search** - DuckDuckGo integration for current travel tips

### Tools Available

The chatbot has access to 7 tools:

1. **searchHotels** - Find hotels by destination
2. **getHotelRooms** - Get room details and pricing
3. **searchFlights** - Find available flights
4. **getFlightSeats** - Check seat availability
5. **searchDestinations** - Search travel destinations
6. **webSearch** - Search the web for travel information
7. **calculator** - Calculate travel costs

### Conversation Features

- 20-message conversation history
- Context-aware responses
- Dynamic action buttons for navigation
- Welcome message with quick actions
- Natural language understanding

### Knowledge Base

Located in `src/main/resources/knowledge/travel-info.txt`. Contains information about:
- Popular Vietnamese destinations
- Travel tips and recommendations
- Booking procedures
- Payment methods

## Development

### Build the Project

```bash
# Clean and build
./mvnw clean package

# Skip tests during build
./mvnw clean package -DskipTests
```

### Run Tests

```bash
./mvnw test
```

### Hot Reload

Spring DevTools is included for automatic restart on code changes. Thymeleaf templates reload without restart when `spring.thymeleaf.cache=false`.

### Code Structure

**Layered Architecture**:
1. **Controller Layer** - HTTP endpoints and request handling
2. **Service Layer** - Business logic and transactions
3. **Repository Layer** - Data access with Spring Data JPA
4. **Entity Layer** - Database models
5. **DTO Layer** - Data transfer objects

**Design Patterns**:
- Dependency Injection
- Repository Pattern
- Service Pattern
- DTO Pattern
- Builder Pattern (Lombok)

### Adding New Features

**Example: Add a new destination**

1. Update `DataSeeder.java` to include new hotels and flights
2. Add destination to booking form in `booking.html`
3. Update AI chatbot knowledge base in `knowledge/travel-info.txt`
4. Add destination images to `static/images/`

## Troubleshooting

### Application won't start

- Ensure Java 17+ is installed: `java -version`
- Check if port 8080 is in use:
    - macOS/Linux: `lsof -i :8080`
    - Windows: `netstat -ano | findstr :8080`
- Verify Maven is working: `./mvnw --version`

### Can't login

- Use default admin credentials: Phone `0123456789`, Password `123456`
- The `RoleSeeder` auto-creates the admin account on startup
- Check console logs for seeder execution
- Verify H2 console shows users table with data

### Database errors

- H2 database resets on each restart (in-memory mode)
- All data is lost when application stops
- Check `spring.jpa.hibernate.ddl-auto=create-drop` in properties
- For persistence, switch to file-based H2 or SQL Server

### Email not sending

- Verify Gmail App Password (not regular password)
- Enable "Less secure app access" if needed
- Check spam folder for password reset emails
- Verify SMTP settings in application.properties

### AI Chatbot not responding

- Check Mistral API key is valid
- Verify internet connection for API calls
- Check console logs for API errors
- Ensure `ChatbotConfig.java` is properly initialized

### Seat booking conflicts

- Optimistic locking prevents double-booking
- If you see version conflicts, refresh and retry
- Check `flight_seats` table for seat status
- `FlightSeatsAvailabilityTrigger` auto-updates counts

### Performance issues

- Check if SQL logging is enabled (disable in production)
- Verify indexes are created (check H2 console)
- Use pagination for large result sets
- Monitor console for N+1 query issues

## License

Developed by Group 14 - HUST (Hanoi University of Science and Technology)

## Contributing

This is an educational project. For questions or issues, please contact the development team.

---

**Note**: This application uses an in-memory H2 database for easy development. All data is reset when the application restarts. For production use, configure a persistent database like SQL Server or PostgreSQL.
