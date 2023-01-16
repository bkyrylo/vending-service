CREATE TABLE product (
    id uuid PRIMARY KEY,
    product_name varchar(128) NOT NULL,
    cost int NOT NULL DEFAULT 0,
    amount int NOT NULL DEFAULT 0,
    user_id uuid NOT NULL
);