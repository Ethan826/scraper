(ns scraper.firms.morgan-lewis
  (:require [scraper.protocols :refer :all]
            [scraper.helpers :refer :all]))


(def morgan-lewis
  (map->FirmWebsite
   {:names-xpath "//ul[@id='divPeopleListing']//h3/a"
    :urls-xpath "//ul[@id='divPeopleListing']//h3/a"
    :positions-xpath "//ul[@id='divPeopleListing']//p[@class='position-loc']/span"
    :emails-xpath "//ul[@id='divPeopleListing']//p[@class='mail-id-image']/a"}))
