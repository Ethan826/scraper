(ns scraper.firms.baker-mckenzie
  (:require [scraper.protocols :refer :all]
            [scraper.helpers :refer :all]))

(defn- bm-email-fixer [email]
  (str
   (clojure.string/lower-case
    (re-find #"(?<=').*?(?=')" email))
   "@bakermckenzie.com"))

(def baker-mckenzie
  (map->FirmWebsite
   {:names-xpath "//div[@class='atty_name']"
    :positions-xpath "//div[@class='atty_title']"
    :urls-xpath "//div[@class='atty_name']/a"
    :emails-xpath "//div[@class='atty_email']/a"
    :names-function fix-firstname-lastname
    :emails-function bm-email-fixer
    :positions-function identity
    :urls-function identity}))
