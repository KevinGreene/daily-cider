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

(defn fetch-tip! [type]
  (GET (str js/context "/cider/" type)
       {:handler #(session/put! :tip %)}))

(defn set-tip-type [type]
  (secretary/dispatch! type)
  (aset js/window "location" (str "/#/" type)))

(defn get-id-link [id]
  (str  (.replace js/window.location.href (aget js/window.location "hash") "")
        "#/" id))

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
        [:h3 (get tip "description")]]
       [:div.col-md-12
        [:a.btn.btn-primary.btn-lg {:on-click #(set-tip-type "random")} "Random"]
        [:p (get-id-link (get tip "id"))]]])]])

(def pages
  {:home #'home-page})

(defn page []
  [(pages (session/get :page))])

;; -------------------------
;; Routes
(secretary/set-config! :prefix "#")

(secretary/defroute "/" []
  (session/put! :page :home))

(secretary/defroute "/random" []
  (session/put! :page :home)
  (fetch-tip! "random"))

(secretary/defroute "/:id" [id]
  (session/put! :page :home)
  (fetch-tip! id))

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

(defn mount-components []
  (r/render [#'page] (.getElementById js/document "app")))

(defn init! []
  (load-interceptors!)
  (fetch-tip! "daily")
  (hook-browser-navigation!)
  (mount-components))
