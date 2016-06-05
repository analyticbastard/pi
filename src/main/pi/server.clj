(ns pi.server
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [compojure.handler :as handler]
            [ring.middleware.json :as middleware]
            [pi.core :as pi]
            )
  )

(def app-routes
  (compojure.core/routes
    (POST "/mix" request
      (let [strings (or (get-in request [:params :strings])
                        (get-in request [:body :strings]))]
        (if (and strings (coll? strings))
          {:status 200
           :body   {:result (apply pi/mix strings)}}
          {:status 400
           :body   "Bad request"})))
    (route/not-found "Not Found")))

(def app
  (-> app-routes
      handler/api
      (middleware/wrap-json-body {:keywords? true})
      middleware/wrap-json-response
      ))