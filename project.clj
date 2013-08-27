(defproject voitto "0.1.0-SNAPSHOT"
  :description "A simple yet efficient double-entry bookkeeping system"
  :url "https://github.com/japsu/voitto-clj"
  :license {:name "MIT"
            :url "http://opensource.org/licenses/MIT"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [clj-time "0.6.0"]
                 [hiccup "1.0.4"]
                 [http-kit "2.1.10"]
                 [compojure "1.1.5"]]
  :plugins [[no-man-is-an-island/lein-eclipse "2.0.0"]]
  :main voitto.web.server)
