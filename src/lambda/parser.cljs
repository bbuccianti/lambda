(ns lambda.parser)

(defn find-next [t v]
  (first
   (map first
        (filter #(= t (second %))
                (map-indexed vector v)))))

(defn find-matching-close [parens count]
  (let [tipo (second (first parens))]
    (cond
      (and (= :cierra-p tipo) (= 0 count))
      (first (first parens))

      (= :abre-p tipo)
      (find-matching-close (rest parens) (inc count))

      (= :cierra-p tipo)
      (find-matching-close (rest parens) (dec count)))))

(defn find-correct [v]
  (find-matching-close
   (rest (filter #(or (= :cierra-p (second %)) (= :abre-p (second %)))
                 (map-indexed vector v)))
   0))

(defn match [item]
  (case (:tipo item)
    :ident
    {:var (:string item)}
    (:tipo item)))

(defn transform [v]
  (cond
    (and (= (first v) :abre-p) (= (second v) :lambda))
    (let [punto (find-next :punto v)]
      {:abst
       {:param (nth v (dec punto))
        :cuerpo (transform (subvec v (inc punto)))}})

    (and (= (first v) :abre-p) (= (last v) :cierra-p))
    (let [c (find-correct (rest v))]
      (if (or (= 5 (count v)) (= 4 (count v)))
       {:apli {:opdor (nth v 1)
               :opndo (nth v 2)}}
       {:apli {:opdor (transform (subvec v 1 (inc c)))
               :opndo (transform (subvec v (+ c 2)))}}))

    (and (= (first v) :cierra-p) (= (last v) :cierra-p))
    (if (= 3 (count v))
      (second v))

    (= 2 (count v))
    (if (= :cierra-p (last v))
      (first v)
      {:apli {:opdor (first v)
              :opndo (second v)}})

    :else v))

(defn parse [lexed]
  (transform (mapv match lexed)))
