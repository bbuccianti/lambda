(ns lambda.reducer
  (:require
   [clojure.walk :refer [postwalk prewalk]]))

(defn- indexate [expr param index]
  (cond
    (contains? expr :apli)
    {:apli
     {:opdor (indexate (get-in expr [:apli :opdor]) param index)
      :opndo (indexate (get-in expr [:apli :opndo]) param index)}}

    (contains? expr :var)
    (if (= (:var expr) (:ident param))
      (assoc expr :index index)
      expr)

    :else expr))

(defn alpha-rule [ids expr]
  (let [{:keys [opdor opndo]} (:apli expr)
        param (get-in opdor [:abst :param])]
    (if (= param ids)
      (-> expr
          (assoc-in [:apli :opdor :abst :param :index] 1)
          (indexate param 1))
      expr)))

(defn- beta-rule [m]
  (let [{:keys [opdor opndo]} (:apli m)
        param (get-in opdor [:abst :param])]
    (if (and (not (nil? param)) (not (nil? opndo)))
      (prewalk (fn [target]
                 (if (and (= (:var target) (:ident param))
                          (= (:index target) (:index param)))
                   opndo
                   target))
               (get-in opdor [:abst :cuerpo]))
      m)))

(defn get-params [m]
  (->> (tree-seq map? vals m)
       (filter map?)
       (keep :param)))

(defn apply-alpha-rule? [m]
  (let [params (get-params m)
        freqs (frequencies params)
        flt (filter (fn [[k v]] (> v 1)) freqs)]
    (when flt (ffirst flt))))

(defn step [m]
  (if-let [ids (apply-alpha-rule? m)]
    (alpha-rule ids m)
    (beta-rule m)))

(defn can-reduce?
  ([m] (can-reduce? [] m))
  ([path m]
   (cond
     (get-in m [:apli :opdor :abst] false) path

     (get-in m [:apli] false)
     (or (can-reduce? (conj path :apli :opdor) (-> m :apli :opdor))
         (can-reduce? (conj path :apli :opndo) (-> m :apli :opndo)))

     (get-in m [:abst] false)
     (can-reduce? (conj path :abst :cuerpo) (get-in m [:abst :cuerpo]))

     :else nil)))

(defn all-reductions [m]
  (let [acc (transient [m])]
    (loop [before m]
      (let [path (can-reduce? before)]
        (if (nil? path)
          (persistent! acc)
          (let [reduction (if (= (count path) 0)
                            (step before)
                            (update-in before path step))]
            (conj! acc reduction)
            (recur reduction)))))))

