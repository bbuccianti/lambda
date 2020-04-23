(ns lambda.reducer
  (:require
   [clojure.walk :refer [postwalk prewalk]]
   [lambda.debruijnator :refer [debruijn cleaner]]))

(defn- transform [m]
  (let [{:keys [opdor opndo]} (:apli m)]
    (if (and (contains? opdor :abst) (not (nil? opndo)))
      (postwalk (fn [target]
                  (if (and (= (:var target)
                              (get-in opdor [:abst :param :ident]))
                           (= (:index target)
                              (get-in opdor [:abst :param :index])))
                    opndo
                    target))
                (get-in opdor [:abst :cuerpo]))
      m)))

(defn- keep-reducing [m]
  (let [flag (atom true)]
    (prewalk (fn [target]
               (if (and @flag (get-in target [:apli :opdor :abst] false))
                 (do (reset! flag false) (transform target))
                 target))
             m)))

(defn all-reductions [m]
  ;; TODO: fix 20 for managing infinite recursion
  (distinct (take 20 (iterate (comp keep-reducing debruijn cleaner) m))))
