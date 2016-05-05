(ns daily-cider.test.processor
  (:require [clojure.test :refer :all]
            [daily-cider.processor :refer :all]
            [clojure.java.io :as io]
            [clojure.string :as s]))

(deftest process-kbd-text-test
  (testing "process single kbd"
    (let [processed-text (process-kbd-text "<kbd>C-c C-e</kbd>")]
      (is (= ["C-c C-e"] processed-text))))

  (testing "process sorted kbd"
    (let [processed-text (process-kbd-text "<kbd>C-c C-e</kbd> <br/> <kbd>C-x C-e</kbd>")]
      (is (= ["C-c C-e" "C-x C-e"] processed-text))))

  (testing "process unsorted kbd"
    (let [processed-text (process-kbd-text " <kbd>C-x C-e</kbd> <br/> <kbd>C-c C-e</kbd>")]
      (is (= ["C-c C-e" "C-x C-e"] processed-text)))))

(deftest match->entry-test
  (testing "process local resource"
    (let [test-data (slurp (io/file (io/resource "md/interactive_programming.md")))
          matches   (re-seq regex test-data)
          entries (map match->entry matches)
          sorted-entries (sort-by #(-> % :kbd first) entries)]
      (is (= 45 (count entries)))
      (is (some #{"C-M-x"} (:kbd (first sorted-entries)))))))
