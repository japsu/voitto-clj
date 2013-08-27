(ns voitto.web.views.daybook
  (use voitto.helpers
       voitto.model
       voitto.web.views.base))

(defn render-simple-event-in-table [event]
  {:pre [(simple-event? event)]}
  (let
    [from-account (->> (:entries event)
                       (find-first (comp neg? :cents))
                       (:account))
     to-account   (->> (:entries event)
                       (find-first (comp pos? :cents))
                       (:account))
     total        (->> (event-total event)
                       (format-cents))]
    
    [:tr
	   [:td (format-date (:date event))]
	   [:td (:comment event)]
	   [:td (:other-party event)]
	   [:td from-account]
	   [:td to-account]
	   [:td.text-right total]]))

(defn render-split-event-in-table [event]
  {:pre [(split-event? event)]}
  
  [:tr
   [:td (format-date (:date event))]
   [:td (:comment event)]
   [:td (:other-party event)]
   [:td.muted {:colspan 2} "Split event"]
   [:td.text-right (format-cents (event-total event))]])

(defn render-invalid-event-in-table [event]
  {:pre [(not (balanced-event? event))]}
  
  [:tr.danger
   [:td (format-date (:date event))]
   [:td (:comment event)]
   [:td (:other-party event)]
   [:td.muted {:colspan 3} "Unbalanced event"]])

(defn render-event-in-table [event]
  (cond
    (simple-event? event) (render-simple-event-in-table event)
    (split-event? event) (render-split-event-in-table event)
    :else (render-invalid-event-in-table event)))

(defn render-event-table [events]
  [:div#content.container
   [:h2 "Daybook"]
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
     (map render-event-in-table events)]]])

(defn daybook-view [req]
  (respond req :daybook (render-event-table example-events)))