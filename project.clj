(defproject scraper "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [clj-webdriver "0.7.2"]
                 [migratus "0.8.14"]
                 [com.h2database/h2 "1.4.191"]
                 [com.layerware/hugsql "0.4.7"]
                 [org.seleniumhq.selenium/selenium-server "2.53.0"]
                 [org.seleniumhq.selenium/selenium-htmlunit-driver "2.52.0"]
                 [org.seleniumhq.selenium/selenium-chrome-driver "2.53.0"]]
  :plugins [[migratus-lein "0.2.8"]]
  :migratus {:store :database
             :migration-dir "migrations/"
             :db {:classname "org.h2.Driver"
                  :subprotocol "h2"
                  :subname (str "file://" (System/getProperty "user.dir") "/resources/relationship_partner")}})
