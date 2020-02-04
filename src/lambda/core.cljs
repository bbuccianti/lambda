(ns lambda.core
  (:require
   [goog.dom :as gdom]
   [reagent.core :as r]
   [lambda.lexer :as l]
   [lambda.parser :as p]
   [lambda.reducer :as red]
   [lambda.normalize :as n]
   [lambda.stringy :as s]))

(defn reducir [input]
  (->> input
       l/lex
       n/restore
       p/parse
       red/reduct
       s/toString))

(defn handle-key-press [e in]
  (let [input (gdom/getElement "input")
        idx (.-selectionStart input)
        left (subs @in 0 idx)
        right (subs @in idx)]
    (when (= "\\" (.-key e))
      (.preventDefault e)
      (reset! in (str left "λ" right))
      (js/setTimeout #(.setSelectionRange input (inc idx) (inc idx)) 25))))

(defn app []
  (let [in (r/atom "")]
    (fn []
      [:div
       [:div.container.mx-auto.py-3
        {:style {:font-size "48px"
                 :border-bottom "solid 3px #000"}}
        [:input.w-100.text-center.border-0
         {:id "input"
          :value @in
          :onKeyPress #(handle-key-press % in)
          :onChange #(reset! in (.. % -target -value))}]]
       [:div.container.mx-auto.py-3
        [:p
         {:style {:font-size "80px"}}
         (str (try
                (reducir @in)
                (catch :default e e)))]]])))

(defn mount-app []
  (when-let [el (gdom/getElement "app")]
    (r/render-component [app] el)))

(defn on-js-reload []
  (mount-app))

(defn ^:export main []
  (on-js-reload))


