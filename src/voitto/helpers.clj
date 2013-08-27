(ns voitto.helpers
  (use [clj-time.format :only [formatter unparse-local-date]]))

(defn find-first [f coll]
  (first (filter f coll)))

;; TODO localization
(def decimal-separator ",")
(def currency " €")
(def custom-formatter (formatter "yyyy-MM-dd"))

(defn format-cents [cents]
  (let
    [bucks (quot cents 100)
     leftover-cents (format "%02d" (mod cents 100))]
    (str bucks decimal-separator leftover-cents currency)))

(defn format-date [date]
  (unparse-local-date custom-formatter date))
