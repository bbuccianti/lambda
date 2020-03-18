(ns lambda.reducer
  (:require
   [clojure.walk :refer [postwalk]]))

(defn- transform [m]
  (let [{:keys [opdor opndo]} (:apli m)]
    (if (and (contains? opdor :abst) (not (nil? opndo)))
      (postwalk (fn [target]
                  (if (= target (get-in opdor [:abst :param]))
                    opndo
                    target))
                (get-in opdor [:abst :cuerpo]))
      m)))

(defn- keep-reducing [m]
  (let [flag (atom true)]
    (postwalk (fn [target]
                (if (and @flag (get-in target [:apli :opdor :abst] false))
                  (do (reset! flag false) (transform target))
                  target))
              m)))

(defn all-reductions [m]
  ;; TODO: fix 20 for managing infinite recursion
  (distinct (take 20 (iterate keep-reducing m))))
