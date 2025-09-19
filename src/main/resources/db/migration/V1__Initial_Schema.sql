-- ===============================
-- MOVIE BOOKING SYSTEM - COMPLETE SCHEMA
-- ===============================

-- ===============================
-- USERS & RBAC
-- ===============================

CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255),
    name VARCHAR(255) NOT NULL,
    phone VARCHAR(20),
    avatar_url VARCHAR(500),
    date_of_birth DATE,
    email_verified BOOLEAN DEFAULT FALSE,
    phone_verified BOOLEAN DEFAULT FALSE,
    status ENUM('ACTIVE', 'INACTIVE', 'SUSPENDED') DEFAULT 'INACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(36),
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    updated_by VARCHAR(36),
    INDEX idx_email (email),
    INDEX idx_username(username),
    INDEX idx_phone (phone),
    INDEX idx_status (status)
);

CREATE TABLE auth_providers (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    provider VARCHAR(50) NOT NULL, -- GOOGLE, FACEBOOK, APPLE...
    provider_id VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(36),
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    updated_by VARCHAR(36),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    UNIQUE KEY uk_provider (provider, provider_id)
);

CREATE TABLE roles (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) UNIQUE NOT NULL,
    description VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(36),
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    updated_by VARCHAR(36)
);

CREATE TABLE permissions (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) UNIQUE NOT NULL,
    resource VARCHAR(50) NOT NULL,
    action VARCHAR(50) NOT NULL,
    description VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(36),
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    updated_by VARCHAR(36),
    INDEX idx_resource_action (resource, action)
);

CREATE TABLE role_permissions (
    role_id BIGINT,
    permission_id BIGINT,
    PRIMARY KEY (role_id, permission_id),
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE,
    FOREIGN KEY (permission_id) REFERENCES permissions(id) ON DELETE CASCADE
);

CREATE TABLE user_roles (
    user_id BIGINT,
    role_id BIGINT,
    assigned_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    assigned_by BIGINT,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE,
    FOREIGN KEY (assigned_by) REFERENCES users(id)
);

-- ===============================
-- MOVIES & GENRES
-- ===============================

CREATE TABLE movies (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(255) NOT NULL,
    slug VARCHAR(255) UNIQUE NOT NULL,
    description TEXT,
    duration_minutes INT NOT NULL,
    language VARCHAR(50) DEFAULT 'English',
    country VARCHAR(100),
    release_date DATE,
    end_date DATE,
    rating VARCHAR(10),
    imdb_rating DECIMAL(2,1),
    trailer_url VARCHAR(500),
    poster_url VARCHAR(500),
    banner_url VARCHAR(500),
    director VARCHAR(255),
    status ENUM('COMING_SOON', 'NOW_SHOWING', 'ENDED') DEFAULT 'COMING_SOON',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(36),
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    updated_by VARCHAR(36),
    INDEX idx_status (status),
    INDEX idx_release_date (release_date),
    INDEX idx_slug (slug),
    INDEX idx_title (title),
    FULLTEXT idx_search (title, description, director)
);

-- Movie Cast
CREATE TABLE movie_cast (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    movie_id BIGINT NOT NULL,
    actor_name VARCHAR(255) NOT NULL,
    role_name VARCHAR(255),
    FOREIGN KEY (movie_id) REFERENCES movies(id) ON DELETE CASCADE,
    INDEX idx_movie (movie_id),
    INDEX idx_actor (actor_name)
);

-- Movie Subtitles
CREATE TABLE movie_subtitles (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    movie_id BIGINT NOT NULL,
    language VARCHAR(50) NOT NULL,
    FOREIGN KEY (movie_id) REFERENCES movies(id) ON DELETE CASCADE,
    UNIQUE KEY uk_movie_language (movie_id, language)
);

CREATE TABLE genres (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) UNIQUE NOT NULL,
    slug VARCHAR(50) UNIQUE NOT NULL,
    description VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(36),
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    updated_by VARCHAR(36)
);

