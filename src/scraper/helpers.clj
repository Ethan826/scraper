(ns scraper.helpers)

(defn fix-lastname-firstname [the-name]
  "Given a name of the form 'Doe, John W', return a map with :first-name
   :middle-initial :last-name."
  (let [name-fixer-regex #"((\w+)( \w+)?)(?:, )(\w+)((?: )(\w))?"
        [last-name _ _ first-name _ middle-initial] (rest (re-find name-fixer-regex the-name))]
    {:last-name last-name :first-name first-name :middle-initial middle-initial}))

(defn fix-firstname-lastname [the-name]
  "Given a name of the form 'John W. Doe', return a map with :first-name
   :middle-initial :last-name."
  (let [handle-parens
        (if (re-find #"\(.*\)" the-name) ; "Matthew (Matt) Smith => Matt Smith"
          (apply str (rest (re-find #"\((.*)\)(.*)" the-name)))
          the-name)
        re-result (map first (re-seq #"(\w+(-\w+)?)" handle-parens))
        result {:first-name (first re-result)}]
    (if (= 2 (count re-result)) ; firstname lastname
      (assoc result :last-name (last re-result) :middle-initial nil)
      (if (= 1 (count (second re-result))) ; firstname mi lastname
        (assoc result :last-name (last re-result) :middle-initial (second re-result))
                                        ; firstname two lastnames
        (assoc result :last-name (clojure.string/join " " (rest re-result)) :middle-initial nil)))))

(defn fix-email [email]
  "Strip 'mailto:' from an email href."
  (last (re-find #"(?:mailto:)(.*)" email)))
