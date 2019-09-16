(ns lambda.parser)

(defn- find-next [t v]
  (->> (map-indexed vector v)
       (filter #(= t (second %)))
       (map first)
       first))

(defn- find-matching-close [v]
  (loop [parens (->> (map-indexed vector v)
                     (filter #(contains? #{:cierra-p :abre-p} (second %)))
                     rest)
         count 0]
    (case (second (first parens))
      :abre-p
      (recur (rest parens) (inc count))

      :cierra-p
      (if (= 0 (dec count))
        (first (first parens))
        (recur (rest parens) (dec count))))))

(defn- match [item]
  (case (:tipo item)
    :ident {:var (:string item)}
    (:tipo item)))

(defn- transform [v]
  (cond
    (and (= (first v) :abre-p) (= (second v) :lambda))
    (let [punto (find-next :punto v)]
      {:abst
       {:param (nth v (dec punto))
        :cuerpo (transform (subvec v (inc punto)))}})

    (and (= (first v) :abre-p) (= (last v) :cierra-p))
    (if (or (= 5 (count v)) (= 4 (count v)))
      {:apli {:opdor (nth v 1)
              :opndo (nth v 2)}}
      (let [c (find-matching-close v)]
        {:apli {:opdor (transform (subvec v 1 c))
                :opndo (transform (subvec v (inc c)))}}))

    :else (first v)))

(defn parse [lexed]
  (->> (mapv match lexed)
       transform))
