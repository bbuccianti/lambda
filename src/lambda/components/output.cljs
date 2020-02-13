(ns lambda.components.output
  (:require
   [lambda.semantic :as ui]
   [lambda.state :as state]))

(defn results []
  [:> ui/container
   (when (< @state/index (count @state/outputs))
     [:> ui/segment
      {:size "huge"
       :content (get @state/outputs @state/index)}])])
