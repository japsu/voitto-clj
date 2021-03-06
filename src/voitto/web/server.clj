(ns voitto.web.server
  (:use [compojure.route :only [files not-found]]
        [compojure.handler :only [site]]
        [compojure.core :only [defroutes GET POST DELETE ANY context]]
        org.httpkit.server
        voitto.helpers
        voitto.web.views.base
        voitto.web.views.daybook
        voitto.web.views.dashboard
        voitto.web.views.transaction
        voitto.web.views.ledger
        voitto.model))

(def error-404 (base nil
                 [:div.container
                  [:h1 "Not found"] 
                  [:p "The requested resource was not found."]]))

(defroutes routes
  (GET "/" [] dashboard-view)
  (GET "/daybook" [] daybook-view)
  (GET "/ledger/:account" [account] ledger-view)
  (context "/transaction/:transaction" [transaction]
           (GET "/" [] transaction-view)
           (POST "/" [] transaction-update-handler))
  (files "/")
  (not-found error-404))

(defonce server
  (run-server (site #'routes) {:port 8000}))
