(ns lambda.stringy)

(defn toString [m]
  (cond
    (contains? m :abst)
    (let [abst (:abst m)]
      (str "(λ" (toString (:param abst)) "."
           (toString (:cuerpo abst)) ")"))

    (contains? m :apli)
    (let [apli (:apli m)]
      (str "(" (toString (:opdor apli))
           " " (toString (:opndo apli)) ")"))

    (contains? m :var)
    (:var m)

    :else m))
