CREATE TABLE IF NOT EXISTS orders (
    id BIGSERIAL PRIMARY KEY,
    status VARCHAR(255) NOT NULL,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_date TIMESTAMP
);

CREATE SEQUENCE IF NOT EXISTS orders_seq INCREMENT BY 1;


CREATE TABLE IF NOT EXISTS order_item (
    id BIGSERIAL PRIMARY KEY,
    order_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    name VARCHAR(255) NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    quantity BIGINT NOT NULL,

    CONSTRAINT fk_order_item_order
    FOREIGN KEY (order_id)
    REFERENCES orders(id) ON DELETE CASCADE
);

CREATE SEQUENCE IF NOT EXISTS order_item_seq INCREMENT BY 1;


CREATE TABLE IF NOT EXISTS order_item_attribute_value (
    order_item_id BIGSERIAL NOT NULL,
    values_key VARCHAR(255) NOT NULL,
    PRIMARY KEY (order_item_id, values_key),
    attribute_value VARCHAR(255) NOT NULL,

    CONSTRAINT fk_order_item_attribute_values
    FOREIGN KEY (order_item_id)
    REFERENCES order_item(id)
    ON DELETE CASCADE
);

CREATE SEQUENCE IF NOT EXISTS order_item_attribute_value_seq INCREMENT BY 1;


CREATE TABLE IF NOT EXISTS order_additional_cost (
    order_id BIGSERIAL NOT NULL,
    cost_type VARCHAR(255) NOT NULL,
    PRIMARY KEY (order_id, cost_type),
    cost_amount DECIMAL(10, 2) NOT NULL,

    CONSTRAINT fk_order_additional_cost
    FOREIGN KEY (order_id)
    REFERENCES orders(id)
    ON DELETE CASCADE
);

CREATE SEQUENCE IF NOT EXISTS order_additional_cost_seq INCREMENT BY 1;


CREATE TABLE IF NOT EXISTS user_details (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    order_id BIGINT NOT NULL,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    email VARCHAR(255) NOT NULL,

    CONSTRAINT fk_user_details_order
    FOREIGN KEY (order_id)
    REFERENCES orders(id)
    ON DELETE CASCADE
);

CREATE SEQUENCE IF NOT EXISTS user_details_seq INCREMENT BY 1;


CREATE TABLE IF NOT EXISTS address (
    id BIGSERIAL PRIMARY KEY,
    user_details_id BIGINT NOT NULL,
    street VARCHAR(255) NOT NULL,
    city VARCHAR(255) NOT NULL,
    postal_code VARCHAR(255) NOT NULL,

    CONSTRAINT fk_address_user_details
    FOREIGN KEY (user_details_id)
    REFERENCES user_details(id)
    ON DELETE CASCADE
);
