(ns lambda.core
  (:require
   [goog.dom :as gdom]
   [reagent.core :as r]
   [lambda.semantic :as ui]
   [lambda.components.input :refer [readline]]
   [lambda.components.output :refer [results]]))

(defn app []
  (let [in (r/atom "")]
    (fn []
      [:> ui/container
       [readline in]
       [results]])))

(defn mount-app []
  (when-let [el (gdom/getElement "app")]
    (r/render-component [app] el)))

(defn on-js-reload []
  (mount-app))

(defn ^:export main []
  (on-js-reload))