CREATE TABLE movie_genres (
    movie_id BIGINT,
    genre_id INT,
    PRIMARY KEY (movie_id, genre_id),
    FOREIGN KEY (movie_id) REFERENCES movies(id) ON DELETE CASCADE,
    FOREIGN KEY (genre_id) REFERENCES genres(id) ON DELETE CASCADE
);

-- ===============================
-- THEATERS, ROOMS, SEATS
-- ===============================

CREATE TABLE theaters (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    slug VARCHAR(255) UNIQUE NOT NULL,
    address VARCHAR(500) NOT NULL,
    city VARCHAR(100) NOT NULL,
    district VARCHAR(100),
    latitude DECIMAL(10, 8),
    longitude DECIMAL(11, 8),
    phone VARCHAR(20),
    email VARCHAR(255),
    facilities JSON,
    opening_hours JSON,
    status ENUM('ACTIVE', 'INACTIVE', 'MAINTENANCE', 'CLOSED') DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(36),
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    updated_by VARCHAR(36),
    INDEX idx_city (city),
    INDEX idx_status (status),
    INDEX idx_location (latitude, longitude)
);

CREATE TABLE rooms (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    theater_id BIGINT NOT NULL,
    name VARCHAR(100) NOT NULL,
    capacity INT NOT NULL,
    type ENUM('STANDARD', 'VIP', 'IMAX', '3D', '4DX', 'SCREENX') DEFAULT 'STANDARD',
    room_type ENUM('STANDARD', 'IMAX', 'VIP', '4DX', 'SCREENX') DEFAULT 'STANDARD',
    total_seats INT DEFAULT 0,
    seat_map_version INT DEFAULT 1,
    screen_type VARCHAR(50),
    sound_system VARCHAR(50),
    amenities JSON,
    status ENUM('ACTIVE', 'MAINTENANCE', 'CLOSED') DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(36),
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    updated_by VARCHAR(36),
    FOREIGN KEY (theater_id) REFERENCES theaters(id) ON DELETE CASCADE,
    INDEX idx_theater (theater_id),
    INDEX idx_type (room_type),
    INDEX idx_status (status)
);

CREATE TABLE seats (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    room_id BIGINT NOT NULL,
    seat_number VARCHAR(10) NOT NULL,
    row_label VARCHAR(5),
    row_name VARCHAR(2) NOT NULL,
    type ENUM('REGULAR', 'VIP', 'COUPLE', 'WHEELCHAIR') DEFAULT 'REGULAR',
    seat_type ENUM('REGULAR', 'VIP', 'COUPLE', 'WHEELCHAIR') DEFAULT 'REGULAR',
    status ENUM('AVAILABLE', 'BROKEN') DEFAULT 'AVAILABLE',
    is_active BOOLEAN DEFAULT TRUE,
    position_x INT,
    position_y INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(36),
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    updated_by VARCHAR(36),
    FOREIGN KEY (room_id) REFERENCES rooms(id) ON DELETE CASCADE,
    UNIQUE KEY uk_room_seat (room_id, seat_number),
    UNIQUE KEY uk_room_seat_position (room_id, row_name, seat_number),
    INDEX idx_room (room_id),
    INDEX idx_type (seat_type)
);

-- ===============================
-- SHOWTIMES & BOOKINGS
-- ===============================

CREATE TABLE showtimes (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    movie_id BIGINT NOT NULL,
    room_id BIGINT NOT NULL,
    show_date DATE NOT NULL,
    show_time TIME NOT NULL,
    start_time TIMESTAMP NOT NULL,
    end_time TIMESTAMP NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    base_price DECIMAL(10,2) NOT NULL,
    vip_price DECIMAL(10,2),
    couple_price DECIMAL(10,2),
    available_seats INT NOT NULL,
    total_seats INT NOT NULL,
    booking_open_time TIMESTAMP,
    booking_close_time TIMESTAMP,
    status ENUM('SCHEDULED', 'OPEN', 'FULL', 'ONGOING', 'ENDED', 'CANCELLED', 'FINISHED') DEFAULT 'SCHEDULED',
    version INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(36),
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    updated_by VARCHAR(36),
    FOREIGN KEY (movie_id) REFERENCES movies(id) ON DELETE CASCADE,
    FOREIGN KEY (room_id) REFERENCES rooms(id) ON DELETE CASCADE,
    UNIQUE KEY uk_room_datetime (room_id, show_date, show_time),
    INDEX idx_start_time (start_time),
    INDEX idx_movie_date (movie_id, show_date),
    INDEX idx_room_date (room_id, show_date),
    INDEX idx_date_time (show_date, show_time),
    INDEX idx_status (status)
);

