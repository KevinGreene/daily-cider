(ns daily-cider.processor
  (:require [clojure.tools.logging :as log]
            [luminus.logger :as logger]
            [clojure.java.io :as io]
            [clojure.string :as s]
            [clj-time.core :as t]
            [clj-http.client :as client]
            [cheshire.core :as json]))

(defonce shortcuts-array (atom []))

(defn day-of-year []
  (let [now (t/now)]
    (-> now
        (t/year)
        (t/date-time)
        (t/interval now)
        (t/in-days))))

(defn get-daily-shortcut []
  (nth @shortcuts-array (-> (day-of-year)
                            (mod (count @shortcuts-array)))))

(defn get-random-shortcut []
  (rand-nth @shortcuts-array ))

(def regex #"<kbd>.*\|.*\n")

(defn process-kbd-text [kbd-text]
  (-> kbd-text
      (s/replace #"</?kbd>" "")
      (s/split #"<br/>")
      (->> (map s/trim))
      (sort)
      (vec)))

(defn match->entry
  [regex-match]
  
  (let [result-array (s/split regex-match #"\|" 2)]
    {:kbd (-> result-array
              first
              process-kbd-text)
     :description (-> result-array
                      second
                      s/trim)}))




(def github-url "https://api.github.com/repos/")
(def cider-user "clojure-emacs")
(def cider-repo "cider")
(def cider-path "doc")

(defn get-contents-of-github-folder [owner repo folder]
  (let [url (str github-url
                 owner "/"
                 repo "/"
                 "contents/"
                 folder)]
    (-> url
        client/get
        :body
        (json/parse-string true)
        (->> (map :download_url)
             (filter (complement nil?))
             (map client/get)
             (map :body)))))


(defn load-shortcuts []
  (let [data (get-contents-of-github-folder cider-user cider-repo cider-path)
        matches   (mapcat #(re-seq regex %) data)]
    (reset! shortcuts-array (vec (map match->entry matches)))))
