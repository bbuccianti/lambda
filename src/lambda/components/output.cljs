(ns lambda.components.output
  (:require
   [goog.dom :as gdom]
   [reagent.core :as r]
   [clojure.string :refer [split replace]]
   [lambda.state :as state]
   [lambda.semantic :as ui]
   [lambda.stringy :refer [toString]]))

(defn clean-index [text]
  (if (re-matches #".*_\d+.*" text)
    (replace text #"_\d+" "")
    text))

(defn handle-copy [text label]
  (let [input (gdom/getElement "input")
        old @state/command]
    (reset! state/command (clean-index text))
    (js/setTimeout (fn []
                     (.select input)
                     (if (js/document.execCommand "copy")
                       (reset! label "Copiado!")
                       (reset! label "Error!"))
                     (reset! state/command old))
                   10)))

(defn make-sub [value]
  ^{:key (gensym "sub")}
  [:sub {:style {:fontSize "0.7rem"}} (rest value)])

(defn fix-index [expression]
  (if (re-matches #".*_\d+.*" expression)
    (butlast
     (interleave (split expression #"_\d+")
                 (cycle (map make-sub (re-seq #"_\d+" expression)))))
    expression))

(defn copy-button [input copy-msg]
  [:> ui/button
   {:attached "right"
    :color "teal"
    :icon "copy"
    :size "huge"
    :compact true
    :style {:paddingTop "1.2rem"}
    :onClick #(handle-copy input copy-msg)}])

(defn make-segment [input]
  (let [copy-msg (r/atom "")]
    (fn []
      [:> ui/segment-group
       {:horizontal true}
       [:> ui/segment
        {:size "huge"
         :textAlign "center"}
        (if (:index? @state/config)
          (fix-index input)
          input)]
       [:> ui/popup
        {:on "click"
         :pinned true
         :position "left center"
         :onClose #(reset! copy-msg "")
         :trigger
         (r/as-component (copy-button input copy-msg))}
        (if (empty? @copy-msg)
          [:> ui/placeholder {:style {:minWidth "50px"}}
           [:> ui/placeholder-line {:length "large"}]]
          [:<> [:p @copy-msg]])]])))

(defn results []
  (when (< @state/index (count @state/outputs))
    (let [cmd (get @state/outputs @state/index)]
      [:> ui/container
       {:style {:paddingTop "2rem"}}
       (if (:trace? @state/config)
         (doall
          (for [r (:reductions cmd)]
            ^{:key (gensym "out")}
            [make-segment (toString r)]))
         [make-segment (-> cmd :reductions last toString)])
       [:> ui/button
        {:attach "bottom"
         :compact true
         :content "Reportá errores!"
         :color "blue"
         :floated "right"
         :as "a"
         :target "_blank"
         :href "https://todo.sr.ht/~bbuccianti/lambda"}]
       [:p "v0.92"]])))


