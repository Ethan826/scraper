(ns scraper.db
  (:require [clojure.java.jdbc :as j]
            [clojure.test :refer :all]))

(def firms-table-name "firms")
(def positions-table-name "positions")
(def lawyers-table-name "lawyers")

(def ^:dynamic *db*
  (let [db-host "localhost"
        db-port 5432
        db-name "relationship_partner"]
    {:classname "org.postgresql.Driver"
     :subprotocol "postgresql"
     :subname (str "//" db-host ":" db-port "/" db-name)
     :user "postgres"}))

(defn add-firms [firm-names]
  (condp instance? firm-names
    java.lang.String (j/insert! *db* (keyword firms-table-name) {:name firm-names})
    clojure.lang.Sequential (j/insert-multi!
                             *db*
                             (keyword firms-table-name)
                             (map #(assoc {} :name %) firm-names))))

