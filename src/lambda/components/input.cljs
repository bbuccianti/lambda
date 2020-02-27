(ns lambda.components.input
  (:require
   [goog.dom :as gdom]
   [lambda.semantic :as ui]
   [lambda.state :as state]
   [lambda.lexer :refer [lex]]
   [lambda.normalize :refer [restore]]
   [lambda.parser :refer [parse]]
   [lambda.reducer :refer [all-reductions]]
   [lambda.stringy :refer [toString]]))

(defn normalizar [input]
  (-> input lex restore parse (toString :full)))

(defn get-reductions [input]
  (-> input lex restore parse all-reductions))

(defn reset-and-restore [el s i]
  (reset! state/command s)
  (js/setTimeout #(.setSelectionRange el i i) 3))

(defn handle-action []
  (let [new-input {:command @state/command
                   :reductions (get-reductions @state/command)}]
    (swap! state/outputs conj new-input)
    (reset! state/index (dec (count @state/outputs)))))

(defn handle-key-press [e]
  (case (.-key e)
    "\\" (let [input (gdom/getElement "input")
               idx (.-selectionStart input)
               left (subs @state/command 0 idx)
               right (subs @state/command idx)]
           (.preventDefault e)
           (reset-and-restore input (str left "λ" right) (inc idx)))

    "Enter" (handle-action)

    nil))

(defn wrapped-inc [n]
  (min (inc n) (count @state/outputs)))

(defn wrapped-dec [n]
  (if (> 0 (dec n)) 0 (dec n)))

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

(defn readline []
  [:> ui/container
   {:style {:paddingTop "15px"}}
   [:> ui/input
    {:id "input"
     :placeholder "Insertá una expresión!"
     :fluid true
     :size "huge"
     :value @state/command
     :onKeyPress #(handle-key-press %)
     :onKeyUp #(handle-history-changes (.-key %))
     :onChange handle-on-change
     :action {:content "Evaluar" :onClick handle-action}
     :style {:margin-bottom "5px"}}]
   [:> ui/button
    {:attach "bottom"
     :content "Trace"
     :compact true
     :color (if (:trace? @state/config) "green" "red")
     :onClick #(swap! state/config update :trace? not)}]
   [:> ui/button
    {:attach "bottom"
     :compact true
     :content "Reportá el error!"
     :color "blue"
     :floated "right"
     :as "a"
     :target "_blank"
     :href "https://todo.sr.ht/~bbuccianti/lambda"}]])
