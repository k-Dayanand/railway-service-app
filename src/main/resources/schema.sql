-- ═══════════════════════════════════════════════════════════════
--  Railway Service Management System — MySQL Schema
--  NOTE: Spring Boot JPA auto-creates tables via ddl-auto=update
--        Run this ONLY if you want to create the DB manually.
-- ═══════════════════════════════════════════════════════════════

CREATE DATABASE IF NOT EXISTS railway_web;
USE railway_web;

-- Users table
CREATE TABLE IF NOT EXISTS users (
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    name       VARCHAR(100) NOT NULL,
    email      VARCHAR(150) NOT NULL UNIQUE,
    password   VARCHAR(255) NOT NULL,   -- BCrypt hash
    phone      VARCHAR(15),
    role       ENUM('USER','ADMIN') NOT NULL DEFAULT 'USER',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- Trains table
CREATE TABLE IF NOT EXISTS trains (
    train_id        BIGINT AUTO_INCREMENT PRIMARY KEY,
    train_name      VARCHAR(150) NOT NULL,
    train_number    VARCHAR(20)  NOT NULL UNIQUE,
    source          VARCHAR(100) NOT NULL,
    destination     VARCHAR(100) NOT NULL,
    departure_time  VARCHAR(10)  NOT NULL,
    arrival_time    VARCHAR(10)  NOT NULL,
    total_seats     INT NOT NULL DEFAULT 0,
    available_seats INT NOT NULL DEFAULT 0,
    sleeper_seats   INT NOT NULL DEFAULT 0,
    ac_seats        INT NOT NULL DEFAULT 0,
    general_seats   INT NOT NULL DEFAULT 0,
    fare_sleeper    DECIMAL(8,2) DEFAULT 0.00,
    fare_ac         DECIMAL(8,2) DEFAULT 0.00,
    fare_general    DECIMAL(8,2) DEFAULT 0.00,
    status          ENUM('ACTIVE','INACTIVE') DEFAULT 'ACTIVE'
);

-- Tickets table
CREATE TABLE IF NOT EXISTS tickets (
    ticket_id        BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id          BIGINT NOT NULL,
    train_id         BIGINT NOT NULL,
    passenger_name   VARCHAR(100) NOT NULL,
    passenger_age    INT NOT NULL,
    passenger_gender ENUM('Male','Female','Other') DEFAULT 'Male',
    journey_date     DATE,
    seat_number      VARCHAR(10),
    seat_type        ENUM('Sleeper','AC','General') DEFAULT 'General',
    amount           DECIMAL(8,2) NOT NULL,
    status           ENUM('BOOKED','CANCELLED') DEFAULT 'BOOKED',
    booked_at        DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id)  REFERENCES users(id)      ON DELETE CASCADE,
    FOREIGN KEY (train_id) REFERENCES trains(train_id) ON DELETE CASCADE
);

-- Payments table
CREATE TABLE IF NOT EXISTS payments (
    payment_id      BIGINT AUTO_INCREMENT PRIMARY KEY,
    ticket_id       BIGINT NOT NULL UNIQUE,
    amount          DECIMAL(8,2) NOT NULL,
    payment_method  ENUM('UPI','DEBIT_CARD','CREDIT_CARD','NET_BANKING') NOT NULL,
    payment_status  ENUM('SUCCESS','FAILED','REFUNDED') DEFAULT 'SUCCESS',
    transaction_ref VARCHAR(50),
    paid_at         DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (ticket_id) REFERENCES tickets(ticket_id) ON DELETE CASCADE
);
