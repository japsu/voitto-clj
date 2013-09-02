(ns voitto.web.views.base
  (:use voitto.core
        hiccup.core
        hiccup.page))

(defn render-navigation-link [current-section [section href text]]
  (let
    [css-class (if
                 (= section current-section) "active"
                 nil)]
    
    [:li {:class css-class}
     [:a {:href href} text]]))

(defn render-navigation-links [section]
  [:ul.nav.navbar-nav
   (map (partial render-navigation-link section)
       [[:dashboard "/" "Dashboard"]
        [:daybook "/daybook" "Daybook"]
        [:ledger "/ledger" "Ledgers"]
        [:report "/report" "Reports"]])])

(defn base [section content]
  (html
    (html5
      [:head
       [:title app-name]
       [:meta {:name "viewport" :content "width=device-width, initial-scale=1.0"}]
       [:link {:href "/bootstrap.min.css" :rel "stylesheet" :media "screen"}]]
      [:body
       [:div.navbar.navbar-default.navbar-static-top
        [:div.container
         [:div.navbar-header
          [:button.navbar-toggle {:type "button"
                                  :data-toggle "collapse"
                                  :data-target ".navbar-collapse"}
           [:span.icon-bar]
           [:span.icon-bar]
           [:span.icon-bar]]
          [:a.navbar-brand {:href "/"} app-name]]
         [:div.navbar-collapse.collapse
          (render-navigation-links section)]]]
       content
       [:script {:src "/jquery.min.js"}]
       [:script {:src "/bootstrap.min.js"}]])))

(defn respond [req section content]
  {:status 200
   :headers {"Content-Type" "text/html; charset=utf-8"}
   :body (base section content)})