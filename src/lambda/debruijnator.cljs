(ns lambda.debruijnator
   (:require
   [clojure.walk :refer [prewalk postwalk]]))

(defn- indexate [{:keys [param cuerpo]} index]
  {:abst
   {:param (assoc param :index index)
    :cuerpo
    (prewalk (fn [target]
               (if (and (contains? target :var)
                        (not (contains? target :index))
                        (= (:var target) (:ident param)))
                 (assoc target :index index)
                 target))
             cuerpo)}})

(defn debruijn [expression]
  (let [index (atom 0)]
    (postwalk (fn [target]
                (if (contains? target :abst)
                  (indexate (:abst target) (swap! index inc))
                  target))
              expression)))
