(ns scraper.firms.latham
  (:require [scraper.protocols :refer :all]
            [scraper.helpers :refer :all]))

(def latham (map->FirmWebsite
             {:names-xpath "//table[@id='PeopleList']/tbody/tr/td[1]"
              :positions-xpath "//table[@id='PeopleList']/tbody/tr/td[2]"
              :emails-xpath "//table[@id='PeopleList']/tbody/tr/td[3]/a"
              :urls-xpath "//table[@id='PeopleList']/tbody/tr/td[1]/a"
              :emails-selector "href"
              :names-function fix-lastname-firstname
              :emails-function fix-email
              :positions-function identity
              :urls-function identity}))
