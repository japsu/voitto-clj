(ns voitto.web.views.helpers)

(defn transaction-uri [{event-id :db/id :or {event-id "new"}}]
  (str "/transaction/" event-id))

(defn transaction-link [event content]
  [:a {:href (transaction-uri event)} content])