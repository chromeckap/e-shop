CREATE EXTENSION IF NOT EXISTS pg_trgm;

CREATE TABLE IF NOT EXISTS product (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    is_visible BOOLEAN NOT NULL
);

CREATE SEQUENCE IF NOT EXISTS product_seq INCREMENT BY 1;


CREATE TABLE IF NOT EXISTS product_category (
    product_id BIGINT NOT NULL,
    category_id BIGINT NOT NULL,
    PRIMARY KEY (product_id, category_id),

    FOREIGN KEY (product_id)
    REFERENCES product(id)
    ON DELETE CASCADE
);

CREATE SEQUENCE IF NOT EXISTS product_category_seq INCREMENT BY 1;


CREATE TABLE IF NOT EXISTS product_related_product (
    product_id BIGINT NOT NULL,
    related_product_id BIGINT NOT NULL,
    PRIMARY KEY (product_id, related_product_id),

    FOREIGN KEY (product_id)
    REFERENCES product(id)
    ON DELETE CASCADE,

    FOREIGN KEY (related_product_id)
    REFERENCES product(id)
    ON DELETE CASCADE
);

CREATE SEQUENCE IF NOT EXISTS product_related_product_seq INCREMENT BY 1;


CREATE TABLE IF NOT EXISTS product_image (
    id BIGSERIAL PRIMARY KEY,
    product_id BIGINT NOT NULL,
    image_path VARCHAR(255) NOT NULL,
    upload_order INT NOT NULL,

    FOREIGN KEY (product_id)
    REFERENCES product(id)
    ON DELETE CASCADE
);

CREATE SEQUENCE IF NOT EXISTS product_image_seq INCREMENT BY 1;



CREATE TABLE IF NOT EXISTS attribute (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);

CREATE SEQUENCE IF NOT EXISTS attribute_seq INCREMENT BY 1;


CREATE TABLE IF NOT EXISTS attribute_value (
    id BIGSERIAL PRIMARY KEY,
    attribute_id BIGINT NOT NULL,
    value VARCHAR(255) NOT NULL,

    FOREIGN KEY (attribute_id)
    REFERENCES attribute(id)
    ON DELETE CASCADE
);

CREATE SEQUENCE IF NOT EXISTS attribute_value_seq INCREMENT BY 1;


CREATE TABLE IF NOT EXISTS product_attribute (
    product_id BIGINT NOT NULL,
    attribute_id BIGINT NOT NULL,
    PRIMARY KEY (product_id, attribute_id),

    FOREIGN KEY (product_id)
    REFERENCES product(id)
    ON DELETE CASCADE,

    FOREIGN KEY (attribute_id)
    REFERENCES attribute(id)
    ON DELETE CASCADE
);

CREATE SEQUENCE IF NOT EXISTS product_attribute_seq INCREMENT BY 1;



CREATE TABLE IF NOT EXISTS variant (
    id BIGSERIAL PRIMARY KEY,
    product_id BIGINT NOT NULL,
    sku VARCHAR(255) NOT NULL UNIQUE,
    base_price DECIMAL(10, 2) NOT NULL,
    discounted_price DECIMAL(10, 2),
    quantity INT NOT NULL,
    quantity_unlimited BOOLEAN,

    FOREIGN KEY (product_id)
    REFERENCES product(id)
    ON DELETE CASCADE
);

CREATE SEQUENCE IF NOT EXISTS variant_seq INCREMENT BY 1;


CREATE TABLE IF NOT EXISTS variant_attribute_value (
    variant_id BIGINT NOT NULL,
    attribute_value_id BIGINT NOT NULL,
    PRIMARY KEY (variant_id, attribute_value_id),

    FOREIGN KEY (variant_id)
    REFERENCES variant(id)
    ON DELETE CASCADE,

    FOREIGN KEY (attribute_value_id)
    REFERENCES attribute_value(id)
    ON DELETE CASCADE
);

CREATE SEQUENCE IF NOT EXISTS variant_attribute_value_seq INCREMENT BY 1;