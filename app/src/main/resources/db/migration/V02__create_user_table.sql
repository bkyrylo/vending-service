CREATE TABLE "user" (
    id uuid PRIMARY KEY,
    username varchar(64) UNIQUE NOT NULL,
    password varchar(64) NOT NULL
);