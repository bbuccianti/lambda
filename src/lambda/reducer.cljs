(ns lambda.reducer
  (:require
   [clojure.walk :refer [postwalk]]))

(defn- transform [m]
  (let [{:keys [opdor opndo]} (:apli m)]
    (cond
      (contains? opdor :abst)
      (postwalk (fn [target]
                 (if (= target (get-in opdor [:abst :param]))
                   opndo
                   target))
                (get-in opdor [:abst :cuerpo]))

      (contains? opdor :apli)
      {:apli {:opdor (transform opdor)
              :opndo (transform opndo)}}

      :else m)))

(defn- keep-reducing [m]
  (if (contains? m :apli)
    (transform m)
    m))

(defn all-reductions [m]
  ;; TODO: fix 10 for managing infinite recursion
  (distinct (take 10 (iterate keep-reducing m))))
