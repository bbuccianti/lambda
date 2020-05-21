(ns lambda.reducer
  (:require
   [clojure.pprint :refer [pprint]]
   [clojure.walk :refer [postwalk prewalk]]
   [lambda.stringy :refer [toString]]))

(defn get-params [m]
  (->> (tree-seq map? vals m)
       (filter map?)
       (keep :param)))

(defn indexate [expr target index]
  (cond
    (get-in expr [:apli] false)
    (-> expr
        (update-in [:apli :opdor] indexate target index)
        (update-in [:apli :opndo] indexate target index))

    (and (get-in expr [:abst] false)
         (= (get-in expr [:abst :param :ident]) (:var target))
         (= (get-in expr [:abst :param :index]) (:index target)))
    (-> expr
        (assoc-in [:abst :param :index] (inc index))
        (update-in [:abst :cuerpo] indexate target (inc index)))

    (= expr target)
    (assoc-in expr [:index] index)

    (get-in expr [:abst] false)
    (update-in expr [:abst :cuerpo] indexate target index)

    :else expr))

(defn replace-in [expr param opndo]
  (cond
    (get-in expr [:apli] false)
    (-> expr
        (update-in [:apli :opdor] replace-in param opndo)
        (update-in [:apli :opndo] replace-in param opndo))

    (and (get-in expr [:abst] false)
         (not= (get-in expr [:abst :param]) param))
    (-> expr
        (update-in [:abst :cuerpo] replace-in param opndo))

    (and (contains? expr :var)
         (= (:var expr) (:ident param))
         (= (:index expr) (:index param)))
    opndo

    :else expr))

(defn- alpha-rule [m]
  (let [{:keys [opdor opndo]} (:apli m)
        index (if (:index opndo) (:index opndo) 0)]
    {:apli
     {:opdor (indexate opdor opndo index)
      :opndo opndo}}))

(defn- beta-rule [m]
  (let [{:keys [opdor opndo]} (:apli m)
        param (get-in opdor [:abst :param])]
    (replace-in (get-in opdor [:abst :cuerpo])
                param
                opndo)))

(defn- eta-rule [m]
  (get-in m [:abst :cuerpo :apli :opdor]))

(defn can-reduce?
  ([m] (can-reduce? [] m))
  ([path m]
   (cond
     (get-in m [:apli :opdor :abst] false) path

     (get-in m [:apli] false)
     (or (can-reduce? (conj path :apli :opdor) (-> m :apli :opdor))
         (can-reduce? (conj path :apli :opndo) (-> m :apli :opndo)))

     (and (empty? path)
          (= (get-in m [:abst :param :ident] "ident")
             (get-in m [:abst :cuerpo :apli :opndo :var] "var")))
     (conj path :eta)

     (get-in m [:abst] false)
     (can-reduce? (conj path :abst :cuerpo) (get-in m [:abst :cuerpo]))

     :else nil)))

(defn step [path before]
  (let [m (get-in before path)
        params (get-params m)
        opndo (get-in m [:apli :opndo])
        flt (filter
             #(and (= (:var opndo) (:ident %))
                   (= (:index opndo) (:index %)))
             params)]
    (if (empty? flt)
      {:rule "β"  :reduction (update-in before path beta-rule)}
      {:rule "α" :reduction (update-in before path alpha-rule)})))

(defn all-reductions
  ([m]
   (all-reductions false m))
  ([traced? m]
   (let [acc (transient [{:rule nil :reduction (toString m)}])]
     (loop [before m]
       (let [path (can-reduce? before)]
         (if (nil? path)
           (if traced?
             (persistent! acc)
             (toString before))
           (let [result
                 (cond
                   (= [:eta] path)
                   {:rule "η" :reduction (eta-rule before)}

                   (empty? path)
                   {:rule "β" :reduction (beta-rule before)}

                   :else (step path before))]
             (when traced?
               (conj! acc (update-in result [:reduction] toString)))
             (recur (:reduction result)))))))))
