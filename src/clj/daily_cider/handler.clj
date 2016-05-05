(ns daily-cider.handler
  (:require [compojure.core :refer [routes wrap-routes]]
            [daily-cider.layout :refer [error-page]]
            [daily-cider.routes.home :refer [home-routes]]
            [daily-cider.routes.cider :refer [cider-routes]]
            [compojure.route :as route]
            [daily-cider.env :refer [defaults]]
            [mount.core :as mount]
            [daily-cider.middleware :as middleware]))

(mount/defstate init-app
                :start ((or (:init defaults) identity))
                :stop  ((or (:stop defaults) identity)))

(def app-routes
  (routes
   (-> #'home-routes
       (wrap-routes middleware/wrap-csrf)
       (wrap-routes middleware/wrap-formats))
   (-> #'cider-routes
       (wrap-routes middleware/wrap-api))
    (route/not-found
      (:body
        (error-page {:status 404
                     :title "page not found"})))))


(defn app [] (middleware/wrap-base #'app-routes))
