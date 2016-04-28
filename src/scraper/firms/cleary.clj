(ns scraper.firms.cleary
  (:require [scraper.protocols :refer :all]
            [scraper.helpers :refer :all]))

(def cleary
  (map->FirmWebsite
   {:names-xpath "//h3[@class='search-results__name']"
    :positions-xpath "//p[@class='search-results__title']"
    :emails-xpath "//a[@class='search-results__email']"
    :urls-xpath "//h3[@class='search-results__name']/a"
    :emails-selector "innerHTML"
    :names-function fix-firstname-lastname
    :emails-function identity
    :positions-function identity
    :urls-function identity}))

; https://www.clearygottlieb.com/professionals?locationName=403802f4-50b6-4f2a-960e-f656c77a7aaa
