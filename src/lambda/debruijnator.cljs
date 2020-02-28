(ns lambda.debruijnator
   (:require
   [clojure.walk :refer [prewalk postwalk]]))

(def indexed (atom {}))
(def index (atom 0))
(def state (atom 0))

(defn- load-state[]
  (let [value @state]
    (reset! index value)))

(defn- save-state[]
  (let [value @index]
    (reset!  state value)))

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
  (let [
        indexer (fn [target]
                  (do
                    (if (contains? target :opdor)
                      (save-state)
                      )
                    (if (vector? target)
                      (if (= (first target) :opndo)
                        (load-state))                      
                      )
                   (if (contains? target :abst)
                        (indexate (target :abst))
                        target)))]
    (postwalk (fn [target]
                (indexer target))
              exp)))

(defn debruijn[exp]
  (do
    (swap! indexed {})
    (reset! index 0)
    (reset! state 0)
   (keep-indexing exp 0)))
