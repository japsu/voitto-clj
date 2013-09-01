(ns voitto.web.views.dashboard
  (use voitto.helpers
       voitto.model
       voitto.db
       voitto.web.views.base
       voitto.web.views.daybook))

(defn dashboard-view [req]
  (let
    [recurrent-and-future-transactions (get-all-transactions)
     recent-transactions               (get-all-transactions)]
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
	                [:strong "Safe to spend "]
                  [:span.text-muted "Total"]]
	               [:div.panel-body
	                [:h1 "365,54 €"]
	                [:p.text-muted "until 2018-10-03"]]]]
	             [:div.col-md-4
	              [:div.panel.panel-default
	               [:div.panel-heading
	                [:strong "Safe to spend "]
                  [:span.text-muted "Daily"]]
	               [:div.panel-body
	                [:h1 "7,39 €/day"]
	                [:p.text-muted "until 2018-10-03"]]]]]
	            [:div.panel.panel-default
	             [:div.panel-heading
	              [:strong "Recurrent and future transactions waiting to happen"]]
	             (render-transaction-table recurrent-and-future-transactions)]
	            [:div.panel.panel-default
	             [:div.panel-heading
	              [:strong "Recent transactions"]]
	             (render-transaction-table recent-transactions)]])))