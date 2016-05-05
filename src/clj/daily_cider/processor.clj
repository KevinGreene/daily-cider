(ns daily-cider.processor
  (:require [clojure.tools.logging :as log]
            [luminus.logger :as logger]
            [clojure.java.io :as io]
            [clojure.string :as s]
            [clj-time.core :as t]))

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

(defn load-shortcuts []
  (let [test-data (slurp (io/file (io/resource "md/interactive_programming.md")))
        matches   (re-seq regex test-data)]
    (reset! shortcuts-array (vec (map match->entry matches)))))




