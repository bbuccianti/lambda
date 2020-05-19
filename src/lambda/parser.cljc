(ns lambda.parser
  (:require
   [lambda.lexer :refer [lex]]
   [lambda.normalizer :refer [next-x next-close]]))

(declare combis)
(def list-of-combinators
  ["I" "(λx.x)"
   "K" "(λx.(λy.x))"
   "S" "(λx.(λy.(λz.((x z) (y z)))))"
   "B" "(λx.(λy.(λz.(x (y z)))))"
   "C" "(λx.(λy.(λz.((x z) y))))"
   "D" "(λx.(λy.(λz.(λv.((x y) (z v)))"
   "J" "(λx.(λy.(λz.(λv.((x y) ((x v) z)))"
   "M" "(λx.(x x))"
   "O" "(λx.(λy.y))"
   "R" "(λx.(λy.(λz.((y z) x))))"
   "Q" "(λx.(λy.(λz.(y (x z)))))"
   "W" "(λx.(λy.((x y) y)))"
   "T" "(λx.(λy.(y x)))"
   "Y" "(λf.((λx.(f (x x))) (λx.(f (x x)))))"])

(defn- match [item]
  (case (:tipo item)
    :ident {:var (:string item)}
    :combi (combis (:string item))
    (:tipo item)))

(def combis
  (reduce (fn [acc [k v]]
            (into acc {k (->> v lex (map match))}))
          {}
          (partition 2 list-of-combinators)))

(defn- transform [lxd]
  (cond
    (= [:abre-p :lambda] (take 2 lxd))
    (let [punto (int (next-x :punto lxd))
          c (next-close lxd #{:abre-p :cierra-p} :cierra-p)]
      {:abst
       {:param {:ident (:var (nth lxd (dec punto)))}
        :cuerpo (transform (->> lxd (take c) (drop (inc punto))))}})

    (= (first lxd) :abre-p)
    (let [inner (rest (butlast lxd))
          c (int (next-close inner #{:abre-p :cierra-p} :cierra-p))]
      (if (map? (first inner))
        {:apli {:opdor (first inner)
                :opndo (transform (rest inner))}}
        {:apli {:opdor (transform (take (inc c) inner))
                :opndo (transform (drop (inc c) inner))}}))

    :else (first lxd)))

(defn parse [lexed]
  (->> lexed (map match) flatten transform))
