(ns voitto.web.views.ledger
  (:use [hiccup.util :only [escape-html] :as hutil]
        voitto.helpers
        voitto.model
        voitto.db
        voitto.web.views.base
        voitto.web.views.daybook
        voitto.web.views.transaction
        voitto.web.views.helpers))

(defn render-transaction-in-ledger [cur-account transaction]
  (let
    [css-classes (if  (not (balanced-transaction? transaction)) "danger" nil)
     date-link   (->> (:transaction/date transaction)
                      (date-link))
     txn-link    (->> (:transaction/comment transaction)
                      (escape-html)
                      (transaction-link transaction))
     oparty-link (->> (:transaction/otherParty transaction)
                      (escape-html)
                      (transaction-link transaction))
     total       (->> (:transaction/entry transaction)
                      (filter (comp (partial = cur-account) :entry/account))
                      (map :entry/cents)
                      (reduce + 0)
                      (format-cents))]
    
    [:tr {:class css-classes}
     [:td date-link]
     [:td txn-link]
     [:td oparty-link]
     (display-account-in-table (comp (partial not= cur-account) :entry/account) transaction)
     [:td.text-right total]]))

(defn render-ledger-table [params]
  (let
    [account      (params :account)
     transactions (query-entities [:find '?txn
                                   :where
                                   ['?txn :transaction/entry '?etr]
                                   ['?etr :entry/account (:db/id account)]])]
    
    [:table.table.table-striped
     [:thead
      [:tr
       [:th "Date"]
       [:th "Comment"]
       [:th "Other party"]
       [:th "Other account"]
       [:th.text-right "Sum"]]]
     [:tbody
      (map (partial render-transaction-in-ledger account) transactions)]]))

(def ledger-view-params
  {:account {:parse get-account :format (comp name :account/ident)}
   :from    {:parse parse-date :format format-date}
   :to      {:parse parse-date :format format-date}})

(defn ledger-view [req]
  (let
    [params       (parse-params ledger-view-params (:params req))
     account-name (->> (params :account)
                       (:account/name)
                       (escape-html))]
    (respond req :ledger
           [:div#content.container
            [:h1 "Ledger "
             [:small account-name]]
            (render-ledger-table params)])))            
  