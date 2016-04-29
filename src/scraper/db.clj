(ns scraper.db
  (:require [clojure.java.jdbc :as j]
            [clojure.test :refer :all]))

(def firms-table-name "firms")
(def firms-name-column "name")
(def positions-table-name "positions")
(def positions-position-column "position")
(def lawyers-table-name "lawyers")

(def ^:dynamic *db*
  (let [db-host "localhost"
        db-port 5432
        db-name "relationship_partner"]
    {:classname "org.postgresql.Driver"
     :subprotocol "postgresql"
     :subname (str "//" db-host ":" db-port "/" db-name)
     :user "postgres"}))

(defn- add-helper [input table column]
  (condp instance? input
    java.lang.String (j/insert! *db* (keyword table) {(keyword column) input})
    clojure.lang.Sequential (j/insert-multi!
                             *db*
                             (keyword table)
                             (map #(assoc {} (keyword column) %) input))))

(defn add-firms [firm-names]
  (add-helper firm-names firms-table-name firms-name-column))

(defn add-positions [position]
  (add-helper position positions-table-name positions-position-column))
