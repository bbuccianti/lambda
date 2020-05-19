(ns lambda.state
  (:require
   #?@(:clj [[clojure.string :as string]]
       :cljs [[reagent.core :as r]])))

(def outputs (#?(:clj atom :cljs r/atom) []))
(def history (#?(:clj atom :cljs r/atom) []))
(def command (#?(:clj atom :cljs r/atom) ""))
(def index (#?(:clj atom :cljs r/atom) 0))
(def config (#?(:clj atom :cljs r/atom)
             {:trace? false
              :full? false
              :index? false
              :report? true}))
