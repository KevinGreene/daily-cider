(ns daily-cider.env
  (:require [selmer.parser :as parser]
            [clojure.tools.logging :as log]
            [daily-cider.dev-middleware :refer [wrap-dev]]))

(def defaults
  {:init
   (fn []
     (parser/cache-off!)
     (log/info "\n-=[daily-cider started successfully using the development profile]=-"))
   :stop
   (fn []
     (log/info "\n-=[daily-cider has shutdown successfully]=-"))
   :middleware wrap-dev})
