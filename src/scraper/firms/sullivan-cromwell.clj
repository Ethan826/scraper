(ns scraper.firms.sullivan-cromwell
  (:require [scraper.protocols :refer :all]
            [scraper.helpers :refer :all]))

(def sullivan-cromwell
  (map->FirmWebsite
   {:names-xpath "//div[@class='table-body']//div[@class='col-1']/a"
    :positions-xpath "//div[@class='table-body']//div[@class='col-2']/span"
    :emails-xpath "//div[@class='table-body']//div[@class='col-4']/div[2]/a/span"
    :urls-xpath "//div[@class='table-body']//div[@class='col-1']/a"
    :emails-selector "innerHTML"
    :names-function fix-lastname-firstname
    :emails-function identity
    :positions-function identity
    :urls-function identity}))

; https://www.sullcrom.com/professionals-search-results?firstlastname=&lastname=&position=0&practice=0&school=0&keyword=&office=0&clerkship=0&language=0&Mfirstlastname=&Mlastname=&Mkeyword=&Mposition=0&Moffice=0&Mpractice=0&Mschool=0&Mclerkship=0&Mlanguage=0&btn_profSrch_desktop=find+lawyers
