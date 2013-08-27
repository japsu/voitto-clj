(ns voitto.web.views.dashboard
  (use voitto.helpers
       voitto.model
       voitto.web.views.base))

(defn dashboard-view [req]
  (respond req :dashboard
           [:div#content.container
            [:h2 "Dashboard goes here"]]))