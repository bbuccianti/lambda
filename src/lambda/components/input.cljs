(ns lambda.components.input
  (:require
   [goog.dom :as gdom]
   [lambda.semantic :as ui]
   [lambda.state :as state]
   [lambda.instaparser :as instaparser]
   [lambda.utils :refer [get-reductions
                         handle-history-changes
                         reset-and-restore]]))

(defn handle-action []
  (let [new-input {:command @state/command
                   :reductions (get-reductions @state/command)}]
    (swap! state/outputs conj new-input)
    (reset! state/index (dec (count @state/outputs)))
    (reset-and-restore (gdom/getElement "input") "" 0)))

(defn insert-lambda [e]
  (let [input (gdom/getElement "input")
        idx (.-selectionStart input)
        left (subs @state/command 0 idx)
        right (subs @state/command idx)
        command (str left "λ" right)]
    (.preventDefault e)
    (reset-and-restore input command (inc idx))))

(defn handle-key-press [e]
  (case (.-key e)
    "\\" (insert-lambda e)
    "Enter" (handle-action)
    nil))

(defn make-arrow [direction]
  [:> ui/button
    {:attach "bottom"
     :icon (str "arrow " direction)
     :compact true
     :onClick
     #(handle-history-changes
       (if (= direction "right") "ArrowDown" "ArrowUp"))}])

(defn toggle-button [name k]
  [:> ui/button
    {:attach "bottom"
     :content name
     :compact true
     :floated "right"
     :color (if (k @state/config) "green" "red")
     :onClick #(swap! state/config update k not)}])

(defn readline []
  [:> ui/container
   {:style {:paddingTop "15px"
            :position "relative"}}
   [:> ui/form
    [:> ui/input
     {:id "input"
      :placeholder "Insertá una expresión lambda!"
      :fluid true
      :input {:autoComplete "off"}
      :error (instaparser/failure?)
      :size "huge"
      :default-value @state/command
      :onKeyPress #(handle-key-press %)
      :onKeyUp #(handle-history-changes (.-key %))
      :onChange #(reset! state/command (.. % -target -value))
      :action {:content "Evaluar"
               :onClick handle-action
               :disabled (instaparser/failure?)}
      :style {:margin-bottom "5px"}}]
    [:> ui/container
     {:textAlign "center"
      :style {:position "absolute"}}
     [instaparser/help-label]]
    [make-arrow "left"]
    [make-arrow "right"]
    [:> ui/button
     {:attach "bottom"
      :content "λ"
      :compact true
      :basic true
      :onClick insert-lambda}]
    [toggle-button "Trace" :trace?]
    [toggle-button "Full" :full?]
    [toggle-button "Índices" :index?]]])

