(ns voitto.web.views.transaction
  (use voitto.helpers
       voitto.model
       voitto.db
       voitto.web.views.base
       voitto.web.views.helpers))

(defn input [{:keys [label name type value] :or {type "text" value ""}}]
  (let
    [elem-id (str "input-" name)]
	  (seq [[:label {:for elem-id} label]
          [:input.form-control {:id elem-id :type type :name name :value value}]])))

(defn transaction-toolbar []
  [:div.btn-toolbar.pull-right
   [:div.btn-group
    [:a.btn.btn-default {:href "/daybook"} "Back to daybook"]
    [:button.btn.btn-danger {:type "submit" :name "_method" :value "delete"} "Delete"]
    [:button.btn.btn-success {:type "submit" :name "_method" :value "post"} "Save"]]])

(defn render-transaction-form [event]
  (let
    [date        (->> (or (:event/date event)
                          (java.util.Date.))
                      (format-date))
     comment     (or  (:event/comment event)
                      "")
     other-party (or  (:event/otherParty event)
                      "")]
    
    [:form {:role "form" :method "post" :action (transaction-uri event)}
     (transaction-toolbar)
     [:legend "Transaction details"]
	   [:fieldset
	    [:div.row
	     [:div.form-group.col-md-8 (input {:label "Date" :name "date" :type "date" :value date})] 
	     [:div.form-group.col-md-4
	      [:label "Recurrence"]
	      [:div.checkbox
	       [:label
	        [:input#txn-input-repeat {:type "checkbox" :name "repeat"}]
	        "Repeat..."]]]]
	    
	    [:div.row
	     [:div.form-group.col-md-8 (input {:label "Comment" :name "comment" :value comment})] 
	     [:div.form-group.col-md-4 (input {:label "Other party" :name "other-party" :value other-party})]]]
	   
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
	      [:input.form-control.col-md-4 {:type "text" :name "sum[]"}]]]]]))

(defn transaction-view [req]
  (let
    [event-id (get-in req [:params :event-id])
     event    (case event-id
                    "new" {}
                    (->> (get-in req [:params :event-id])
                         (Long.)
                         (get-event)))]
    
    (respond req :daybook
             [:div#content.container
              (render-transaction-form event)])))

(defn transaction-update-handler [req]
  (let
    [event ()]
    @(d/transact [event] (db @conn))
    