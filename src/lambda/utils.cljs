(ns lambda.utils
  (:require
   [goog.dom :as gdom]
   [lambda.state :as state]
   [lambda.lexer :refer [lex]]
   [lambda.normalize :refer [restore]]
   [lambda.parser :refer [parse]]
   [lambda.debruijnator :refer [debruijn]]
   [lambda.reducer :refer [all-reductions]]
   [lambda.stringy :refer [toString]]))

(defn get-reductions [input]
  (-> input lex restore parse debruijn all-reductions))

(defn wrapped-dec [n]
  (if (> 0 (dec n)) (count @state/outputs) (dec n)))

(defn wrapped-inc [n]
  (min (inc n) (count @state/outputs)))

(defn reset-and-restore [el cmd i]
  (set! (.-value el) cmd)
  (reset! state/command cmd)
  (js/setTimeout #(.setSelectionRange el i i) 5))

(defn swap-history-and-input [f]
  (swap! state/index f)
  (let [old (get @state/outputs @state/index)]
    (reset-and-restore (gdom/getElement "input")
                       (if old (:command old) "")
                       (if old (count (:command old)) 0))))

(defn handle-history-changes [key]
  (case key
    "ArrowUp"   (swap-history-and-input wrapped-dec)
    "ArrowDown" (swap-history-and-input wrapped-inc)
    nil))
