(ns lambda.parser)

(defn encontrar [v]
  )

(defn parse [lexed]
  (if (= :abre-p (:tipo (first lexed)))
    (encontrar (subvec (vec lexed) 1))))
