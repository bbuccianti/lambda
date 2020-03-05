(ns lambda.debruijnator
   (:require
   [clojure.walk :refer [prewalk postwalk]]))

(defn- indexate [abstraction index]
  (let [param (:param abstraction)
        cuerpo (:cuerpo abstraction)
        var (:var param)]
    {:abst
     {:param (assoc param :index index)
      :cuerpo
      (prewalk (fn [target]
                 (if (and (contains? target :var) (= (target :var) var))
                   (assoc target :index index)
                   target))
               cuerpo)}}))

(defn debruijn [expression]
  (let [index (atom 0)]
    (postwalk (fn [target]
                (if (contains? target :abst)
                  (indexate (target :abst) (swap! index inc))
                  target))
              expression)))
