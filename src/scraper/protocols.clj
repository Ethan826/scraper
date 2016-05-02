(ns scraper.protocols
  (:import [org.openqa.selenium By WebDriver WebElement]
           [org.openqa.selenium.firefox FirefoxDriver]
           [org.openqa.selenium.support.ui ExpectedConditions WebDriverWait]))

(defn create-driver []
  (FirefoxDriver.))

(defprotocol Scraper
  (scrape-single-page [this url driver]))

(defrecord FirmWebsite [names-xpath urls-xpath positions-xpath emails-xpath emails-selector
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

(defrecord Lawyer [first-name last-name middle-initial email position firm])

(defn create-lawyer [m]
  (map->Lawyer m))
