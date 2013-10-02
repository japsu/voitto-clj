(ns voitto.web.views.ledger
  (:use [hiccup.util :only [escape-html] :as hutil]
        voitto.helpers
        voitto.model
        voitto.db
        voitto.web.views.base
        voitto.web.views.daybook
        voitto.web.views.uris))

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
                      (cents->str))
     is-other    (comp (partial not= cur-account) :entry/account)]
    
    [:tr {:class css-classes}
     [:td date-link]
     [:td txn-link]
     [:td oparty-link]
     (display-account-in-table is-other transaction)
     [:td.text-right total]]))

(defn render-ledger-table [cur-account transactions]
  [:table.table.table-striped
   [:thead
    [:tr
     [:th "Date"]
     [:th "Comment"]
     [:th "Other party"]
     [:th "Other account"]
     [:th.text-right "Sum"]]]
   [:tbody
    (map (partial render-transaction-in-ledger cur-account) transactions)]])

(defn ledger-view [req]
  (let
    [params       (parse-params ledger-view-params (req :params))
     account      (params :account)
     account-name (escape-html (:account/name account))
	   transactions (query-entities '[:find ?txn
                                    :in $ ?acc ?from ?to
                                    :where
                                   
                                    [?txn :transaction/entry ?etr]
                                    [?etr :entry/account ?acc]
                                   
                                    [?txn :transaction/date ?date]
                                    [(>= ?date ?from)]
                                    [(<= ?date ?to)]]
                                  (:db/id account)
                                  (:from params)
                                  (:to params))]

    (respond req :ledger
           [:div#content.container
            [:h1 "Ledger "
             [:small account-name]]
            (render-ledger-table account transactions)])))            
  