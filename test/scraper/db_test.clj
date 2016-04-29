(ns scraper.db-test
  (:require [scraper.db :as sut]
            [clojure.java.jdbc :as j]
            [clojure.test :refer :all]))

(def ^{:private true} test-db
  (let [db-host "localhost"
        db-port 5432
        db-name "test"]
    {:classname "org.postgresql.Driver"
     :subprotocol "postgresql"
     :subname (str "//" db-host ":" db-port "/" db-name)
     :user "postgres"}))

(defn- up-fixture []
  (let [firms-sql (j/create-table-ddl
                   (keyword sut/firms-table-name)
                   [[:id :serial :primary :key]
                    [:name :text :not :null]])
        positions-sql (j/create-table-ddl
                       (keyword sut/positions-table-name)
                       [[:id :serial :primary :key]
                        [:position :text :not :null]])
        lawyers-sql (j/create-table-ddl
                     (keyword sut/lawyers-table-name)
                     [[:id :serial :primary :key]
                      [:firstname :text :not :null]
                      [:lastname :text :not :null]
                      [:email :text :not :null]
                      [:position :integer :not :null "references positions(id)"]
                      [:firm :integer :not :null "references firms(id)"]])]
    (j/execute! test-db firms-sql)
    (j/execute! test-db positions-sql)
    (j/execute! test-db lawyers-sql)))

(defn- down-fixture [] ; SQL Injection risk if used outside of test suite.
  (j/execute! test-db (str "drop table if exists " sut/lawyers-table-name))
  (j/execute! test-db (str "drop table if exists " sut/positions-table-name))
  (j/execute! test-db (str "drop table if exists " sut/firms-table-name)))

(defn- setup-db [f]
  (up-fixture)
  (f)
  (down-fixture))

(defn- has-item? [item db table column]
  (let [sql-string (str "select count(*) from " table " where " column " = ?")] ; DANGER! SQL Injection. Used only in tests.
    (-> (j/query db [sql-string item])
        first
        :count
        pos?)))

(deftest add-firms-test
  (binding [sut/*db* test-db]
    (let [firm "Dewey Cheatem & Howe"
          firms ["Salt n' Peppa" "Bingham McCutcheon"]]
      (do
        (sut/add-firms firm)
        (is (has-item? firm sut/*db* sut/firms-table-name sut/firms-name-column))
        (sut/add-firms firms)
        (is (has-item? (first firms) sut/*db* sut/firms-table-name sut/firms-name-column))
        (is (has-item? (second firms) sut/*db* sut/firms-table-name sut/firms-name-column))))))

(deftest add-positions-test
  (binding [sut/*db* test-db]
    (let [position "Associate"
          positions ["Partner" "Of Counsel"]]
      (do
        (sut/add-positions position)
        (is (has-item? position sut/*db* sut/positions-table-name sut/positions-position-column))
        (sut/add-positions positions)
        (is (has-item? (first positions) sut/*db* sut/positions-table-name sut/positions-position-column))
        (is (has-item? (second positions) sut/*db* sut/positions-table-name sut/positions-position-column))))))

(use-fixtures :each setup-db)
