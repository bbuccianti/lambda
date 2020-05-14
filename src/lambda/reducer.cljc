(ns lambda.reducer
  (:require
   [clojure.walk :refer [postwalk prewalk]]))

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
         (= (:var expr) (:ident param)))
    opndo

    :else expr))

(defn- beta-rule [m]
  (let [{:keys [opdor opndo]} (:apli m)
        param (get-in opdor [:abst :param])]
    (replace-in (get-in opdor [:abst :cuerpo])
                param
                opndo)))

(defn step [m]
  (beta-rule m))

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

