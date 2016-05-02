(h/def-db-fns "queries.sql")

(defn- get-firm-names []
  (map :name (get-all-firm-names *db*)))

(defn- get-positions []
  (map :position (get-all-positions *db*)))

(get-positions)

(drop-lawyers-table *db*)
(drop-positions-table *db*)
(drop-firms-table *db*)
(create-positions-table *db*)
(create-firms-table *db*)
(create-lawyers-table *db*)
(insert-firm *db* {:name "Jenner"})
(insert-position *db* {:position "Associate"})

(insert-firm *db* {:name "Jenner"})
(get-firm-id-by-name *db* {:name "Jenner"})

(insert-lawyer *db* {:first-name "Ethan"
                     :middle-initial "E"
                     :last-name "Kent"
                     :email "ekent@jenner.com"
                     :position "Associate"
                     :firm-name "Jenner"})

(str (System/getProperty "user.dir") "/resources/relationship_partner")

(def firms-table-name "firms")
(def firms-name-column "name")
(def positions-table-name "positions")
(def positions-position-column "position")
(def lawyers-table-name "lawyers")

(defn- get-new-entries [data]
  ())

(defn- process-lawyers [& lawyers]
  (let [data (reduce
              (fn [accum el]
                (assoc
                 accum
                 :firms (conj (:firms accum) (:firm-name el))
                 :positions (conj (:positions accum) (:position el))))
              {:firms #{} :positions #{}}
              lawyers)]
    add-firms))

(def ^:dynamic *db*
  {:classname "org.h2.Driver"
   :subprotocol "h2"
   :subname (str "file://" (System/getProperty "user.dir") "/resources/relationship_partner.mv.db")})

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

(defn add-lawyers []
  nil)
