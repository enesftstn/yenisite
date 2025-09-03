USE autoparts_db;

-- Insert sample users
INSERT INTO users (email, password, first_name, last_name, phone, address, city, state, zip_code, role, is_verified) VALUES
('admin@autoparts.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'Admin', 'User', '555-0001', '123 Admin St', 'New York', 'NY', '10001', 'ADMIN', TRUE),
('seller1@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'John', 'Seller', '555-0002', '456 Seller Ave', 'Los Angeles', 'CA', '90001', 'SELLER', TRUE),
('buyer1@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'Jane', 'Buyer', '555-0003', '789 Buyer Blvd', 'Chicago', 'IL', '60601', 'USER', TRUE),
('seller2@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'Mike', 'Parts', '555-0004', '321 Parts Rd', 'Houston', 'TX', '77001', 'SELLER', TRUE);

-- Insert sample locations
INSERT INTO locations (user_id, address, city, state, zip_code, latitude, longitude, is_primary) VALUES
(2, '456 Seller Ave', 'Los Angeles', 'CA', '90001', 34.0522, -118.2437, TRUE),
(3, '789 Buyer Blvd', 'Chicago', 'IL', '60601', 41.8781, -87.6298, TRUE),
(4, '321 Parts Rd', 'Houston', 'TX', '77001', 29.7604, -95.3698, TRUE);

-- Insert sample auto parts
INSERT INTO auto_parts (seller_id, title, description, brand, model, year_start, year_end, part_number, category, subcategory, condition_type, price, original_price, quantity, specifications, compatibility) VALUES
(2, 'BMW E46 Front Brake Pads', 'High-quality ceramic brake pads for BMW 3 Series E46. Excellent stopping power and low dust.', 'BMW', '3 Series', 1999, 2006, 'BP-E46-001', 'Brakes', 'Brake Pads', 'NEW', 89.99, 120.00, 5, '{"material": "ceramic", "warranty": "2 years"}', '{"models": ["320i", "325i", "330i", "M3"]}'),
(2, 'Honda Civic Headlight Assembly', 'OEM replacement headlight for Honda Civic. Perfect fit and finish.', 'Honda', 'Civic', 2006, 2011, 'HL-CIV-006', 'Lighting', 'Headlights', 'USED', 145.00, 280.00, 2, '{"side": "driver", "bulb_type": "H11"}', '{"models": ["Civic Sedan", "Civic Coupe"]}'),
(4, 'Ford F-150 Tailgate', 'Complete tailgate assembly for Ford F-150. Minor scratches but fully functional.', 'Ford', 'F-150', 2009, 2014, 'TG-F150-009', 'Body', 'Tailgate', 'USED', 350.00, 800.00, 1, '{"color": "Oxford White", "condition": "good"}', '{"models": ["F-150 Regular Cab", "F-150 SuperCab", "F-150 SuperCrew"]}'),
(4, 'Chevrolet Camaro SS Exhaust System', 'Performance exhaust system for Camaro SS. Aggressive sound and improved flow.', 'Chevrolet', 'Camaro', 2010, 2015, 'EX-CAM-010', 'Exhaust', 'Cat-Back System', 'REFURBISHED', 599.99, 1200.00, 1, '{"material": "stainless steel", "sound_level": "aggressive"}', '{"models": ["Camaro SS", "Camaro ZL1"]}');

-- Insert sample favorites
INSERT INTO favorites (user_id, part_id) VALUES
(3, 1),
(3, 2);

-- Insert sample conversation and messages
INSERT INTO conversations (participant1_id, participant2_id, part_id) VALUES
(3, 2, 1);

INSERT INTO messages (conversation_id, sender_id, content) VALUES
(1, 3, 'Hi, I\'m interested in the BMW brake pads. Are they still available?'),
(1, 2, 'Yes, they are still available. Would you like to know more details?'),
(1, 3, 'What\'s the condition and do you offer any warranty?');

-- Update message read status
UPDATE messages SET is_read = TRUE, read_at = NOW() WHERE id IN (1, 2);
