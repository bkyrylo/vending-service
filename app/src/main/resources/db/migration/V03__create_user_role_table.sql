CREATE TABLE user_role (
    id serial PRIMARY KEY,
    user_id uuid REFERENCES "user"(id) NOT NULL,
    role_id int REFERENCES role(id) NOT NULL
);