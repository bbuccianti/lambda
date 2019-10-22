(ns lambda.core
  (:require
   [goog.dom :as gdom]
   [clojure.string :as string]
   [reagent.core :as r]
   [lambda.lexer :as l]
   [lambda.parser :as p]
   [lambda.reducer :as red]
   [lambda.normalize :as n]))

(defn reducir [input]
  (->> input
       l/lex
       n/restore
       p/parse
       red/reduce))

(defn app []
  (let [state (r/atom {:input ""})]
    (fn []
      [:div
       [:div.container.mx-auto.py-3
        {:style {:font-size "48px"
                 :border-bottom "solid 3px #000"}}
        [:input.w-100.text-center.border-0
         {:id "input"
          :value (:input @state)
          :on-key-press
          (fn [e]
            (when (= "\\" (.-key e))
              (.preventDefault e)
              (swap! state assoc :input (str (:input @state) "λ"))))
          :on-change
          #(swap! state assoc :input (.. % -target -value))}]]
       [:div.container.mx-auto.py-3
        [:p
         (str (try
                (reducir (:input @state))
                (catch :default e e)))]]])))

(defn mount-app-element [] 
  (when-let [el (gdom/getElement "app")]
    (r/render-component [app] el)
    (.. (gdom/getElement "input") focus)))

(defn on-js-reload [] 
  (mount-app-element))
