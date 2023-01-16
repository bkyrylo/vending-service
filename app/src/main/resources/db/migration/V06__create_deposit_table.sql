CREATE TABLE deposit (
    id uuid PRIMARY KEY,
    cents int NOT NULL DEFAULT 0,
    user_id uuid REFERENCES "user"(id) NOT NULL
);