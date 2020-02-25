(ns lambda.debruijnator
   (:require
   [clojure.walk :refer [prewalk postwalk prewalk-demo]]))

(defn indexate[exp index]
  (let [param (get-in exp [:param])
        cuerpo (get-in exp  [:cuerpo])
        var (get-in exp  [:param :var])]
    {:abst
     {:param
      (assoc param :index index)
     :cuerpo
     (prewalk (fn [target]
                (if (and (contains? target :var)
                         (= (target :var) var))
                  (assoc target :index index)
                  target))
              cuerpo)}}))

(defn keep-indexing[exp]
  (postwalk (fn [target]
              (if (contains? target :abst)
                (indexate (target :abst) 1)
                target))
            exp))

(defn debruijn[exp]
  (keep-indexing exp))
