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

(defn handle-key-press [e in]
  (let [input (gdom/getElement "input")
        idx (.-selectionStart input)
        left (subs @in 0 idx)
        right (subs @in idx)]
    (when (= "\\" (.-key e))
      (.preventDefault e)
      (reset! in (str left "λ" right))
      (js/setTimeout #(.setSelectionRange input (inc idx) (inc idx)) 25))))

(defn handle-action [in]
  (swap! state/outputs conj (reducir @in))
  (reset! state/index (dec (count @state/outputs))))

(defn wrapped-inc [n]
  (min (inc n) (dec (count @state/outputs))))

(defn wrapped-dec [n]
  (if (< 0 (dec n)) 0 (dec n)))

(defn handle-history-changes [key]
  (case key
    "ArrowUp"   (swap! state/index wrapped-dec)
    "ArrowDown" (swap! state/index wrapped-inc)
    nil))

(defn readline [in]
  [:> ui/container
   {:style {:paddingTop "15px"}}
   [:> ui/input
    {:id "input"
     :placeholder "Insertá una expresión!"
     :fluid true
     :size "huge"
     :value @in
     :onKeyPress #(handle-key-press % in)
     :onKeyUp #(handle-history-changes (.-key %))
     :onChange #(reset! in (.. % -target -value))
     :action {:content "Evaluar" :onClick #(handle-action in)}}]])
