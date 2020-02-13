(ns lambda.reducer
  (:require
   [clojure.walk :refer [prewalk postwalk]]))

(defn- transform [m]
  (let [{:keys [opdor opndo]} m]
    (if (contains? opdor :abst)
      (prewalk (fn [target]
                 (if (= target (get-in opdor [:abst :param]))
                   opndo
                   target))
               (get-in opdor [:abst :cuerpo]))
      {:apli m})))

(defn reduct [m]
  (postwalk (fn [target]
              (if (contains? target :apli)
                (transform (:apli target))
                target))
            m))
