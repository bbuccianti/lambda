(ns lambda.parser)

(defn find-next [tipo v]
  (map first
       (filter #(= (:tipo (second %)) :cierra-p)
               (map-indexed vector v))))

(defn match [item]
  (case (:tipo item)
    :ident
    {:var (:string item)}
    (:tipo item)))

(defn transform [v]
  (if (and (= (first v) :abre-p)
           (= (last v)  :cierra-p))
    {:apl (vec (rest (butlast v)))}
    v))

(defn parse [lexed]
  (transform (mapv match lexed)))
