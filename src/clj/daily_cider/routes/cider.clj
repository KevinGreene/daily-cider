(ns daily-cider.routes.cider
  (:require [compojure.core :refer [defroutes GET context]]
            [ring.util.http-response :as response]
            [clojure.java.io :as io]
            [daily-cider.processor :refer [get-daily-shortcut
                                           get-random-shortcut
                                           get-nth-shortcut]]))

(defroutes cider-routes
  (context "/cider" []
           (GET "/daily" []  (ring.util.response/response (get-daily-shortcut)))
           (GET "/random" [] (response/ok (get-random-shortcut)))
           (GET "/:id" [id] (response/ok (get-nth-shortcut (Integer/parseInt id))))))
