CREATE TABLE IF NOT EXISTS lawyers(
       id serial primary key,
       firstname text not null,
       middleinitial text,
       lastname text not null,
       email text not null,
       position integer not null references positions(id),
       firm integer not null references firms(id)
);
