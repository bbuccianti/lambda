(ns lambda.components.output
  (:require
   [lambda.semantic :as ui]
   [lambda.state :as state]))

(defn results []
  (when (< @state/index (count @state/outputs))
    (let [cmd (get @state/outputs @state/index)]
      [:> ui/container
       {:style {:paddingTop "50px"}}
       [:> ui/segment
        {:size "huge"
         :content (:command cmd)}]
       [:> ui/segment
        {:size "huge"
         :content (:reduced cmd)}]])))
