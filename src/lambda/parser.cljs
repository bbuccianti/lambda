(ns lambda.parser)

(defn find-next [t v from]
  (first
   (map first
        (filter #(= t (second %))
                (map-indexed vector (subvec v from))))))

(defn match [item]
  (case (:tipo item)
    :ident
    {:var (:string item)}
    (:tipo item)))

(defn transform [v]
  (cond
    (and (= (first v) :abre-p)
         (= (second v) :lambda))
    {:abst
     {:param (nth v 2)
      :cuerpo (transform
                (subvec v (inc
                           (find-next :punto v 0))))}}
    (and (= (first v) :abre-p)
         (= (last v)  :cierra-p))
    {:apli {:opdor (nth v 1) :opndo (nth v 2)}}
    :else v))

(defn parse [lexed]
  (transform (mapv match lexed)))
