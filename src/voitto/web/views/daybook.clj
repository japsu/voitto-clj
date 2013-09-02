(ns voitto.web.views.daybook
  (:use [hiccup.util :only [escape-html] :as hutil]
        voitto.helpers
        voitto.model
        voitto.db
        voitto.web.views.base
        voitto.web.views.uris))

(defn daybook-toolbar []
  [:div.btn-toolbar.pull-right
   [:div.btn-group
    [:a.btn.btn-success {:href "/transaction/new"} "New transaction"]]])

(defn display-account-in-table [pred transaction]
  (let
    [matching-entries (->> (:transaction/entry transaction)
                           (filter pred))]
       
    (case (count matching-entries)
      0     [:td.text-muted "Missing"]
      1     [:td (->> matching-entries
                      (first)
                      (:entry/account)
                      (ledger-link))]
            [:td.text-muted "Split"])))

(defn render-transaction-in-table [transaction]
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
     total       (->> (transaction-total transaction)
                      (format-cents))]
    
    [:tr {:class css-classes}
     [:td date-link]
     [:td txn-link]
     [:td oparty-link]
     (display-account-in-table (comp neg? :entry/cents) transaction)
     (display-account-in-table (comp pos? :entry/cents) transaction)
		 [:td.text-right total]]))

(defn render-transaction-table [transactions]
  [:table.table.table-striped
    [:thead
     [:tr
      [:th "Date"]
      [:th "Comment"]
      [:th "Other party"]
      [:th "From account"]
      [:th "To account"]
      [:th.text-right "Sum"]]]
    [:tbody
     (map render-transaction-in-table transactions)]])

(defn daybook-view [req]
  (let
    [params       (parse-params daybook-view-params (req :params))
     transactions (query-entities '[:find ?txn
                                    :in $ ?from ?to
                                    :where
                                    [?txn :transaction/date ?date]
                                    [(>= ?date ?from)]
                                    [(<= ?date ?to)]]
                                  (params :from)
                                  (params :to))]
    (respond req :daybook
           [:div#content.container
            (daybook-toolbar) 
            [:h1 "Daybook "
             [:small (to-from-on params)]]
            (render-transaction-table transactions)])))