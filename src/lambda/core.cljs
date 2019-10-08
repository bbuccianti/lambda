(ns lambda.core
  (:require
   [goog.dom :as gdom]
   [clojure.string :as string]
   [reagent.core :as r :refer [atom]]
   [lambda.lexer :as l]))

(enable-console-print!)

(defonce estado (atom {:expresion ""}))

(defn reducir [s]
  (l/lex s))

(defn salida-expr []
  [:div
   (:expresion @estado)])

(defn entrada-expr []
  [:div
   [:input {:id "expresion"}]
   [:button
    {:on-click
     #(swap! estado assoc
             :expresion (reducir (.-value (gdom/getElement "expresion"))))}
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

