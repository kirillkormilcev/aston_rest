DROP TABLE IF EXISTS products CASCADE;
DROP TABLE IF EXISTS product_categories CASCADE;
DROP TABLE IF EXISTS products_product_categories CASCADE;
DROP TABLE IF EXISTS order_details CASCADE;
DROP TABLE IF EXISTS order_details_products CASCADE;
DROP TABLE IF EXISTS order_approval CASCADE;

CREATE TABLE IF NOT EXISTS products
(
    id BIGSERIAL
        PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    price DECIMAL,
    quantity BIGINT,
    available BOOLEAN
);

CREATE TABLE IF NOT EXISTS product_categories
(
    id BIGSERIAL
        PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    category_type VARCHAR(64) NOT NULL,
    CONSTRAINT unique_pc_ct UNIQUE (name, category_type)
);

CREATE TABLE IF NOT EXISTS products_product_categories
(
    product_id BIGINT NOT NULL
        CONSTRAINT fk_product_product_category
            REFERENCES products ON DELETE CASCADE,
    product_category_id BIGINT NOT NULL
        CONSTRAINT fk_product_category_product
            REFERENCES product_categories,
    PRIMARY KEY (product_id, product_category_id)
);

CREATE TABLE IF NOT EXISTS order_details
(
    id BIGSERIAL
        PRIMARY KEY,
    order_status VARCHAR(64),
    total_amount DECIMAL
);

CREATE TABLE IF NOT EXISTS order_details_products
(
    odp_id BIGSERIAL
        PRIMARY KEY,
    order_detail_id BIGINT NOT NULL
        CONSTRAINT fk_order_detail_id
            REFERENCES order_details ON DELETE CASCADE,
    product_id BIGINT NOT NULL
        CONSTRAINT fk_order_detail_product
            REFERENCES products
);

CREATE TABLE IF NOT EXISTS order_approval
(
    id BIGSERIAL
        PRIMARY KEY,
    order_detail_id BIGINT NOT NULL
        CONSTRAINT fk_order_approval_order_detail
            REFERENCES order_details
);

CREATE INDEX odp_od_id ON order_details_products (order_detail_id);

CREATE INDEX odp_p_id ON order_details_products (product_id);

CREATE INDEX oa_od_id ON order_approval (order_detail_id);