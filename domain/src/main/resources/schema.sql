DROP TABLE IF EXISTS `product` CASCADE;
DROP TABLE IF EXISTS `orders` CASCADE;

CREATE TABLE product (
     product_id BIGINT NOT NULL,
     name VARCHAR(255),
     price DOUBLE,
     quantity INT,
     version BIGINT DEFAULT 0,
     PRIMARY KEY (product_id)
);

CREATE TABLE orders (
    id BIGINT AUTO_INCREMENT,
    product_id BIGINT,
    quantity INT,
    user_id VARCHAR(255),
    PRIMARY KEY (id),
    FOREIGN KEY (product_id) REFERENCES product(product_id) ON DELETE CASCADE
);