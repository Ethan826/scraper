CREATE TABLE IF NOT EXISTS firms(
       id serial primary key,
       name text unique not null
);
