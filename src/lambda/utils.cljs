(ns lambda.utils
  (:require
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
