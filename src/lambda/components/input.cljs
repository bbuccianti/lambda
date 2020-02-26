(ns lambda.components.input
  (:require
   [goog.dom :as gdom]
   [lambda.semantic :as ui]
   [lambda.state :as state]
   [lambda.lexer :refer [lex]]
   [lambda.normalize :refer [restore]]
   [lambda.parser :refer [parse]]
   [lambda.reducer :refer [reduct]]
   [lambda.stringy :refer [toString]]))

(defn reducir [input]
  (->> input lex restore parse reduct toString))

(defn handle-key-press [e]
  (let [input (gdom/getElement "input")
        idx (.-selectionStart input)
        left (subs @state/command 0 idx)
        right (subs @state/command idx)]
    (when (= "\\" (.-key e))
      (.preventDefault e)
      (reset! state/command (str left "λ" right))
      (js/setTimeout #(.setSelectionRange input (inc idx) (inc idx)) 25))))

(defn handle-action []
  (let [new-input {:command @state/command
                   :reduced (reducir @state/command)}]
    (swap! state/outputs conj new-input)
    (reset! state/index (dec (count @state/outputs)))))

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
     :onChange #(reset! state/command (.. % -target -value))
     :action {:content "Evaluar" :onClick handle-action}}]])
