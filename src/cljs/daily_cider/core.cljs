(ns daily-cider.core
  (:require [reagent.core :as r]
            [reagent.session :as session]
            [secretary.core :as secretary :include-macros true]
            [goog.events :as events]
            [goog.history.EventType :as HistoryEventType]
            [markdown.core :refer [md->html]]
            [daily-cider.ajax :refer [load-interceptors!]]
            [ajax.core :refer [GET POST]])
  (:import goog.History))

(defn about-page []
  [:div.container
   [:div.row
    [:div.col-md-12
     "this is the story of daily-cider... work in progress"]]])

(defn home-page []
  [:div.container
   [:div {:style {:margin-top "100px"}}
    [:h1.title "Daily Cider"]
    (when-let [tip (session/get :tip)]
      [:div.tips {:style {:text-align "center"
                          :margin-top "50px"}}
       [:div.col-md-12 
        (for [kbd (get tip "kbd")]
          [:h2 [:kbd kbd]])]
       [:div.col-large-offset-4.col-large-4.col-md-offset-2.col-md-8
        {:style {:margin-top "20px"}}
        [:h3 (get tip "description")]]])]])

(def pages
  {:home #'home-page
   :about #'about-page})

(defn page []
  [(pages (session/get :page))])

;; -------------------------
;; Routes
(secretary/set-config! :prefix "#")

(secretary/defroute "/" []
  (session/put! :page :home))

(secretary/defroute "/about" []
  (session/put! :page :about))

;; -------------------------
;; History
;; must be called after routes have been defined
(defn hook-browser-navigation! []
  (doto (History.)
        (events/listen
          HistoryEventType/NAVIGATE
          (fn [event]
              (secretary/dispatch! (.-token event))))
        (.setEnabled true)))

;; -------------------------
;; Initialize app

(defn fetch-tip! []
  (GET (str js/context "/cider/daily") {:handler #(session/put! :tip %)}))

(defn mount-components []
  (r/render [#'page] (.getElementById js/document "app")))

(defn init! []
  (load-interceptors!)
  (fetch-tip!)
  (hook-browser-navigation!)
  (mount-components))
