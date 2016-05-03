(ns scraper.db
  (:require [hugsql.core :as h]
            [clojure.set :as set]))

(def ^:dynamic *db*
  {:classname "org.h2.Driver"
   :subprotocol "h2"
   :subname (str "file://" (System/getProperty "user.dir") "/resources/relationship_partner.db")})

(h/def-db-fns "queries.sql")

(defn get-firms-and-positions [lawyers]
  (reduce
   (fn [accum el]
     (assoc
      accum
      :firms (conj (:firms accum) (:firm-name el))
      :positions (conj (:positions accum) (:position el))))
   {:firms #{} :positions #{}}
   lawyers))

(defn add-new-firms-and-positions [lawyers]
  (let [new-firms-and-positions (get-firms-and-positions lawyers)
        existing-positions (set (map :position (get-all-positions *db*)))
        existing-firms (set (map :name (get-all-firm-names *db*)))
        new-positions (set/difference (:positions new-firms-and-positions) existing-positions)
        new-firms (set/difference (:firms new-firms-and-positions) existing-firms)]
    (insert-firms *db* {:firms (map list new-firms)})
    (insert-positions *db* {:positions (map list new-positions)})))

(defn insert-lawyers-with-fks [lawyers]
  (add-new-firms-and-positions lawyers)
  (insert-lawyer *db* (first lawyers)))

