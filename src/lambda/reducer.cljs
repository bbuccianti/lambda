(ns lambda.reducer
  (:require
   [clojure.walk :refer [prewalk-replace prewalk]]))

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
  (cond
    (contains? m :apli)
    (transform (:apli m))

    (contains? m :opdor)
    (transform m)

    :else m))
