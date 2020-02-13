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

(defn- keep-reducing [m]
  (postwalk (fn [target]
              (if (contains? target :apli)
                (transform (:apli target))
                target))
            m))

(defn reduct [m]
  ;; TODO: fix 10 for managing infinite recursion
  (last (take 10 (iterate keep-reducing m))))
