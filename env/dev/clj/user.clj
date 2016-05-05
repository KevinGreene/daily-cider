(ns user
  (:require [mount.core :as mount]
            [daily-cider.figwheel :refer [start-fw stop-fw cljs]]
            daily-cider.core))

(defn start []
  (mount/start-without #'daily-cider.core/repl-server))

(defn stop []
  (mount/stop-except #'daily-cider.core/repl-server))

(defn restart []
  (stop)
  (start))


