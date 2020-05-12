(ns lambda.normalizer)

(def abre-p {:tipo :abre-p :string "("})
(def cierra-p {:tipo :cierra-p :string ")"})
(def lambda {:tipo :lambda :string "λ"})
(def punto {:tipo :punto :string "."})

(defn next-x [x lexed]
  (->> (map-indexed list lexed) (filter #(= x (second %))) ffirst))

(defn next-close [lexed t-set t-close]
  (reduce (fn [acc [i item]]
            (if (= t-close item)
              (if (>= 0 (dec acc)) (reduced i) (dec acc))
              (inc acc)))
          0 (->> (map-indexed list lexed)
                 (filter #(t-set (second %))))))

(defn surround [x1 x2]
  (list abre-p x1 x2 cierra-p))

(defn isolate [lexed]
  (cond
    (#{:ident :combi} (:tipo (first lexed)))
    (cons (first lexed) (isolate (rest lexed)))

    (= lambda (first lexed))
    (list lexed)

    (= abre-p (first lexed))
    (let [c (next-close lexed #{abre-p cierra-p} cierra-p)]
      (cons (take (inc c) lexed)
            (isolate (drop (inc c) lexed))))

    :else lexed))

(defn regroup [lexed]
  (if (>= 1 (count lexed))
    lexed
    (reduce (fn [acc nxt] (surround acc nxt))
            (apply surround (take 2 lexed))
            (drop 2 lexed))))

(defn inners [lst]
  (map (fn [i]
         (cond
           (or (= [:abre-p :ident] (map :tipo (take 2 i)))
               (= [abre-p abre-p] (take 2 i)))
           (-> (butlast (rest i)) isolate inners regroup)

           (= lambda (first i))
           (-> (flatten (list abre-p i cierra-p)) isolate inners)

           (= [:abre-p :lambda] (map :tipo (take 2 i)))
           (let [p (next-x punto i)
                 left (take (inc p) i)
                 middle (-> (butlast (drop (inc p) i))
                            isolate inners regroup)]
             (list
              (if (> (count left) 4)
                (map #(list abre-p lambda % punto)
                     (butlast (drop 2 left)))
                left)
              middle
              (if (> (count left) 4)
                (repeat (- (count left) 3) (last i))
                (last i))))

           :else i))
       lst))

(defn restore [lexed]
  (-> lexed isolate inners regroup flatten))
