(ns voitto.web.views.daybook
  (use voitto.helpers
       voitto.model
       voitto.db
       voitto.web.views.base
       voitto.web.views.helpers))

(defn display-account-in-table [pred event]
  (let
    [matching-entries (->> (:event/entry event)
                           (filter (comp pred :entry/cents)))]
       
    (case (count matching-entries)
      0     [:td.text-muted "Missing"]
      1     [:td (->> matching-entries
                      (first)
                      (:entry/account)
                      (:account/name))]
      :else [:td.text-muted "Split"])))

(defn render-event-in-table [event]
  (let
    [css-classes (cond
                      (not (balanced-event? event)) "danger"
                      :else nil)
     total       (->> (event-total event)
                      (format-cents))]
    
    [:tr {:class css-classes}
     [:td (format-date (:event/date event))]
     [:td (transaction-link event (:event/comment event))]
     [:td (:event/otherParty event)]
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

(defn daybook-toolbar []
  [:div.btn-toolbar.pull-right
   [:div.btn-group
    [:a.btn.btn-success {:href "/transaction/new"} "New transaction"]]])

(defn daybook-view [req]
  (respond req :daybook
           [:div#content.container
            (daybook-toolbar) 
            [:h1 "Daybook"]
            (render-event-table (get-events))]))