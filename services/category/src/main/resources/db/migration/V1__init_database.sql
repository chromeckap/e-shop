CREATE TABLE IF NOT EXISTS category (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    parent_id BIGINT,

    CONSTRAINT fk_category_parent
    FOREIGN KEY (parent_id)
    REFERENCES category(id)
    ON DELETE SET NULL

);

CREATE SEQUENCE IF NOT EXISTS category_seq INCREMENT BY 1;