(ns lambda.utils
  (:require
   #?@(:cljs [[goog.dom :as gdom]])
   [lambda.state :as state]
   [lambda.lexer :refer [lex]]
   [lambda.normalizer :refer [restore]]
   [lambda.parser :refer [parse]]
   [lambda.reducer :refer [all-reductions]]
   [lambda.stringy :refer [toString]]))

(defn get-reductions [input]
  (->> input lex restore parse
       (all-reductions (:trace? @state/config))))

(defn wrapped-dec [n]
  (if (> 0 (dec (int n))) (count @state/outputs) (dec (int n))))

(defn wrapped-inc [n]
  (min (inc (int n)) (count @state/outputs)))

(defn reset-and-restore [el cmd i]
  #?(:cljs (set! (.-value el) cmd))
  (reset! state/command cmd)
  #?(:cljs
     (js/setTimeout #(.setSelectionRange el i i) 5)))

(defn swap-history-and-input [f]
  (swap! state/index f)
  (let [old (get @state/outputs @state/index)]
    #?(:cljs
       (reset-and-restore (gdom/getElement "input")
                          (if old (:command old) "")
                          (if old (count (:command old)) 0)))))

(defn handle-history-changes [key]
  (case key
    "ArrowUp"   (swap-history-and-input wrapped-dec)
    "ArrowDown" (swap-history-and-input wrapped-inc)
    nil))
