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

(defn salida-expr []
  [:div
   [:p (str "expresion: " (:expr @state))]])

(defn entrada-expr []
  [:div
   [:input {:id "input"}]
   [:button
    {:on-click
     #(swap! state assoc :expr (reducir (.-value (gdom/getElement "input"))))}
    "Reducir"]])

(defn app []
  [:div
   [entrada-expr]
   [salida-expr]])

(defn mount-app-element []
  (when-let [el (gdom/getElement "app")]
    (r/render-component [app] el)))

(defn on-js-reload []
  (mount-app-element))
