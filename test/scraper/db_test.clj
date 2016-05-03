(ns scraper.db-test
  (:require [scraper.db :as sut]
            [scraper.protocols :refer [create-lawyer]]
            [hugsql.core :as h]
            [clojure.java.jdbc :as j]
            [clojure.test :refer :all])
  (:import [scraper.protocols Lawyer]))

(def ^{:private true} queries-path "queries.sql")

(def ^:dynamic test-db
  {:classname "org.h2.Driver"
   :subprotocol "h2"
   :subname (str "file://" (System/getProperty "user.dir") "/test/test.db")})

(defn- up-fixture []
  (h/def-db-fns queries-path)
  (create-positions-table test-db)
  (create-firms-table test-db)
  (create-lawyers-table test-db))

(defn- down-fixture []
  (drop-lawyers-table test-db)
  (drop-firms-table test-db)
  (drop-positions-table test-db))

(defn- setup-db [f]
  (up-fixture)
  (f)
  (down-fixture))

(def ^{:private true} lawyer-1
  {:first-name "John"
   :last-name "Marshall"
   :middle-initial nil
   :email "jmarshall@scotus.gov"
   :firm-name "SCOTUS"
   :position "Chief Justice"})

(def ^{:private true} lawyer-2
  {:first-name "Joseph"
   :last-name "Story"
   :middle-initial "J"
   :email "jstory@scotus.gov"
   :firm-name "SCOTUS"
   :position "Associate Justice"})

(def ^{:private true} lawyer-3
  {:first-name "Ethan"
   :last-name "Kent"
   :middle-initial "E"
   :email "ekent@jenner.com"
   :firm-name "Jenner & Block"
   :position "Associate"})

(deftest get-firms-and-positions-test
  (is (= {:firms #{"SCOTUS" "Jenner & Block"}
          :positions #{"Chief Justice" "Associate Justice" "Associate"}}
         (sut/get-firms-and-positions [lawyer-1 lawyer-2 lawyer-3]))))

(deftest add-new-firms-and-positions-test
  (binding [sut/*db* test-db]
    (insert-position test-db {:position "Associate"})
    (insert-firm test-db {:name "SCOTUS"})
    (sut/add-new-firms-and-positions [lawyer-1 lawyer-2 lawyer-3])
    (let [positions (set (map :position (get-all-positions test-db)))]
      (is (= positions
             #{"Chief Justice" "Associate Justice" "Associate"})))))

;; (h/def-db-fns "queries.sql")

(deftest insert-lawyers-with-fks-test
  (binding [sut/*db* test-db]
    (let [lawyers [lawyer-1 lawyer-2 lawyer-3]]
      (sut/insert-lawyers-with-fks [lawyer-3])
      (is (not (nil? (get-lawyer-by-name sut/*db* {:first-name "Ethan" :last-name "Kent"}))))
      (is (not (nil? (get-firm-by-name sut/*db* {:name (:firm-name lawyer-3)})))))))

;; (down-fixture)
;; (up-fixture)

;; (insert-position test-db {:position "Associate"})
;; (insert-firm test-db {:name "Jenner & Block"})
;; (set (map :position (get-all-positions test-db)))

;; (binding [sut/*db* test-db]
;;   (sut/add-new-firms-and-positions [lawyer-1 lawyer-2 lawyer-3]))

(use-fixtures :each setup-db)