-- Seat Reservations (New from code 2)
CREATE TABLE seat_reservations (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    showtime_id BIGINT NOT NULL,
    seat_id BIGINT NOT NULL,
    user_id BIGINT,
    session_id VARCHAR(255),
    reserved_until TIMESTAMP NOT NULL,
    status ENUM('RESERVED', 'EXPIRED', 'CONFIRMED') DEFAULT 'RESERVED',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (showtime_id) REFERENCES showtimes(id),
    FOREIGN KEY (seat_id) REFERENCES seats(id),
    FOREIGN KEY (user_id) REFERENCES users(id),
    UNIQUE KEY uk_showtime_seat (showtime_id, seat_id),
    INDEX idx_showtime (showtime_id),
    INDEX idx_user (user_id),
    INDEX idx_session (session_id),
    INDEX idx_expiry (reserved_until),
    INDEX idx_status (status)
);

CREATE TABLE bookings (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    showtime_id BIGINT NOT NULL,
    booking_code VARCHAR(12) UNIQUE NOT NULL,
    booking_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    total_price DECIMAL(10,2) NOT NULL,
    total_seats INT NOT NULL,
    total_amount DECIMAL(10,2) NOT NULL,
    discount_amount DECIMAL(10,2) DEFAULT 0,
    final_amount DECIMAL(10,2) NOT NULL,
    booking_fee DECIMAL(10,2) DEFAULT 0,
    payment_method ENUM('CASH', 'CREDIT_CARD', 'DEBIT_CARD', 'MOMO', 'ZALO_PAY', 'VNPAY', 'E_WALLET', 'BANK_TRANSFER') NOT NULL,
    status ENUM('PENDING', 'CONFIRMED', 'CANCELLED', 'EXPIRED', 'REFUNDED', 'USED') DEFAULT 'PENDING',
    booking_status ENUM('PENDING', 'CONFIRMED', 'CANCELLED', 'REFUNDED', 'USED') DEFAULT 'PENDING',
    payment_status ENUM('PENDING', 'PAID', 'FAILED', 'REFUNDED') DEFAULT 'PENDING',
    notes TEXT,
    booking_source ENUM('WEB', 'MOBILE_APP', 'PHONE', 'COUNTER') DEFAULT 'WEB',
    payment_time TIMESTAMP NULL,
    expiry_time TIMESTAMP NOT NULL,
    used_time TIMESTAMP NULL,
    cancelled_time TIMESTAMP NULL,
    cancellation_reason TEXT,
    refund_amount DECIMAL(10,2),
    refund_time TIMESTAMP NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(36),
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    updated_by VARCHAR(36),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (showtime_id) REFERENCES showtimes(id) ON DELETE CASCADE,
    INDEX idx_user (user_id),
    INDEX idx_showtime (showtime_id),
    INDEX idx_booking_code (booking_code),
    INDEX idx_status (booking_status),
    INDEX idx_payment_status (payment_status),
    INDEX idx_booking_time (booking_time),
    INDEX idx_expiry (expiry_time)
);

CREATE TABLE booking_seats (
    booking_id BIGINT,
    seat_id BIGINT,
    price DECIMAL(10,2) NOT NULL,
    status ENUM('BOOKED', 'CANCELLED') DEFAULT 'BOOKED',
    reserved_until TIMESTAMP,
    PRIMARY KEY (booking_id, seat_id),
    FOREIGN KEY (booking_id) REFERENCES bookings(id) ON DELETE CASCADE,
    FOREIGN KEY (seat_id) REFERENCES seats(id) ON DELETE CASCADE
);

