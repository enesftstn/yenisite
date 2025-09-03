USE autoparts_db;

-- Additional performance indexes
CREATE INDEX idx_users_location ON users(city, state, zip_code);
CREATE INDEX idx_users_created_at ON users(created_at);

CREATE INDEX idx_parts_search_combo ON auto_parts(brand, model, year_start, year_end, category);
CREATE INDEX idx_parts_price_range ON auto_parts(price, condition_type, is_available);
CREATE INDEX idx_parts_seller_available ON auto_parts(seller_id, is_available);

CREATE INDEX idx_orders_date_range ON orders(created_at, status);
CREATE INDEX idx_orders_buyer_status ON orders(buyer_id, status);
CREATE INDEX idx_orders_seller_status ON orders(seller_id, status);

CREATE INDEX idx_messages_conversation_time ON messages(conversation_id, created_at);
CREATE INDEX idx_messages_unread ON messages(conversation_id, is_read, created_at);

CREATE INDEX idx_reviews_user_rating ON reviews(reviewed_user_id, rating);
CREATE INDEX idx_reviews_part_rating ON reviews(part_id, rating);
