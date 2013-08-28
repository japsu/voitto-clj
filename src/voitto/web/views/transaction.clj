(ns voitto.web.views.transaction
  (use voitto.helpers
       voitto.model
       voitto.web.views.base))

(defn render-transaction-form [event]
  [:form {:role "form" :method "post" :action (transaction-uri event)}
   [:fieldset
    [:legend "Transaction details"]
	  
    [:div.row
     [:div.form-group.col-md-8
      [:label {:for "txn-input-date"} "Date"]
      [:input#txn-input-date.form-control {:type "date" :name "date"}]] 
     [:div.form-group.col-md-4
      [:label "Recurrence"]
      [:div.checkbox
       [:label
        [:input#txn-input-repeat {:type "checkbox" :name "repeat"}]
        "Repeat..."]]]]
    
    [:div.row
     [:div.form-group.col-md-8
      [:label {:for "txn-input-comment"} "Comment"]
      [:input#txn-input-comment.form-control {:type "text" :name "comment"}]] 
     [:div.form-group.col-md-4
      [:label {:for "txn-input-other-party"} "Other party"]
      [:input#txn-input-other-party.form-control.col-md-4 {:type "text" :name "other-party"}]]]]
   
   [:fieldset
    [:legend "Entries"]
    [:div.row
     [:div.form-group.col-md-8
      [:label {:for "txn-input-account"} "Account"]
      [:input#txn-input-account.form-control {:type "text" :name "account[]"}]] 
     [:div.form-group.col-md-4
      [:label {:for "txn-input-sum"} "Sum"]
      [:input#txn-input-sum.form-control.col-md-4 {:type "text" :name "sum[]"}]]]
    [:div.row
     [:div.form-group.col-md-8
      [:input.form-control {:type "text" :name "account[]"}]]
     [:div.form-group.col-md-4
      [:input.form-control.col-md-4 {:type "text" :name "sum[]"}]]]]
  
   [:button {:type "submit" :class "btn btn-success"} "Save"]])

(defn transaction-view [req]
  (respond req :daybook
           [:div#content.container
            (render-transaction-form {})]))

(defn transaction-update-handler [req] nil)