-- Booking Details (Enhanced from code 2)
CREATE TABLE booking_details (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    booking_id BIGINT NOT NULL,
    seat_id BIGINT NOT NULL,
    seat_type ENUM('REGULAR', 'VIP', 'COUPLE', 'WHEELCHAIR') NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    row_name VARCHAR(2) NOT NULL,
    seat_number INT NOT NULL,
    FOREIGN KEY (booking_id) REFERENCES bookings(id) ON DELETE CASCADE,
    FOREIGN KEY (seat_id) REFERENCES seats(id),
    UNIQUE KEY uk_booking_seat (booking_id, seat_id),
    INDEX idx_booking (booking_id)
);

-- ===============================
-- PAYMENTS & PROMOTIONS
-- ===============================

CREATE TABLE payments (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    booking_id BIGINT NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    method ENUM('CASH', 'CREDIT_CARD', 'DEBIT_CARD', 'MOMO', 'ZALO_PAY', 'VNPAY', 'E_WALLET', 'BANK_TRANSFER') NOT NULL,
    payment_method ENUM('CREDIT_CARD', 'DEBIT_CARD', 'E_WALLET', 'BANK_TRANSFER', 'CASH') NOT NULL,
    payment_provider VARCHAR(50),
    currency VARCHAR(3) DEFAULT 'VND',
    status ENUM('PENDING', 'SUCCESS', 'FAILED', 'REFUNDED', 'CANCELLED') DEFAULT 'PENDING',
    transaction_id VARCHAR(100),
    provider_transaction_id VARCHAR(255),
    gateway_response JSON,
    failure_reason TEXT,
    payment_time TIMESTAMP,
    processed_at TIMESTAMP NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(36),
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    updated_by VARCHAR(36),
    FOREIGN KEY (booking_id) REFERENCES bookings(id) ON DELETE CASCADE,
    INDEX idx_booking (booking_id),
    INDEX idx_transaction (transaction_id),
    INDEX idx_provider_transaction (provider_transaction_id),
    INDEX idx_status (status),
    INDEX idx_processed_at (processed_at),
    INDEX idx_booking_status (booking_id, status)
);

CREATE TABLE promotions (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    code VARCHAR(50) UNIQUE NOT NULL,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(255),
    discount_type ENUM('PERCENTAGE', 'FIXED_AMOUNT', 'BUY_X_GET_Y') NOT NULL,
    discount_percent DECIMAL(5,2),
    discount_amount DECIMAL(10,2),
    discount_value DECIMAL(10,2) NOT NULL,
    min_amount DECIMAL(10,2),
    max_discount DECIMAL(10,2),
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    valid_from TIMESTAMP NOT NULL,
    valid_until TIMESTAMP NOT NULL,
    status ENUM('ACTIVE', 'EXPIRED', 'INACTIVE') DEFAULT 'ACTIVE',
    usage_limit INT,
    usage_per_user INT DEFAULT 1,
    used_count INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(36),
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    updated_by VARCHAR(36),
    INDEX idx_code (code),
    INDEX idx_status (status),
    INDEX idx_validity (valid_from, valid_until)
);

