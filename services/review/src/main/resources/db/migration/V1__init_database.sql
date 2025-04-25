CREATE TABLE IF NOT EXISTS review (
    id BIGSERIAL PRIMARY KEY,
    product_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    rating INT NOT NULL,
    text TEXT NOT NULL,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_date TIMESTAMP
);

CREATE SEQUENCE IF NOT EXISTS review_seq INCREMENT BY 1;

CREATE INDEX IF NOT EXISTS idx_review_user_id ON review (user_id);
CREATE INDEX IF NOT EXISTS idx_review_product_id ON review (product_id);