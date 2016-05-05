(ns daily-cider.env
  (:require [clojure.tools.logging :as log]))

(def defaults
  {:init
   (fn []
     (log/info "\n-=[daily-cider started successfully]=-"))
   :stop
   (fn []
     (log/info "\n-=[daily-cider has shutdown successfully]=-"))
   :middleware identity})
