(ns voitto.web.views.transaction
  (use voitto.helpers
       voitto.model
       voitto.web.views.base))

(defn transaction-uri [event]
  (str "/transaction/" (:id event)))

(defn render-transaction-form [event]
  [:form {:role "form" :method "post" :action (transaction-uri event)}
   [:div.form-group
    [:label {:for "txn-input-date"} "Date"]
    [:input#txn-input-date.form-control {:type "date" :name "date"}]]
    
  [:div.form-group
    [:label {:for "txn-input-comment"} "Comment"]
    [:input#txn-input-comment.form-control {:type "text" :name "comment"}]]
    
  [:div.form-group
    [:label {:for "txn-input-other-party"} "Other party"]
    [:input#txn-input-other-party.form-control {:type "text" :name "other-party"}]]
    
  [:button {:type "submit" :class "btn btn-success"} "Save"]])

(defn transaction-view [req]
  (prn req)
  (respond req :daybook
           [:div#content.container
            [:h2 "Transaction"]
            (render-transaction-form {})]))

(defn transaction-update-handler [req] nil)