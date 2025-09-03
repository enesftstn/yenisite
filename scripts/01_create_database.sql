-- Create database and user for Auto Parts Exchange
CREATE DATABASE IF NOT EXISTS autoparts_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Create user and grant privileges
CREATE USER IF NOT EXISTS 'autoparts_user'@'localhost' IDENTIFIED BY 'autoparts_pass';
GRANT ALL PRIVILEGES ON autoparts_db.* TO 'autoparts_user'@'localhost';
FLUSH PRIVILEGES;

USE autoparts_db;
