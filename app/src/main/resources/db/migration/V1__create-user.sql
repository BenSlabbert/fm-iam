CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE "user" (
    id uuid DEFAULT uuid_generate_v4 (),
    name varchar(255),
    password_hash varchar(255),
    version integer default 0 not null ,
    created timestamp default now() not null,
    updated timestamp default now() not null,
    PRIMARY KEY (id)
);
