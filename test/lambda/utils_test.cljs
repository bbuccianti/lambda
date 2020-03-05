(ns lambda.utils-test
  (:require
   [cljs.test :refer [deftest are use-fixtures]]
   [reagent.core :as r]
   [lambda.state :as state]
   [lambda.utils :refer [wrapped-inc wrapped-dec]]))

(use-fixtures :once
  {:before #(swap! state/outputs conj 1 2 3)
   :after #(reset! state/outputs [])})

(deftest history-wrapped-dec
  (are [exp act] (= exp (wrapped-dec act))
    0 1
    9 10
    3 0))

(deftest history-wrapped-inc
  (are [exp act] (= exp (wrapped-inc act))
    1 0
    3 2
    3 3
    3 4))
