(ns lambda.normalizer
  (:require
   [lambda.lexer :as l]))

(def abre-p {:tipo :abre-p :string "("})
(def cierra-p {:tipo :cierra-p :string ")"})
(def lambda {:tipo :lambda :string "λ"})
(def punto {:tipo :punto :string "."})

(defn flast [lst]
  (last (first lst)))

(defn next-x [x lexed]
  (->> (map-indexed list lexed) (filter #(= x (second %))) ffirst))

(defn next-close [lexed]
  (reduce (fn [acc item]
            (if (= cierra-p (second item))
              (if (>= 0 (dec acc)) (reduced (first item)) (dec acc))
              (inc acc)))
          0 (->> (map-indexed list lexed)
                 (filter #(#{abre-p cierra-p} (second %))))))

(defn surround
  ([x] (apply surround x))
  ([x1 x2] (list abre-p x1 x2 cierra-p)))

(defn isolate [lexed]
  (cond
    (#{:ident :combi} (:tipo (first lexed)))
    (cons (first lexed) (isolate (rest lexed)))

    (= abre-p (first lexed))
    (let [c (next-close lexed)]
      (cons (take (inc c) lexed)
            (isolate (drop (inc c) lexed))))

    :else lexed))

(defn regroup [lexed]
  (if (= 1 (count lexed))
    lexed
    (reduce (fn [acc nxt] (surround acc nxt))
            (surround (take 2 lexed))
            (drop 2 lexed))))

(defn inners [lst]
  (map (fn [i]
         (cond
           (and (= abre-p (first i)) (= :ident (:tipo (second i))))
           (regroup (-> (butlast (rest i)) isolate inners))

           (and (= abre-p (first i)) (= lambda (second i)))
           (let [p (next-x punto i)
                 left (take (inc p) i)
                 middle (regroup (-> (butlast (drop (inc p) i))
                                     isolate inners))]
             (list
              (if (> (count left) 4)
                (map #(list abre-p lambda % punto)
                     (butlast (drop 2 left)))
                left)
              middle
              (if (> (count left) 4)
                (repeat (- (count left) 3) (last i))
                (last i))))

           (= [abre-p abre-p] (take 2 i))
           (surround (-> (butlast (rest i)) isolate inners))

           :else i))
       lst))

(defn restore [lexed]
  (-> lexed isolate inners regroup flatten))
