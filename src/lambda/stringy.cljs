(ns lambda.stringy)

(defn- transform [m]
  (println m)
  (cond
    (find m :abst)
    (let [abst (:abst m)]
      (str "(λ" (transform (:param abst)) "."
           (transform (:cuerpo abst)) ")"))

    (find m :apli)
    (let [apli (:apli m)]
      (str "("(transform (:opdor apli))
           " " (transform (:opndo apli)) ")"))

    (find m :var)
    (:var m)

    :else m))

(defn toString [m]
  (transform m))
