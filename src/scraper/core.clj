(ns foo.core
  (:require [clojure.test :refer :all])
  (:import [org.openqa.selenium By WebDriver WebElement]
           [org.openqa.selenium.firefox FirefoxDriver]
           [org.openqa.selenium.support.ui ExpectedConditions WebDriverWait]))

(defn- create-driver []
  (FirefoxDriver.))

(defprotocol Scraper
  (scrape-single-page [this url driver]))

(def driver (create-driver))
(.get driver "https://www.morganlewis.com/our-people-results?pagenum=1&sortingqs=Last%20name&pagesize=500&loadCategories=true&param_sitecontentcategory=OUR%20PEOPLE&")

(defrecord FirmWebsite [root-url names-xpath urls-xpath positions-xpath emails-xpath
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

(defn- fix-name [the-name]
  (let [name-fixer-regex #"((\w+)( \w+)?)(?:, )(\w+)((?: )(\w))?"
        [last-name _ _ first-name _ middle-initial] (rest (re-find name-fixer-regex the-name))]
    {:last-name last-name :first-name first-name :middle-initial middle-initial}))

(defn- fix-email [email]
  (last (re-find #"(?:mailto:)(.*)" email)))

(def latham (map->FirmWebsite
             {:root-url "https://www.lw.com/attorneyBioSearch.aspx?titleGroupName=Associate&peopleViewMode=ListView&esmode=1"
              :names-xpath "//table[@id='PeopleList']/tbody/tr/td[1]"
              :positions-xpath "//table[@id='PeopleList']/tbody/tr/td[2]"
              :emails-xpath "//table[@id='PeopleList']/tbody/tr/td[3]/a"
              :urls-xpath "//table[@id='PeopleList']/tbody/tr/td[1]/a"
              :names-function fix-name
              :emails-function fix-email
              :positions-function identity
              :urls-function identity}))

;; (def morgan-lewis (map->FirmWebsite
;;                    {:root-url "https://www.morganlewis.com/our-people-results?pagenum=1&sortingqs=Last%20name&pagesize=500&loadCategories=true&param_sitecontentcategory=OUR%20PEOPLE&"
;;                     :names-xpath "//ul[@id='divPeopleListing']//h3/a"
;;                     :urls-xpath "//ul[@id='divPeopleListing']//h3/a"
;;                     :positions-xpath "//ul[@id='divPeopleListing']//p[@class='position-loc']/span"
;;                     :emails-xpath "//ul[@id='divPeopleListing']//p[@class='mail-id-image']/a"}))

(scrape-single-page latham "https://www.lw.com/attorneyBioSearch.aspx?titleGroupName=Associate&peopleViewMode=ListView&esmode=1" driver)

(defn- scrape-location-lawyers-page [driver]
  (let [name-elements (.findElementsByXPath driver name-elements-xpath)
        url-elements (.findElementsByXPath driver url-elements-xpath)
        role-elements (.findElementsByXPath driver role-elements-xpath)
        email-elements (.findElementsByXPath driver email-elements-xpath)
        names (doall (map #(fix-name (.getText %)) name-elements))
        urls (doall (map #(.getAttribute % "href") url-elements))
        roles (doall (map #(.getText %) role-elements))
        emails (doall (map #(.getText %) email-elements))]
    (mapv (fn [name url position email]
            (merge name (assoc {} :url url :position position :email email)))
          names urls roles emails)))
