(ns scraper.db
  (:require [clojure.java.jdbc :as j]
            [clojure.test :refer :all]))

(def firms-table-name "firms")
(def firms-name-column "name")
(def positions-table-name "positions")
(def positions-position-column "position")
(def lawyers-table-name "lawyers")

(def ^:dynamic *db*
    {:classname "org.sqlite.JDBC"
     :subprotocol "sqlite"
     :subname "resources/database.db"})

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

(defn add-or-ignore-with-id [input table column]
  (let [insert-string (str "INSERT OR IGNORE INTO " table "(" column ") VALUES (?)")
        query-string (str "SELECT * FROM " table " where " column " = ?")]
    (j/execute! *db* [insert-string input])
    (-> (j/query *db* [query-string input]) first :id)))  ; Does not appear to risk SQL injections because no user input
