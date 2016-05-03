-- :name create-firms-table :! :n
-- :doc Create the firms table
CREATE TABLE IF NOT EXISTS firms(id INTEGER AUTO_INCREMENT PRIMARY KEY, name VARCHAR(255) NOT NULL)

-- :name create-positions-table :! :n
-- :doc Create the positions table
CREATE TABLE IF NOT EXISTS positions(id INTEGER AUTO_INCREMENT PRIMARY KEY, position VARCHAR(255) NOT NULL)

-- :name create-lawyers-table :! :n
-- :doc Create the lawyers table
CREATE TABLE IF NOT EXISTS lawyers(
    id INTEGER AUTO_INCREMENT PRIMARY KEY,
    firstname VARCHAR(255) NOT NULL,
    middleinitial VARCHAR(255),
    lastname VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    firmname VARCHAR(255) NOT NULL,
    position VARCHAR(255) NOT NULL,
    FOREIGN KEY(position) REFERENCES positions(position),
    FOREIGN KEY(firmname) REFERENCES firms(name)
);

-- :name get-all-firm-names :?
-- :doc Get all firm names
SELECT * FROM firms

-- :name get-all-positions :?
-- :doc Get all positions 
SELECT * FROM positions

-- :name drop-firms-table :!
-- :doc Drops the firms table
DROP TABLE IF EXISTS firms

-- :name drop-positions-table :!
-- :doc Drops the positions table
DROP TABLE IF EXISTS positions

-- :name drop-lawyers-table :!
-- :doc Drops the lawyers table
DROP TABLE IF EXISTS lawyers

-- :name insert-firm :! :n
-- :doc Insert a single firm
INSERT INTO firms(name) values (:name)

-- :name insert-firms :! :n
-- :doc Insert multiple firms
INSERT INTO firms(name) values :tuple*:firms

-- :name insert-position :! :n
-- :doc Insert a single position
INSERT INTO positions(position) values (:position)

-- :name insert-positions :! :n
-- :doc Insert multiple positions
INSERT INTO positions(position) values :tuple*:positions

-- :name get-firm-by-name :? :1
-- :doc Get the first firm matching the supplied name
SELECT * FROM firms WHERE name = :name

-- :name insert-lawyer :! :n
-- :doc Insert a single lawyer
INSERT INTO lawyers(firstname, middleinitial, lastname, email, firmname, position)
VALUES (:first-name, :middle-initial, :last-name, :email, :firm-name, :position)

-- :name get-lawyer-by-name :? :1
-- :doc Get the first lawyer matching the supplied name
SELECT * FROM lawyers WHERE lastname = :last-name AND firstname = :first-name
