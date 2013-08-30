(ns voitto.web.views.daybook
  (:use [hiccup.util :only [escape-html] :as hutil]
        voitto.helpers
        voitto.model
        voitto.db
        voitto.web.views.base
        voitto.web.views.transaction
        voitto.web.views.helpers))

(defn daybook-toolbar []
  [:div.btn-toolbar.pull-right
   [:div.btn-group
    [:a.btn.btn-success {:href "/transaction/new"} "New transaction"]]])

(def daybook-view-params
  {:from {:format format-date :parse parse-date}
   :to   {:format format-date :parse parse-date}})

(format-params daybook-view-params {:from (java.util.Date.)})

(defn daybook-uri [params]
  (->> params
       (format-params daybook-view-params)
       (hutil/url "/daybook")
       (hutil/to-str)))

(defn date-link [date]
  [:a {:href (daybook-uri {:from date :to date})} (format-date date)])

(defn display-account-in-table [pred event]
  (let
    [matching-entries (->> (:event/entry event)
                           (filter (comp pred :entry/cents)))]
       
    (case (count matching-entries)
      0     [:td.text-muted "Missing"]
      1     [:td (->> matching-entries
                      (first)
                      (:entry/account)
                      (escape-html)
                      (ledger-link))]
            [:td.text-muted "Split"])))

(defn render-event-in-table [event]
  (let
    [css-classes (if  (not (balanced-event? event)) "danger" nil)
     date-link   (->> (:event/date event)
                      (date-link))
     txn-link    (->> (:event/comment event)
                      (escape-html)
                      (transaction-link event))
     ;oparty-link (->> (:event/otherParty event)
     ;                 (other-party-link))
     oparty-link (->> (:event/otherParty event)
                      (escape-html)
                      (transaction-link event))
     total       (->> (event-total event)
                      (format-cents))]
    
    [:tr {:class css-classes}
     [:td date-link]
     [:td txn-link]
     [:td oparty-link]
     (display-account-in-table neg? event)
     (display-account-in-table pos? event)
		 [:td.text-right total]]))

(defn render-event-table [events]
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
     (map render-event-in-table events)]])

(defn daybook-view [req]
  (respond req :daybook
           [:div#content.container
            (daybook-toolbar) 
            [:h1 "Daybook"]
            (render-event-table (get-events))]))