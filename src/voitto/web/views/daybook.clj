(ns voitto.web.views.daybook
  (use voitto.helpers
       voitto.model
       voitto.db
       voitto.web.views.base))

(defn render-simple-event-in-table [event]
  {:pre [(simple-event? event)]}
  (let
    [from-account (->> (:event/entry event)
                       (find-first (comp neg? :entry/cents))
                       (:entry/account)
                       (:account/name))
     to-account   (->> (:event/entry event)
                       (find-first (comp pos? :entry/cents))
                       (:entry/account)
                       (:account/name))
     total        (->> (event-total event)
                       (format-cents))]
    
    [:tr
	   [:td (format-date (:event/date event))]
	   [:td (:event/comment event)]
	   [:td (:event/otherParty event)]
	   [:td from-account]
	   [:td to-account]
	   [:td.text-right total]]))

(defn render-split-event-in-table [event]
  {:pre [(split-event? event)]}
  
  [:tr
   [:td (format-date (:event/date event))]
   [:td (:event/comment event)]
   [:td (:event/otherParty event)]
   [:td.text-muted "Split"]
   [:td.text-muted "Split"]
   [:td.text-right (format-cents (event-total event))]])

(defn render-invalid-event-in-table [event]
  {:pre [(not (balanced-event? event))]}
  [:tr.danger
   [:td (format-date (:event/date event))]
   [:td (:event/comment event)]
   [:td (:event/otherParty event)]
   [:td {:colspan 3}]])

(defn render-event-in-table [event]
  (cond
    (simple-event? event) (render-simple-event-in-table event)
    (split-event? event)  (render-split-event-in-table event)
    :else                 (render-invalid-event-in-table event)))

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