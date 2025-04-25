CREATE TABLE IF NOT EXISTS delivery_method (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    is_active BOOLEAN NOT NULL,
    courier_type VARCHAR(255) NOT NULL,
    price DECIMAL(19, 4) NOT NULL,
    is_free_for_order_above BOOLEAN,
    free_for_order_above DECIMAL(19, 4)
);

CREATE SEQUENCE IF NOT EXISTS delivery_method_seq INCREMENT BY 1;


CREATE TABLE IF NOT EXISTS delivery (
    id BIGSERIAL PRIMARY KEY,
    order_id BIGINT NOT NULL,
    method_id BIGINT NOT NULL,
    status VARCHAR(255) NOT NULL,

    CONSTRAINT fk_delivery_method
    FOREIGN KEY (method_id)
    REFERENCES delivery_method(id)
);

CREATE SEQUENCE IF NOT EXISTS delivery_seq INCREMENT BY 1;