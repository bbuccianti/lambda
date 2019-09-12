(ns lambda.parser)

(defn find-next [t v from]
  (first
   (map first
        (filter #(= t (second %))
                (map-indexed vector (subvec v from))))))

(defn find-correct [v from]
  (first
   (nth
    (reverse
     (filter #(or (= :cierra-p (second %)) (= :abre-p (second %)))
             (map-indexed vector v)))
    from)))

(defn match [item]
  (case (:tipo item)
    :ident
    {:var (:string item)}
    (:tipo item)))

(defn transform [v]
  (cond
    (and (= (first v) :abre-p) (= (second v) :lambda))
    (let [punto (find-next :punto v 0)]
      {:abst
       {:param (nth v (dec punto))
        :cuerpo (transform (subvec v (inc punto)))}})

    (and (= (first v) :abre-p) (= (last v) :cierra-p))
    (if (= 4 (count v))
      {:apli {:opdor (nth v 1)
              :opndo (nth v 2)}}
      {:apli {:opdor (transform (subvec v 1 (find-correct v 1)))
              :opndo (transform (subvec v (find-correct v 1)))}})

    (and (= (first v) :cierra-p) (= (last v) :cierra-p))
    (if (= 3 (count v))
      (second v))

    (= 2 (count v))
    {:apli {:opdor (first v)
            :opndo (second v)}}

    :else v))

(defn parse [lexed]
  (transform (mapv match lexed)))
