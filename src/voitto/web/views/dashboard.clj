(ns voitto.web.views.dashboard
  (use voitto.helpers
       voitto.model
       voitto.web.views.base
       voitto.web.views.daybook))

(defn dashboard-view [req]
  (respond req :dashboard
           [:div#content.container
            [:div.row
             [:div.col-md-4
              [:div.panel.panel-default
               [:div.panel-heading
                [:strong "Assets"]]
               [:ul.list-group
                [:li.list-group-item
                 "Savings"
                 [:strong.pull-right "2524,22 €"]]
                [:li.list-group-item
                 "Checking"
                 [:strong.pull-right "2524,22 €"]]]]]
             [:div.col-md-4
              [:div.panel.panel-default
               [:div.panel-heading
                [:strong "Safe to spend "
                 [:small "Total"]]]
               [:div.panel-body
                [:h1 "365,54 €"]
                [:p.text-muted "until 2018-10-03"]]]]
             [:div.col-md-4
              [:div.panel.panel-default
               [:div.panel-heading
                [:strong "Safe to spend "
                 [:small "Daily"]]]
               [:div.panel-body
                [:h1 "7,39 €/day"]
                [:p.text-muted "until 2018-10-03"]]]]]
            [:div.panel.panel-default
             [:div.panel-heading
              [:strong "Recurrent and future transactions waiting to happen"]]
             (render-event-table example-events)]
            [:div.panel.panel-default
             [:div.panel-heading
              [:strong "Recent transactions"]]
             (render-event-table example-events)]]))