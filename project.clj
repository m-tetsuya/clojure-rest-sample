(defproject clojure-rest-sample "0.1.0-SNAPSHOT"
            :description "FIXME: write description"
            :url "http://example.com/FIXME"
            :license {:name "Eclipse Public License"
                      :url "http://www.eclipse.org/legal/epl-v10.html"}
            :dependencies [
                           [org.clojure/clojure "1.5.1"]
                           [org.clojure/clojure-contrib "1.2.0"]
                           [compojure "1.0.1"]
                           [liberator "0.10.0"]
                           [ring/ring-jetty-adapter "1.1.0-SNAPSHOT"]
                           [ring/ring-devel "1.1.0-SNAPSHOT"]
                           [korma "0.3.0-RC6"]
                           [mysql/mysql-connector-java "5.1.13"]
                           ]
            :plugins [[lein-ring "0.8.8"]]
            :ring {:handler clojure-rest-sample.core/handler}
            )
            
