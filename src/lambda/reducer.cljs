(ns lambda.reducer
  (:require
   [clojure.walk :refer [postwalk prewalk]]
   [lambda.debruijnator :refer [debruijn cleaner]]))

(def clean? (atom false))

(defn- count-replacements [m]
  (let [{:keys [opdor opndo]} (:apli m)
        param (get-in opdor [:abst :param])
        counter (atom 0)]
    (prewalk
     (fn [target]
       (if (and (= (:var target) (:ident param))
                (= (:index target) (:index param)))
         (do (swap! counter inc) target)
         target))
     (get-in opdor [:abst :cuerpo]))
    @counter))

(defn- transform [m]
  (let [{:keys [opdor opndo]} (:apli m)]
    (if (and (contains? opdor :abst) (not (nil? opndo)))
      (do
        (when (> (count-replacements m) 1)
          (reset! clean? true))
        (postwalk (fn [target]
                    (if (and (= (:var target)
                                (get-in opdor [:abst :param :ident]))
                             (= (:index target)
                                (get-in opdor [:abst :param :index])))
                      opndo
                      target))
                  (get-in opdor [:abst :cuerpo])))
      m)))

(defn- step [m]
  (let [flag (atom true)]
    (prewalk (fn [target]
               (if (and @flag (get-in target [:apli :opdor :abst] false))
                 (do (reset! flag false) (transform target))
                 target))
             (if @clean?
               (do (reset! clean? false)
                   (-> m cleaner debruijn))
               m))))

(defn can-reduce? [m]
  (cond
    (get-in m [:apli :opdor :abst] false) true

    (get-in m [:apli] false)
    (or (can-reduce? (:opdor (:apli m))) (can-reduce? (:opndo (:apli m))))

    (get-in m [:abst] false)
    (can-reduce? (get-in m [:abst :cuerpo]))

    :else false))

(defn all-reductions [m]
  (loop [new-expr (debruijn m)
         acc (transient [m])]
    (if (can-reduce? new-expr)
      (let [reduction (step new-expr)]
        (recur reduction (conj! acc reduction)))
      (persistent! acc))))

