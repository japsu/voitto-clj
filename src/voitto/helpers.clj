(ns voitto.helpers
  (:use [clj-time.core :only [local-date]]
        [clj-time.format :only [formatter parse unparse unparse-local-date]]
        [clj-time.coerce :only [from-date to-date]]
        [clojure.string :only [split]]))

;; TODO localization
(def decimal-separator ",")
(def decimal-separator-regex #",")
(def currency " €")
(def custom-formatter (formatter "yyyy-MM-dd"))

(defn cents->str [cents]
  (let
    [bucks (quot cents 100)
     leftover-cents (format "%02d" (mod cents 100))]
    (str bucks decimal-separator leftover-cents)))

(defn str->cents [s]
  (let
    [[bucks-str cents-str] (split s decimal-separator-regex 2)
     [bucks cents] (map #(Integer. %1) [bucks-str cents-str])]
    
    (+ (* bucks 100) cents)))

(defn cents->str-with-currency [cents]
  (str (cents->str cents) currency))

(defprotocol FormatDate
  (date->str [date] "Format a date in the format used by Voitto"))

(extend-protocol FormatDate
  java.util.Date
  (date->str [date] (date->str (from-date date)))
  
  org.joda.time.LocalDate
  (date->str [date] (unparse-local-date custom-formatter date))
  
  org.joda.time.DateTime
  (date->str [date] (unparse custom-formatter date)))

(defn str->date [date-str]
  (to-date (parse custom-formatter date-str)))

(defn get-current-fiscal-year-start []
  (to-date (local-date 2013 1 1)))

(defn get-current-fiscal-year-end []
  (to-date (local-date 2013 12 31)))

(defn to-from-on [{:keys [from to]}]
  (if
    (= from to) (str "On " (date->str from))
    (str "From " (date->str from) " to " (date->str to))))

(defn today []
  (java.util.Date.))