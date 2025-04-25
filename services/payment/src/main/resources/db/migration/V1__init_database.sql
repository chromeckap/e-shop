CREATE TABLE IF NOT EXISTS payment_method (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    is_active BOOLEAN NOT NULL,
    gateway_type VARCHAR(255) NOT NULL,
    price DECIMAL(19, 4) NOT NULL,
    is_free_for_order_above BOOLEAN,
    free_for_order_above DECIMAL(19, 4)
);

CREATE SEQUENCE IF NOT EXISTS payment_method_seq INCREMENT BY 1;


CREATE TABLE IF NOT EXISTS payment (
    id BIGSERIAL PRIMARY KEY,
    order_id BIGINT NOT NULL,
    method_id BIGINT NOT NULL,
    status VARCHAR(255) NOT NULL,
    total_price DECIMAL(19, 4) NOT NULL,
    dtype VARCHAR(255) NOT NULL,
    session_id VARCHAR(255),
    is_paid BOOLEAN,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_date TIMESTAMP,

    CONSTRAINT fk_delivery_method
    FOREIGN KEY (method_id)
    REFERENCES payment_method(id)
);

CREATE SEQUENCE IF NOT EXISTS payment_seq INCREMENT BY 1;

