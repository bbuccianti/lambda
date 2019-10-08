(ns lambda.core
  (:require
   [goog.dom :as gdom]
   [clojure.string :as string]
   [reagent.core :as r]
   [lambda.lexer :as l]
   [lambda.parser :as p]
   [lambda.reducer :as red]))

(defonce state (r/atom {:expr ""}))

(defn reducir [input]
  (->> input
       l/lex
       p/parse
       red/reduce))

(defn app []
  (let [state (r/atom {:input ""})]
    (fn []
      [:div
       [:dvi
        [:input
         {:id "input"
          :value (:input @state)
          :on-key-press
          (fn [e]
            (when (= "\\" (.-key e))
              (.preventDefault e)
              (swap! state assoc :input (str (:input @state) "λ"))))
          :on-change
          #(swap! state assoc :input (.. % -target -value))}]]
       [:div
        [:p
         (str "expresion: "
              (try
                (reducir (:input @state))
                (catch :default e
                  e)))]]])))

(defn mount-app-element []
  (when-let [el (gdom/getElement "app")]
    (r/render-component [app] el)))

(defn on-js-reload []
  (mount-app-element))

