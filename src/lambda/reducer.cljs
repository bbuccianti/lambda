(ns lambda.reducer
  (:require
   [clojure.walk :refer [postwalk]]))

(defn- transform [m]
  (let [{:keys [opdor opndo]} (:apli m)]
    (if (contains? opdor :abst)
      (postwalk (fn [target]
                 (if (= target (get-in opdor [:abst :param]))
                   opndo
                   target))
                (get-in opdor [:abst :cuerpo]))
      m)))

(defn- keep-reducing [m]
  (postwalk (fn [target]
              (if (contains? target :apli)
                (transform target)
                target))
            m))

(defn all-reductions [m]
  ;; TODO: fix 20 for managing infinite recursion
  (distinct (take 20 (iterate keep-reducing m))))
