(ns clojure-rest-sample.core
  (:import (java.net URL))
  (:require
     [liberator.core :refer [resource defresource]]
     [ring.middleware.params :refer [wrap-params]]
     [ring.adapter.jetty :refer [run-jetty]]      
     [compojure.core :refer [defroutes ANY]]
     [clojure.java.io :as io]
     [clojure-rest-sample.lib.rest-util :as rest-util]
     [clojure-rest-sample.models.customer :as customer]
     )
  
  (:use
     [korma.core]
     [korma.db]
     )
  )

                                    
(def counter (ref 0))

(defresource parameter [txt]
  :available-media-types ["text/plain"]
  :handle-ok (fn [_] (format "The text is %s" txt)))

(defonce entries (ref {"1" {:a 1 :b 2} "2" {:c [1 2 3 4 5 ] :d [:e :f ]}}))


(defn build-entry-url [request id]
  (URL. (format "%s://%s:%s%s/%s"
                (name (:scheme request))
                (:server-name request)
                (:server-port request)
                (:uri request)
                (str id))))



(defresource list-resource
  :available-media-types ["application/json"]
  :allowed-methods [:get :post]
  :known-content-type? #(rest-util/check-content-type % ["application/json"])
  :malformed? #(rest-util/parse-json % ::data)
  :post! #(let [id (str (inc (rand-int 100000)))]
            (dosync (alter entries assoc id (::data %)))
            {::id id})
  :post-redirect? true
  :location #(build-entry-url (get % :request) (get % ::id))
  :handle-ok (rest-util/create-json (customer/all)))

;  :handle-ok #(map (fn [id] (str (build-entry-url (get % :request) id)))
;                   (keys @entries)))


(defresource entry-resource [id]
  :allowed-methods [:get :put :delete]
  :known-content-type? #(rest-util/check-content-type % ["application/json"])
  :exists? (fn [_]
             (let [e (get @entries id)]
                    (if-not (nil? e)
                      {::entry e})))
  :existed? (fn [_] (nil? (get @entries id ::sentinel)))
  :available-media-types ["application/json"]
  :handle-ok ::entry
  :delete! (fn [_] (dosync (alter entries assoc id nil)))
  :malformed? #(rest-util/parse-json % ::data)
  :can-put-to-missing? false
  :put! #(dosync (alter entries assoc id (::data %)))
  :new? (fn [_] (nil? (get @entries id ::sentinel))))

(defroutes app
           (ANY "/foo" [] (resource :available-media-types ["text/html"]
                                    :handle-ok "<html>Hello, Internet.</html>"))
           (ANY "/hoo" [] (resource :available-media-types ["text/html"]))
           (ANY "/bar" []
                (resource :available-media-types ["text/html"]
                          :handle-ok (fn [_] (format "The counter is %d" @counter))))
           (ANY "/bar2/:txt" [txt] (parameter txt))
           (ANY "/collection/:id" [id] (entry-resource id))
           (ANY "/collection" [] list-resource)
           )

(def handler 
  (-> app 
    (wrap-params)))  

;(defn -main []
;  (run-jetty #'handler {:port 3000}))
;  (println (select customer)))


