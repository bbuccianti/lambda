(ns lambda.parser
  (:require
   [lambda.lexer :refer [lex]]))

(def combis {"I" (lex "(λx.x)")
             "K" (lex "(λx.(λy.x))")
             "O" (lex "(λx.(λy.y))")
             "B" (lex "(λx.(λy.(λz.(x (y z)))))")
             "C" (lex "(λx.(λy.(λz.((x z) y))))")
             "T" (lex "(λx.(λy.(y x)))")
             "S" (lex "(λx.(λy.(λz.((x z) (y z)))))")
             "W" (lex "(λx.(λy.((x y) y)))")
             "Y" (lex "(λf.(λx.f (x x)) (λx. f (x x)))")})

(defn- find-next [t v]
  (->> (map-indexed vector v) (filter #(= t (second %))) (map first) first))

(defn- find-matching-close [v]
  (reduce (fn [i item]
            (case (second item)
              :abre-p (inc i)
              :cierra-p (if (>= 0 (dec i)) (reduced (first item)) (dec i))))
          0 (->> (map-indexed vector v)
                 (filter #(contains? #{:cierra-p :abre-p} (second %)))
                 rest)))

(defn- match [item]
  (case (:tipo item)
    :ident {:var (:string item)}
    :combi (mapv match (get combis (:string item)))
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
