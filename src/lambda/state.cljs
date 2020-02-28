(ns lambda.state
  (:require
   [reagent.core :as r]))

(def outputs (r/atom []))
(def history (r/atom []))
(def command (r/atom ""))
(def index (r/atom 0))
(def config (r/atom {:trace? true
                     :full? false}))
