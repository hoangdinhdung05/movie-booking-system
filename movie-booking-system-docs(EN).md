# 📖 Movie Booking System - Documentation

---

## 1. Overview
The **Movie Booking System** is designed to support end-to-end cinema operations, including user authentication, role-based access control (RBAC), movie & theater management, seat booking, promotions, payments, notifications, and auditing. This documentation provides a detailed explanation of the database schema and how it integrates into the application logic.

---

## 2. Core Components

### 👤 Users & RBAC
- **users**: Stores user information (email, phone, password hash, status).
- **auth_providers**: Supports third-party logins (Google, Facebook, Apple).
- **roles, permissions**: Define access control levels.
- **role_permissions, user_roles**: Many-to-many relationships for RBAC.

### 🎬 Movies & Genres
- **movies**: Core movie details (title, duration, trailer, banner, release status).
- **movie_cast**: Cast and crew mapping.
- **movie_subtitles**: Subtitle support for different languages.
- **genres, movie_genres**: Movie categorization (Action, Comedy, Drama, etc.).

### 🎥 Theaters, Rooms & Seats
- **theaters**: Stores cinema location and facilities.
- **rooms**: Screening rooms (IMAX, 4DX, VIP).
- **seats**: Seat layout within rooms (row, number, type).

### 🕒 Showtimes & Bookings
- **showtimes**: Scheduled movie screenings.
- **seat_reservations**: Temporary seat holds with expiration.
- **bookings**: Stores confirmed reservations.
- **booking_details**: Seat-level details for each booking.
- **payments**: Tracks payment details (method, provider, status).

### 🎟 Promotions
- **promotions**: Discount rules (percentage, fixed amount, buy X get Y).
- **promotion_movies, promotion_theaters, promotion_user_groups**: Constraints for applicability.
- **user_promotions**: Tracks usage history.

### ⭐ Reviews, Notifications & Audit
- **reviews**: User-generated ratings and comments.
- **notifications**: System messages for booking confirmation, reminders, promotions.
- **audit_logs**: Tracks critical system actions for compliance.

### 📊 Statistics & Cache
- **movie_stats, theater_stats, showtime_availability**: Precomputed data for performance optimization.

### ⏱ Scheduler
- **cleanup_expired_reservations**: Removes expired seat reservations.
- **cleanup_expired_bookings**: Cancels unpaid/expired bookings.

---

## 3. Business Flows

### 🎬 Booking Flow
1. User selects a movie & showtime.
2. System returns available seats.
3. User selects seats → reservation created with TTL.
4. User proceeds to payment:
    - On success → booking + details created, reservation confirmed.
    - On failure/timeout → booking canceled, seats released.
5. User receives booking code and confirmation email/SMS.

### 🛠 Admin Flow
- **SUPER_ADMIN / ADMIN**: Manage users, roles, permissions.
- **THEATER_MANAGER**: Manage theaters, rooms, showtimes.
- **STAFF**: Support customer bookings at counters.

### 💳 Promotion Flow
1. User enters promotion code.
2. System validates (validity period, usage limits, applicable movies/theaters).
3. Discount applied to booking, logged in **user_promotions**.

---

## 4. Technical Highlights

### ✅ Key Features
- **RBAC**: Role-based access ensures secure endpoint authorization.
- **Seat Reservation TTL**: Prevents overselling by temporarily locking seats.
- **Precomputed Stats**: Improves reporting performance.
- **Audit Logs**: Tracks user/system actions.
- **Notifications**: Real-time updates for users.

### 🔗 Spring Boot Integration
1. **Spring Security + JWT**
    - Map `users`, `roles`, `permissions` to authentication & authorization.
    - JWT filters handle stateless security.

2. **Spring Data JPA**
    - Entities mapped directly to schema.
    - Repository pattern for CRUD & custom queries.

3. **Spring Scheduler / Quartz**
    - Alternative to MySQL events for cleanup jobs.

4. **Redis Cache (Optional)**
    - Cache frequently accessed data (showtimes, seat availability).
    - Store temporary seat reservations.

5. **Payment Gateway Integration**
    - Connect to providers (MoMo, ZaloPay, Stripe, PayPal).

6. **Notifications**
    - Spring Mail for emails.
    - WebSocket/Firebase for real-time push notifications.

---

## 5. Example Queries

### 🔍 Available Seats for Showtime
```sql
SELECT s.id, s.row_label, s.seat_number
FROM seats s
LEFT JOIN seat_reservations r ON s.id = r.seat_id AND r.status = 'RESERVED'
LEFT JOIN booking_details bd ON s.id = bd.seat_id
WHERE s.room_id = :roomId AND bd.id IS NULL AND r.id IS NULL;
```

### 🔍 Top Movies by Booking Count
```sql
SELECT m.title, COUNT(b.id) AS total_bookings
FROM movies m
JOIN showtimes st ON m.id = st.movie_id
JOIN bookings b ON st.id = b.showtime_id
WHERE b.status = 'CONFIRMED'
GROUP BY m.id
ORDER BY total_bookings DESC
LIMIT 10;
```

---

## 6. Future Improvements
- Multi-currency & multi-language support.
- Loyalty program (reward points for bookings).
- AI-powered recommendation engine.
- Dynamic pricing (based on demand & seat location).

---

## 7. Conclusion
This schema provides a **production-ready foundation** for a Movie Booking System with full coverage of real-world features: authentication, RBAC, booking flow, payments, promotions, notifications, statistics, and auditing. It integrates seamlessly with Spring Boot using Security, JPA, Scheduler, and caching layers.

