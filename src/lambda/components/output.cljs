(ns lambda.components.output
  (:require
   [goog.dom :as gdom]
   [reagent.core :as r]
   [lambda.state :as state]
   [lambda.semantic :as ui]
   [lambda.stringy :refer [toString]]))

(defn handle-copy [text]
  (let [input (gdom/getElement "input")
        old @state/command]
    (reset! state/command text)
    (js/setTimeout #(do (.select input)
                        (js/document.execCommand "copy")
                        (reset! state/command old))
                   15)))

(defn make-segment [input]
  [:> ui/segment-group
   {:horizontal true}
   [:> ui/segment
    {:size "huge"
     :textAlign "center"
     :content input}]
   [:> ui/popup
    {:content "Copiado!"
     :on "click"
     :pinned true
     :position "left center"
     :trigger
     (r/as-component [:> ui/button
                      {:attached "right"
                       :color "teal"
                       :icon "copy"
                       :size "huge"
                       :compact true
                       :style {:paddingTop "1.2rem"}
                       :onClick #(handle-copy input)}])}]])

(defn results []
  (when (< @state/index (count @state/outputs))
    (let [cmd (get @state/outputs @state/index)]
      [:> ui/container
       {:style {:paddingTop "50px"}}
       (if (:trace? @state/config)
         (doall
          (for [r (:reductions cmd)]
            ^{:key (gensym "out")}
            [make-segment (toString r (:full? @state/config))]))
         [make-segment (-> cmd :reductions last
                           (toString (:full? @state/config)))])])))
