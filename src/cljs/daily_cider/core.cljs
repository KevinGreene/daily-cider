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
  (when (not= type (str (get (session/get :tip) "id")))
    (GET (str js/context "/cider/" type)
         {:handler #(do (session/put! :tip %)
                        (when (not= type "daily")
                          (aset js/window "location" (str "/#/" (get % "id")))))})))

(defn get-id-link [id]
  (str  (.replace js/window.location.href (aget js/window.location "hash") "")
        "#/" id))

(defn copy-element [id]
  (.select (.getElementById js/document id))
  (.execCommand js/document "copy"))

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
        [:a.btn.btn-primary.btn-lg {:on-click #(secretary/dispatch! "random")} "Random"]
        (let [link (get-id-link (get tip "id"))]
          [:div.col-md-12 {:style {:margin-top "10px"}}
           [:div "Permalink:    "
            [:div.input-group
             [:span.input-group-btn
              [:button.btn.btn-default {:type "button"
                                        :id "permalink-addon"
                                        :on-click #(copy-element "permalink-input")} "Copy"]]
             [:input.form-control {:aria-describedby "permalink-addon"
                                   :id "permalink-input"
                                   :type "text"
                                   :value link
                                   :readOnly true}]]]])]])]])

;; -------------------------
;; Routes
(secretary/set-config! :prefix "#")

(secretary/defroute "/" []
  (fetch-tip! "daily"))

(secretary/defroute "/random" []
  (fetch-tip! "random"))

(secretary/defroute "/:id" [id]
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
  (r/render [#'home-page] (.getElementById js/document "app")))

(defn init! []
  (load-interceptors!)
  (fetch-tip! "daily")
  (hook-browser-navigation!)
  (mount-components))
