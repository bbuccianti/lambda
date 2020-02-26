(ns lambda.debruijnator
   (:require
   [clojure.walk :refer [prewalk postwalk prewalk-demo]]))

(def indexed (atom {}))

(defn- add-indexed[var value]
   (if (nil? ((keyword var) @indexed))
    (swap! indexed assoc (keyword var) value)))

(defn- check-indexed[var]
  ((keyword var) @indexed))

(defn- indexate[exp index]
  (let [param (get-in exp [:param])
        cuerpo (get-in exp  [:cuerpo])
        var (get-in exp  [:param :var])]
    (do
      (add-indexed var index)
      {:abst
       {:param
        (assoc param :index index)
        :cuerpo
        (prewalk (fn [target]
                   (if (and (contains? target :var)
                            (= (target :var) var))
                     (assoc target :index index)
                     target))
                 cuerpo)}})))

(defn- keep-indexing[exp index]
  (let [index  (atom 0)
        indexer (fn [target]
                  (if (contains? target :abst)
                    (let [used (check-indexed
                                (get-in target [:abst :param :var]))]
                      (if (some? used)
                        (indexate (target :abst) used)
                        (indexate (target :abst)
                                  (swap! index inc))))
                    target))]
    (postwalk (fn [target]
                ;; (if (contains? target :apli)
                ;;   {:apli
                ;;    {:opdor (indexer (get-in target [:apli :opdor]))
                ;;     :opndo (indexer (get-in target [:apli :opndo]))}})
              (indexer target))
            exp)))

(defn debruijn[exp]
  (do (swap! indexed {})
   (keep-indexing exp 0)))
