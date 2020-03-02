(ns lambda.parser
  (:require
   [lambda.lexer :refer [lex]]))

(def combinadores {"I" (lex "(λx.x)")
                   "K" (lex "(λx.(λy.x))")
                   "O" (lex "(λx.(λy.y))")
                   "B" (lex "(λx.(λy.(λz.(x (y z)))))")
                   "C" (lex "(λx.(λy.(λz.((x z) y))))")
                   "T" (lex "(λx.(λy.(y x)))")
                   "S" (lex "(λx.(λy.(λz.((x z) (y z)))))")
                   "W" (lex "(λx.(λy.((x y) y)))")})

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
      (if (or (= 0 count) (= 0 (dec count)))
        (first (first parens))
        (recur (rest parens) (dec count))))))

(defn- match [item]
  (case (:tipo item)
    :ident {:var (:string item)}
    :combi (mapv match (get combinadores (:string item)))
    (:tipo item)))

(defn- transform [v]
  (cond
    (and (= (first v) :abre-p) (= (second v) :lambda))
    (let [punto (find-next :punto v)]
      {:abst
       {:param (nth v (dec punto))
        :cuerpo (transform (subvec v (inc punto)))}})

    (= (first v) :abre-p)
    (if (and (>= 3 (count v)) (<= (count v) 5))
      {:apli {:opdor (nth v 1) :opndo (nth v 2)}}

      (let [c (find-matching-close v)]
        (if (map? (second v))
          {:apli {:opdor (nth v 1)
                  :opndo (transform (subvec v 2 c))}}

          {:apli {:opdor (transform (subvec v 1 c))
                  :opndo (transform (subvec v (inc c)))}})))

     :else (first v)))

(defn parse [lexed]
  (-> (mapv match lexed)
      flatten
      vec
      transform))
