(ns lambda.components.input
  (:require
   [goog.dom :as gdom]
   [lambda.semantic :as ui]
   [lambda.state :as state]
   [lambda.utils :refer [wrapped-inc wrapped-dec
                         get-reductions
                         reset-and-restore]]))

(defn handle-action []
  (let [new-input {:command @state/command
                   :reductions (get-reductions @state/command)}]
    (swap! state/outputs conj new-input)
    (reset! state/index (dec (count @state/outputs)))))

(defn insert-lambda [e]
  (let [input (gdom/getElement "input")
        idx (.-selectionStart input)
        left (subs @state/command 0 idx)
        right (subs @state/command idx)]
    (.preventDefault e)
    (reset-and-restore input (str left "λ" right) (inc idx))))

(defn handle-key-press [e]
  (case (.-key e)
    "\\" (insert-lambda e)

    "Enter" (handle-action)

    nil))

(defn swap-history-and-input [f]
  (swap! state/index f)
  (if-let [old (get @state/outputs @state/index)]
    (reset! state/command (:command old))
    (reset! state/command "")))

(defn handle-history-changes [key]
  (case key
    "ArrowUp"   (swap-history-and-input wrapped-dec)
    "ArrowDown" (swap-history-and-input wrapped-inc)
    nil))

(defn handle-on-change [e]
  (let [input (gdom/getElement "input")
        idx (.-selectionStart input)]
    (reset-and-restore input (.. e -target -value) idx)))

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
   {:style {:paddingTop "15px"}}
   [:> ui/input
    {:id "input"
     :placeholder "Insertá una expresión lambda!"
     :fluid true
     :size "huge"
     :value @state/command
     :onKeyPress #(handle-key-press %)
     :onKeyUp #(handle-history-changes (.-key %))
     :onChange handle-on-change
     :onFocus #(swap! state/config assoc :report? false)
     :onBlur #(swap! state/config assoc :report? true)
     :action {:content "Evaluar" :onClick handle-action}
     :style {:margin-bottom "5px"}}]
   [make-arrow "left"]
   [make-arrow "right"]
   [:> ui/button
    {:attach "bottom"
     :content "λ"
     :compact true
     :basic true
     :onClick insert-lambda}]
   [:> ui/button
    {:attach "bottom"
     :compact true
     :content "Reportá errores!"
     :color "blue"
     :floated "right"
     :as "a"
     :target "_blank"
     :href "https://todo.sr.ht/~bbuccianti/lambda"
     :style {:position "absolute"
             :bottom "10px"
             :right "50%"
             :transform "translateX(50%)"
             :visibility (if (:report? @state/config) "visible" "hidden")}}]
   [toggle-button "Trace" :trace?]
   [toggle-button "Full" :full?]
   [toggle-button "Índices" :index?]])

