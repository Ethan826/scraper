(ns foo.core
  (:require [clojure.test :refer :all])
  (:import [org.openqa.selenium By WebDriver WebElement]
           [org.openqa.selenium.firefox FirefoxDriver]
           [org.openqa.selenium.support.ui ExpectedConditions WebDriverWait]))

(defn- create-driver []
  (FirefoxDriver.))

(defprotocol Scraper
  (scrape-single-page [this url driver]))

(defrecord FirmWebsite [names-xpath urls-xpath positions-xpath emails-xpath
                        names-function emails-function positions-function urls-function])

(extend-protocol Scraper
  FirmWebsite
  (scrape-single-page [this url driver]
    (.get driver url)
    (.until (WebDriverWait. driver 30) (ExpectedConditions/presenceOfAllElementsLocatedBy (By/xpath (:names-xpath this))))
    (let [name-elements (.findElementsByXPath driver (:names-xpath this))
          url-elements (.findElementsByXPath driver (:urls-xpath this))
          position-elements (.findElementsByXPath driver (:positions-xpath this))
          email-elements (.findElementsByXPath driver (:emails-xpath this))
          names (map #((:names-function this) (.getText %)) name-elements)
          emails (map #((:emails-function this) (.getAttribute % "href")) email-elements)
          positions (map #((:positions-function this) (.getText %)) position-elements)
          urls (map #((:urls-function this) (.getAttribute % "href")) url-elements)]
      (mapv (fn [name url position email]
              (merge name (assoc {} :url url :position position :email email)))
            names urls positions emails))))

(defn- fix-lastname-firstname [the-name]
  (let [name-fixer-regex #"((\w+)( \w+)?)(?:, )(\w+)((?: )(\w))?"
        [last-name _ _ first-name _ middle-initial] (rest (re-find name-fixer-regex the-name))]
    {:last-name last-name :first-name first-name :middle-initial middle-initial}))

(defn- fix-email [email]
  (last (re-find #"(?:mailto:)(.*)" email)))

(def latham (map->FirmWebsite
             {:names-xpath "//table[@id='PeopleList']/tbody/tr/td[1]"
              :positions-xpath "//table[@id='PeopleList']/tbody/tr/td[2]"
              :emails-xpath "//table[@id='PeopleList']/tbody/tr/td[3]/a"
              :urls-xpath "//table[@id='PeopleList']/tbody/tr/td[1]/a"
              :names-function fix-lastname-firstname
              :emails-function fix-email
              :positions-function identity
              :urls-function identity}))

(defn- fix-firstname-lastname [the-name]
  (let [re-result (map first (re-seq #"(\w+(-\w+)?)" the-name))
        result {:first-name (first re-result)}]
    (if (= 2 (count re-result))
      (assoc result :last-name (last re-result) :middle-initial nil)
      (if (= 1 (count (second re-result)))
        (assoc result :last-name (last re-result) :middle-initial (second re-result))
        (assoc result :last-name (clojure.string/join " " (rest re-result)) :middle-initial nil)))))


(defn- bm-email-fixer [email]
  email)
  ;; (str
  ;;  (clojure.string/lower-case
  ;;   (re-find #"(?<=').*?(?=')" email))
  ;;  "@bakermckenzie.com"))

(def baker-mckenzie (map->FirmWebsite
                     {:names-xpath "//div[@class='atty_name']"
                      :positions-xpath "//div[@class='atty_title']"
                      :urls-xpath "//div[@class='atty_name']"
                      :emails-xpath "//div[@class='atty_email']"
                      :names-function fix-firstname-lastname
                      :emails-function bm-email-fixer
                      :positions-function identity
                      :urls-function identity}))

(.getText (.findElementByXPath driver "//div[@class='atty_name']"))


(re-seq #"(\w+(-\w+)?)" "Narendra Acharya")
(re-seq #"(\w+(-\w+)?)" "Kathleen A. Acharya")
(re-seq #"(\w+(-\w+)?)" "Christine Agnew Acharya")
(re-seq #"(\w+(-\w+)?)" "Bogdan-Alexandru Acharya")


(deftest fix-firstname-lastname-test
  (is (= (fix-firstname-lastname "Narendra Acharya")
         {:last-name "Acharya" :first-name "Narendra" :middle-initial nil}))
  (is (= (fix-firstname-lastname "Kathleen A. Agbayani")
         {:last-name "Agbayani" :first-name "Kathleen" :middle-initial "A"}))
  (is (= (fix-firstname-lastname "Christine Agnew Sloan")
         {:last-name "Agnew Sloan" :first-name "Christine" :middle-initial nil}))
  (is (= (fix-firstname-lastname "Bogdan-Alexandru Albu")
         {:last-name "Albu" :first-name "Bogdan-Alexandru" :middle-initial nil}))
  (is (= (fix-firstname-lastname "Fritz Boot-Strap")
         {:last-name "Boot-Strap" :first-name "Fritz" :middle-initial nil}))
  (is (= (fix-firstname-lastname "Cinna-Bon Fiddle-Sticks")
         {:last-name "Fiddle-Sticks" :first-name "Cinna-Bon" :middle-initial nil})))


;; (def morgan-lewis (map->FirmWebsite
;;                    {:root-url "https://www.morganlewis.com/our-people-results?pagenum=1&sortingqs=Last%20name&pagesize=500&loadCategories=true&param_sitecontentcategory=OUR%20PEOPLE&"
;;                     :names-xpath "//ul[@id='divPeopleListing']//h3/a"
;;                     :urls-xpath "//ul[@id='divPeopleListing']//h3/a"
;;                     :positions-xpath "//ul[@id='divPeopleListing']//p[@class='position-loc']/span"
;;                     :emails-xpath "//ul[@id='divPeopleListing']//p[@class='mail-id-image']/a"}))

;; (def driver (create-driver))
;; (scrape-single-page baker-mckenzie "http://www.bakermckenzie.com/ourpeople/List.aspx?regions=b5eb49ce-aa94-4658-bcd9-001b1fb80f4f&offices=" driver)
