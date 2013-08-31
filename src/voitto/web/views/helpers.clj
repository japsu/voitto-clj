(ns voitto.web.views.helpers
  (:use [hiccup.util :only [escape-html url] :as hutil]
        voitto.helpers))

(defn format-param [param-descr formatted-params [key value]]
  (let
    [{:keys [format] :or {format identity}} (param-descr key {:format identity})]
    (assoc formatted-params key (format value))))

(defn parse-param [param-descr parsed-params [key value]]
  (let
    [{:keys [parse] :or {parse identity}} (param-descr key {:parse identity})]
    (assoc parsed-params key (parse value))))

(defn format-params [param-descr params]
  (reduce (partial format-param param-descr) {} params))

(defn parse-params [param-descr params]
  (reduce (partial parse-param param-descr) {} params))

(defn ledger-uri [{ident :account/ident}]
  (str "/ledger/" ident))

(defn ledger-link [{name :account/name :as account}]
  [:a {:href (ledger-uri account)} (escape-html name)])

