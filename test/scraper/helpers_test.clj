(ns scraper.helpers-test
  (:require [scraper.helpers :as sut]
            [clojure.test :as t]))


(deftest fix-firstname-lastname-test
  (is (= (sut/fix-firstname-lastname "Narendra Acharya")
         {:last-name "Acharya" :first-name "Narendra" :middle-initial nil}))
  (is (= (sut/fix-firstname-lastname "Kathleen A. Agbayani")
         {:last-name "Agbayani" :first-name "Kathleen" :middle-initial "A"}))
  (is (= (sut/fix-firstname-lastname "Christine Agnew Sloan")
         {:last-name "Agnew Sloan" :first-name "Christine" :middle-initial nil}))
  (is (= (sut/fix-firstname-lastname "Bogdan-Alexandru Albu")
         {:last-name "Albu" :first-name "Bogdan-Alexandru" :middle-initial nil}))
  (is (= (sut/fix-firstname-lastname "Fritz Boot-Strap")
         {:last-name "Boot-Strap" :first-name "Fritz" :middle-initial nil}))
  (is (= (sut/fix-firstname-lastname "Matthew (Matt) C. Alshouse")
         {:last-name "Alshouse" :first-name "Matt" :middle-initial "C"}))
  (is (= (sut/fix-firstname-lastname "Cinna-Bon Fiddle-Sticks")
         {:last-name "Fiddle-Sticks" :first-name "Cinna-Bon" :middle-initial nil})))
