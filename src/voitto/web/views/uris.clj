(ns voitto.web.views.uris
  (:use [hiccup.util :only [escape-html] :as hutil]
        voitto.helpers
        voitto.model
        voitto.db))

(defn format-param [param-descr formatted-params [key value]]
  (let
    [{:keys [format] :or {format identity}} (param-descr key {:format identity})]
    (assoc formatted-params key (format value))))

(defn parse-param [params parsed-params [key param-descr]]
  (let
    [user-value   (params key)
     parse        (param-descr :parse identity)
     default      (param-descr :default (constantly nil))
     parsed-value (if
                    (nil? user-value) (default)
                    (parse user-value))]
    (if
      (nil? parsed-value) parsed-params
      (assoc parsed-params key parsed-value))))

(defn format-params [param-descr params]
  (reduce (partial format-param param-descr) {} params))

(defn parse-params [param-descr params]
  (reduce (partial parse-param params) {} param-descr))


(def daybook-view-params
  {:from {:format format-date :parse parse-date :default get-current-fiscal-year-start}
   :to   {:format format-date :parse parse-date :default get-current-fiscal-year-end}})

(defn daybook-uri [params]
  (->> params
       (format-params daybook-view-params)
       (hutil/url "/daybook")
       (hutil/to-str)))

(defn date-link [date]
  [:a {:href (daybook-uri {:from date :to date})} (format-date date)])


(defn ledger-uri [{ident :account/ident}]
  (str "/ledger/" (name ident)))

(defn ledger-link [{name :account/name :as account}]
  [:a {:href (ledger-uri account)} (escape-html name)])

(def ledger-view-params
  {:account {:parse get-account :format (comp name :account/ident)}
   :from    {:parse parse-date :format format-date :default get-current-fiscal-year-start}
   :to      {:parse parse-date :format format-date :default get-current-fiscal-year-end}})


(defn transaction-uri [{transaction-id :db/id :or {transaction-id "new"}}]
  (str "/transaction/" transaction-id))

(defn transaction-link [transaction content]
  [:a {:href (transaction-uri transaction)} content])