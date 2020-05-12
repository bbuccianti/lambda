(ns lambda.parser
  (:require
   [lambda.lexer :refer [lex]]
   [lambda.normalizer :refer [next-x next-close]]))

(def combis {"I" (lex "(λx.x)")
             "K" (lex "(λx.(λy.x))")
             "S" (lex "(λx.(λy.(λz.((x z) (y z)))))")
             "B" (lex "(λx.(λy.(λz.(x (y z)))))")
             "C" (lex "(λx.(λy.(λz.((x z) y))))")
             "D" (lex "(λx.(λy.(λz.(λv.((x y) (z v)))")
             "J" (lex "(λx.(λy.(λz.(λv.((x y) ((x v) z)))")
             "M" (lex "(λx.(x x))")
             "O" (lex "(λx.(λy.y))")
             "R" (lex "(λx.(λy.(λz.((y z) x))))")
             "Q" (lex "(λx.(λy.(λz.(y (x z)))))")
             "W" (lex "(λx.(λy.((x y) y)))")
             "T" (lex "(λx.(λy.(y x)))")
             "Y" (lex "(λf.((λx.(f (x x))) (λx.(f (x x)))))")})

(defn- match [item]
  (case (:tipo item)
    :ident {:var (:string item)}
    :combi (map match (get combis (:string item)))
    (:tipo item)))

(defn- transform [lxd]
  (cond
    (and (= (first lxd) :abre-p) (= (second lxd) :lambda))
    (let [punto (next-x :punto lxd)
          c (next-close lxd #{:abre-p :cierra-p} :cierra-p)]
      {:abst
       {:param {:ident (:var (nth lxd (dec punto)))}
        :cuerpo (transform (->> lxd (take c) (drop (inc punto))))}})

    (= (first lxd) :abre-p)
    (let [inner (rest (butlast lxd))
          c (next-close inner #{:abre-p :cierra-p} :cierra-p)]
      (if (map? (first inner))
        {:apli {:opdor (first inner)
                :opndo (transform (rest inner))}}
        {:apli {:opdor (transform (take (inc c) inner))
                :opndo (transform (drop (inc c) inner))}}))

    :else (first lxd)))

(defn parse [lexed]
  (-> (map match lexed)
      flatten
      transform))
