(ns clojure-rest-sample.models.customer
  (:use
     [korma.core]
     [korma.db]
     )
  )

(defdb testdb 
       (mysql 
         {:db "litera_10080" :host "localhost" :port "3306"  :user "clj" :password "clj"}))

(defentity customer)
(defn all []
  (select customer))

