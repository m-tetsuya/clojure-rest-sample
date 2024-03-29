(ns clojure-rest-sample.lib.rest-util
  (:require
     [clojure.java.io :as io]
     [clojure.data.json :as json]
     ))

;; convert the body to a reader. Useful for testing in the repl
;; where setting the body to a string is much simpler.
(defn body-as-string [ctx]
  (if-let [body (get-in ctx [:request :body])]
    (condp instance? body
      java.lang.String body
      (slurp (io/reader body)))))

;; For PUT and POST parse the body as json and store in the context
;; under the given key.
(defn parse-json [context key]
  (when (#{:put :post} (get-in context [:request :request-method]))
    (try
      (if-let [body (body-as-string context)]
        (let [data (json/read-str body)]
          [false {key data}])
        {:message "No body"})
      (catch Exception e
        (.printStackTrace e)
        {:message (format "IOException: " (.getMessage e))}))))

;; For PUT and POST check if the content type is json.
(defn check-content-type [ctx content-types]
  (if (#{:put :post} (get-in ctx [:request :request-method]))
    (or
     (some #{(get-in ctx [:request :headers "content-type"])}
           content-types)
     [false {:message "Unsupported Content-Type"}])
    true))

(defn my-value-writer [key value]
  (if ( or (instance? java.sql.Timestamp value) (instance? java.sql.Date value))
    (str (java.sql.Date. (.getTime value)))
    value))

(defn create-json [x]
                  (json/write-str x
                                  :value-fn my-value-writer
                                  :key-fn name))
