(ns daily-cider.doo-runner
  (:require [doo.runner :refer-macros [doo-tests]]
            [daily-cider.core-test]))

(doo-tests 'daily-cider.core-test)

