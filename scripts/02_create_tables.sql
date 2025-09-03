USE autoparts_db;

-- Users table
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    phone VARCHAR(20),
    address TEXT,
    city VARCHAR(100),
    state VARCHAR(100),
    zip_code VARCHAR(20),
    country VARCHAR(100) DEFAULT 'USA',
    role ENUM('USER', 'ADMIN', 'SELLER') DEFAULT 'USER',
    is_verified BOOLEAN DEFAULT FALSE,
    verification_token VARCHAR(255),
    reset_token VARCHAR(255),
    reset_token_expiry DATETIME,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_email (email),
    INDEX idx_role (role),
    INDEX idx_verification_token (verification_token),
    INDEX idx_reset_token (reset_token)
);

-- Locations table
CREATE TABLE locations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    address TEXT NOT NULL,
    city VARCHAR(100) NOT NULL,
    state VARCHAR(100) NOT NULL,
    zip_code VARCHAR(20) NOT NULL,
    country VARCHAR(100) DEFAULT 'USA',
    latitude DECIMAL(10, 8),
    longitude DECIMAL(11, 8),
    is_primary BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id),
    INDEX idx_coordinates (latitude, longitude),
    INDEX idx_location (city, state, zip_code)
);

-- Auto Parts table
CREATE TABLE auto_parts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    seller_id BIGINT NOT NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    brand VARCHAR(100),
    model VARCHAR(100),
    year_start INT,
    year_end INT,
    part_number VARCHAR(100),
    category VARCHAR(100),
    subcategory VARCHAR(100),
    condition_type ENUM('NEW', 'USED', 'REFURBISHED') DEFAULT 'USED',
    price DECIMAL(10, 2) NOT NULL,
    original_price DECIMAL(10, 2),
    quantity INT DEFAULT 1,
    is_available BOOLEAN DEFAULT TRUE,
    is_featured BOOLEAN DEFAULT FALSE,
    view_count INT DEFAULT 0,
    images JSON,
    specifications JSON,
    compatibility JSON,
    shipping_cost DECIMAL(8, 2) DEFAULT 0.00,
    return_policy TEXT,
    warranty_info TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (seller_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_seller_id (seller_id),
    INDEX idx_brand_model (brand, model),
    INDEX idx_category (category, subcategory),
    INDEX idx_year_range (year_start, year_end),
    INDEX idx_price (price),
    INDEX idx_condition (condition_type),
    INDEX idx_availability (is_available),
    INDEX idx_featured (is_featured),
    FULLTEXT idx_search (title, description, brand, model, part_number)
);

-- Orders table
CREATE TABLE orders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    buyer_id BIGINT NOT NULL,
    seller_id BIGINT NOT NULL,
    order_number VARCHAR(50) NOT NULL UNIQUE,
    status ENUM('PENDING', 'CONFIRMED', 'SHIPPED', 'DELIVERED', 'CANCELLED', 'REFUNDED') DEFAULT 'PENDING',
    total_amount DECIMAL(10, 2) NOT NULL,
    shipping_cost DECIMAL(8, 2) DEFAULT 0.00,
    tax_amount DECIMAL(8, 2) DEFAULT 0.00,
    shipping_address JSON NOT NULL,
    billing_address JSON,
    tracking_number VARCHAR(100),
    notes TEXT,
    estimated_delivery DATE,
    delivered_at DATETIME,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (buyer_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (seller_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_buyer_id (buyer_id),
    INDEX idx_seller_id (seller_id),
    INDEX idx_order_number (order_number),
    INDEX idx_status (status),
    INDEX idx_created_at (created_at)
);

-- Order Items table
CREATE TABLE order_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT NOT NULL,
    part_id BIGINT NOT NULL,
    quantity INT NOT NULL DEFAULT 1,
    unit_price DECIMAL(10, 2) NOT NULL,
    total_price DECIMAL(10, 2) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    FOREIGN KEY (part_id) REFERENCES auto_parts(id) ON DELETE CASCADE,
    INDEX idx_order_id (order_id),
    INDEX idx_part_id (part_id)
);

-- Payments table
CREATE TABLE payments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT NOT NULL,
    payment_method ENUM('CREDIT_CARD', 'DEBIT_CARD', 'PAYPAL', 'BANK_TRANSFER', 'CASH') DEFAULT 'CREDIT_CARD',
    payment_status ENUM('PENDING', 'COMPLETED', 'FAILED', 'REFUNDED') DEFAULT 'PENDING',
    amount DECIMAL(10, 2) NOT NULL,
    transaction_id VARCHAR(255),
    payment_gateway VARCHAR(100),
    gateway_response JSON,
    processed_at DATETIME,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    INDEX idx_order_id (order_id),
    INDEX idx_transaction_id (transaction_id),
    INDEX idx_payment_status (payment_status)
);

-- Conversations table
CREATE TABLE conversations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    participant1_id BIGINT NOT NULL,
    participant2_id BIGINT NOT NULL,
    part_id BIGINT,
    last_message_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (participant1_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (participant2_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (part_id) REFERENCES auto_parts(id) ON DELETE SET NULL,
    UNIQUE KEY unique_conversation (participant1_id, participant2_id, part_id),
    INDEX idx_participant1 (participant1_id),
    INDEX idx_participant2 (participant2_id),
    INDEX idx_part_id (part_id),
    INDEX idx_last_message (last_message_at)
);

-- Messages table
CREATE TABLE messages (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    conversation_id BIGINT NOT NULL,
    sender_id BIGINT NOT NULL,
    content TEXT NOT NULL,
    message_type ENUM('TEXT', 'IMAGE', 'FILE') DEFAULT 'TEXT',
    attachment_url VARCHAR(500),
    is_read BOOLEAN DEFAULT FALSE,
    read_at DATETIME,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (conversation_id) REFERENCES conversations(id) ON DELETE CASCADE,
    FOREIGN KEY (sender_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_conversation_id (conversation_id),
    INDEX idx_sender_id (sender_id),
    INDEX idx_created_at (created_at),
    INDEX idx_is_read (is_read)
);

-- Favorites table
CREATE TABLE favorites (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    part_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (part_id) REFERENCES auto_parts(id) ON DELETE CASCADE,
    UNIQUE KEY unique_favorite (user_id, part_id),
    INDEX idx_user_id (user_id),
    INDEX idx_part_id (part_id)
);

-- Reviews table
CREATE TABLE reviews (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    reviewer_id BIGINT NOT NULL,
    reviewed_user_id BIGINT NOT NULL,
    order_id BIGINT,
    part_id BIGINT,
    rating INT NOT NULL CHECK (rating >= 1 AND rating <= 5),
    title VARCHAR(255),
    comment TEXT,
    is_verified_purchase BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (reviewer_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (reviewed_user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE SET NULL,
    FOREIGN KEY (part_id) REFERENCES auto_parts(id) ON DELETE SET NULL,
    INDEX idx_reviewer_id (reviewer_id),
    INDEX idx_reviewed_user_id (reviewed_user_id),
    INDEX idx_order_id (order_id),
    INDEX idx_part_id (part_id),
    INDEX idx_rating (rating),
    INDEX idx_created_at (created_at)
);
