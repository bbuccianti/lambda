(ns lambda.debruijnator
   (:require
   [clojure.walk :refer [prewalk postwalk prewalk-demo]]))

(def indexed (atom {}))
(def index (atom 0))

(defn- add-indexed[var]
     (swap! indexed assoc (keyword var) @index))

(defn- inc-indexed[]
  (reset! indexed (apply merge 
                   (map (fn [[k v]] {k (inc v) }) @indexed)))
  )

(defn- check-index[var]
  ((keyword var) @indexed))

(defn- indexate[exp]
  (let [param (get-in exp [:param])
        cuerpo (get-in exp  [:cuerpo])
        var (get-in exp  [:param :var])]
    (do
      (swap! index inc)
      (add-indexed var)
        {:abst
         {:param
          (assoc param :index (check-index var))
          :cuerpo
          (prewalk (fn [target]
                     (if  (and
                           (contains? target :var)
                           (nil? (target :index))
                           (= (target :var) var))
                       (assoc target :index (check-index var))
                       target))
                   cuerpo)}})))

(defn- keep-indexing[exp index]
  (let [indexer (fn [target]
                  (if (contains? target :abst)
                        (indexate (target :abst))
                        target))]
                  ;; (if (some? (get-in target [:opdor]))
                  ;;   ;(do
                  ;;    ; (save-level)
                  ;;    ; (indexate (get-in target [:apli :opdor :abst])))
                  ;;   (if (some? (get-in target [:opndo]))
                  ;;    ; (do
                  ;;       ;(load-level)
                  ;;     ;  (indexate (get-in target [:apli :opdor :abst])))
                  ;;     (if (contains? target :abst)
                  ;;       (indexate (target :abst))
                  ;;       target))))]
    (postwalk (fn [target]
                (indexer target))
              exp)))

(defn debruijn[exp]
  (do
    (swap! indexed {})
    (reset! index 0)
   (keep-indexing exp 0)))
