(ns lambda.components.input
  (:require
   [goog.dom :as gdom]
   [reagent.core :as r]
   [lambda.semantic :as ui]
   [lambda.state :as state]
   [lambda.instaparser :as instaparser]
   [lambda.utils :refer [get-reductions
                         handle-history-changes
                         reset-and-restore]]))

(defn handle-action []
  (if (not (empty? @state/command))
    (let [cmd @state/command
          new-input {:command cmd}
          merged (merge new-input
                        (if (instaparser/failure? cmd)
                          {:error (instaparser/get-error cmd)}
                          {:reductions (get-reductions cmd)}))
          old-index (count @state/outputs)]
      (swap! state/outputs conj merged)
      (reset! state/index old-index)
      (reset-and-restore (gdom/getElement "input") "" 0))))

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

(defn toggle-button [name k message]
  [:> ui/popup
   {:on "hover"
    :position "bottom center"
    :size "small"
    :trigger
    (r/as-component
     [:> ui/button
      {:attach "bottom"
       :content name
       :compact true
       :floated "right"
       :color (if (k @state/config) "green" "red")
       :onClick #(swap! state/config update k not)
       :style {:z-index "90"}}])}
   message])

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
      :size "huge"
      :default-value @state/command
      :onKeyPress #(handle-key-press %)
      :onKeyUp #(handle-history-changes (.-key %))
      :onChange #(reset! state/command (.. % -target -value))
      :action {:content "Evaluar"
               :onClick handle-action}
      :style {:margin-bottom "5px"}}]
    [make-arrow "left"]
    [make-arrow "right"]
    [:> ui/button
     {:attach "bottom"
      :content "λ"
      :compact true
      :basic true
      :onClick insert-lambda}]
    [toggle-button "Trace" :trace? "Muestra todas las reglas aplicadas"]
    [toggle-button "Full" :full? "Muestra todos los paréntesis"]]])

