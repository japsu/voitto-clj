(ns voitto.helpers
  (use [clj-time.format :only [formatter parse unparse unparse-local-date]]
       [clj-time.coerce :only [from-date to-date]]))

;; TODO localization
(def decimal-separator ",")
(def currency " €")
(def custom-formatter (formatter "yyyy-MM-dd"))

(defn format-cents [cents]
  (let
    [bucks (quot cents 100)
     leftover-cents (format "%02d" (mod cents 100))]
    (str bucks decimal-separator leftover-cents currency)))

(defprotocol FormatDate
  (format-date [date] "Format a date in the format used by Voitto"))

(extend-protocol FormatDate
  java.util.Date
  (format-date [date] (format-date (from-date date)))
  
  org.joda.time.LocalDate
  (format-date [date] (unparse-local-date custom-formatter date))
  
  org.joda.time.DateTime
  (format-date [date] (unparse custom-formatter date)))

(defn parse-date [date-str]
  (to-date (parse custom-formatter date-str)))