CREATE TABLE promotion_users (
    promotion_id BIGINT,
    user_id BIGINT,
    used_time TIMESTAMP,
    PRIMARY KEY (promotion_id, user_id),
    FOREIGN KEY (promotion_id) REFERENCES promotions(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE promotion_movies (
    promotion_id BIGINT,
    movie_id BIGINT,
    PRIMARY KEY (promotion_id, movie_id),
    FOREIGN KEY (promotion_id) REFERENCES promotions(id) ON DELETE CASCADE,
    FOREIGN KEY (movie_id) REFERENCES movies(id) ON DELETE CASCADE
);

-- New promotion tables from code 2
CREATE TABLE promotion_theaters (
    promotion_id BIGINT,
    theater_id BIGINT,
    PRIMARY KEY (promotion_id, theater_id),
    FOREIGN KEY (promotion_id) REFERENCES promotions(id) ON DELETE CASCADE,
    FOREIGN KEY (theater_id) REFERENCES theaters(id) ON DELETE CASCADE
);

CREATE TABLE promotion_user_groups (
    promotion_id BIGINT,
    group_name VARCHAR(50),
    PRIMARY KEY (promotion_id, group_name),
    FOREIGN KEY (promotion_id) REFERENCES promotions(id) ON DELETE CASCADE
);

-- Enhanced user promotion usage
CREATE TABLE user_promotions (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    promotion_id BIGINT NOT NULL,
    booking_id BIGINT,
    used_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (promotion_id) REFERENCES promotions(id),
    FOREIGN KEY (booking_id) REFERENCES bookings(id),
    INDEX idx_user_promotion (user_id, promotion_id)
);

-- ===============================
-- REVIEWS
-- ===============================

CREATE TABLE reviews (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    movie_id BIGINT NOT NULL,
    booking_id BIGINT,
    rating INT CHECK (rating >= 1 AND rating <= 10),
    title VARCHAR(255),
    comment TEXT,
    content TEXT,
    is_verified BOOLEAN DEFAULT FALSE,
    helpful_count INT DEFAULT 0,
    status ENUM('ACTIVE', 'HIDDEN', 'FLAGGED') DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(36),
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    updated_by VARCHAR(36),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (movie_id) REFERENCES movies(id) ON DELETE CASCADE,
    FOREIGN KEY (booking_id) REFERENCES bookings(id),
    UNIQUE KEY uk_user_movie_booking (user_id, movie_id, booking_id),
    INDEX idx_movie (movie_id),
    INDEX idx_user (user_id),
    INDEX idx_rating (rating),
    INDEX idx_created (created_at)
);

-- ===============================
-- NOTIFICATIONS
-- ===============================

CREATE TABLE notifications (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    title VARCHAR(255) NOT NULL,
    content TEXT NOT NULL,
    message TEXT NOT NULL,
    type ENUM('SYSTEM', 'PROMOTION', 'BOOKING', 'PAYMENT', 'BOOKING_CONFIRMATION', 'PAYMENT_SUCCESS', 'BOOKING_REMINDER') NOT NULL,
    data JSON,
    status ENUM('SENT', 'READ') DEFAULT 'SENT',
    is_read BOOLEAN DEFAULT FALSE,
    sent_at TIMESTAMP NULL,
    read_at TIMESTAMP NULL,
    expires_at TIMESTAMP NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(36),
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    updated_by VARCHAR(36),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_user_unread (user_id, is_read),
    INDEX idx_type (type),
    INDEX idx_created (created_at)
);

-- ===============================
-- AUDIT LOGS
-- ===============================

CREATE TABLE audit_logs (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT,
    action VARCHAR(100) NOT NULL,
    resource VARCHAR(100),
    resource_type VARCHAR(50) NOT NULL,
    resource_id VARCHAR(100),
    old_value TEXT,
    old_values JSON,
    new_value TEXT,
    new_values JSON,
    ip_address VARCHAR(50),
    user_agent VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(36),
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    updated_by VARCHAR(36),
    FOREIGN KEY (user_id) REFERENCES users(id),
    INDEX idx_user (user_id),
    INDEX idx_resource (resource_type, resource_id),
    INDEX idx_action (action),
    INDEX idx_created (created_at)
);

-- ===============================
-- STATS & STATISTICS
-- ===============================

CREATE TABLE stats_daily (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    date DATE NOT NULL,
    total_bookings INT DEFAULT 0,
    total_revenue DECIMAL(15,2) DEFAULT 0,
    movie_id BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(36),
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    updated_by VARCHAR(36),
    FOREIGN KEY (movie_id) REFERENCES movies(id) ON DELETE CASCADE
);

-- Enhanced statistics tables from code 2
CREATE TABLE movie_stats (
    movie_id BIGINT PRIMARY KEY,
    total_bookings INT DEFAULT 0,
    total_revenue DECIMAL(15,2) DEFAULT 0,
    total_seats_sold INT DEFAULT 0,
    average_rating DECIMAL(2,1) DEFAULT 0,
    total_reviews INT DEFAULT 0,
    last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (movie_id) REFERENCES movies(id) ON DELETE CASCADE
);

CREATE TABLE theater_stats (
    theater_id BIGINT PRIMARY KEY,
    total_bookings INT DEFAULT 0,
    total_revenue DECIMAL(15,2) DEFAULT 0,
    occupancy_rate DECIMAL(5,2) DEFAULT 0,
    last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (theater_id) REFERENCES theaters(id) ON DELETE CASCADE
);

CREATE TABLE showtime_availability (
    showtime_id BIGINT PRIMARY KEY,
    available_regular INT DEFAULT 0,
    available_vip INT DEFAULT 0,
    available_couple INT DEFAULT 0,
    total_available INT DEFAULT 0,
    last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (showtime_id) REFERENCES showtimes(id) ON DELETE CASCADE
);

-- ===============================
-- ADDITIONAL INDEXES FOR OPTIMIZATION
-- ===============================

-- Helpful indexes from code 2
CREATE INDEX idx_showtimes_movie_date_time ON showtimes(movie_id, show_date, show_time);
CREATE INDEX idx_showtimes_availability ON showtimes(show_date, available_seats);
CREATE INDEX idx_bookings_user_status ON bookings(user_id, booking_status);
CREATE INDEX idx_bookings_showtime_status ON bookings(showtime_id, booking_status);
CREATE INDEX idx_seat_reservations_expiry ON seat_reservations(reserved_until);

-- ===============================
-- INITIAL DATA
-- ===============================

INSERT INTO roles (name, description) VALUES
('SUPER_ADMIN', 'Full system access'),
('ADMIN', 'Administrative access'),
('THEATER_MANAGER', 'Theater management access'),
('STAFF', 'Staff access'),
('USER', 'Regular user access');

INSERT INTO permissions (name, resource, action, description) VALUES
('users.create', 'users', 'create', 'Create new users'),
('users.read', 'users', 'read', 'View user information'),
('users.update', 'users', 'update', 'Update user information'),
('users.delete', 'users', 'delete', 'Delete users'),
('movies.create', 'movies', 'create', 'Create new movies'),
('movies.read', 'movies', 'read', 'View movies'),
('movies.update', 'movies', 'update', 'Update movie information'),
('movies.delete', 'movies', 'delete', 'Delete movies'),
('bookings.create', 'bookings', 'create', 'Create bookings'),
('bookings.read', 'bookings', 'read', 'View bookings'),
('bookings.update', 'bookings', 'update', 'Update bookings'),
('bookings.cancel', 'bookings', 'cancel', 'Cancel bookings'),
('theaters.manage', 'theaters', 'manage', 'Manage theaters'),
('reports.view', 'reports', 'view', 'View reports');

INSERT INTO users (username, name, email, password, status, email_verified) VALUES
("super_admin", "Super Admin", "hoangdinhdung0205@gmail.com", "$2a$10$WAmWZOPzJql3FrgdSA/XjukYbyplIuNiexwRwpJnZGQhXwlmOnN86", "ACTIVE", TRUE),
("admin", "Admin", "admin@gmail.com", "$2a$10$WAmWZOPzJql3FrgdSA/XjukYbyplIuNiexwRwpJnZGQhXwlmOnN86", "ACTIVE", TRUE);

INSERT INTO user_roles (user_id, role_id) VALUES
(1, 1),
(2, 2);

-- ===============================
-- EVENTS (CLEANUP) - From code 2
-- ===============================

DELIMITER //
CREATE EVENT IF NOT EXISTS cleanup_expired_reservations
ON SCHEDULE EVERY 1 MINUTE
DO
  UPDATE seat_reservations
  SET status = 'EXPIRED'
  WHERE status = 'RESERVED' AND reserved_until < NOW();
//

CREATE EVENT IF NOT EXISTS cleanup_expired_bookings
ON SCHEDULE EVERY 5 MINUTE
DO
  UPDATE bookings
  SET booking_status = 'CANCELLED'
  WHERE booking_status = 'PENDING' AND expiry_time < NOW();
//

DELIMITER ;