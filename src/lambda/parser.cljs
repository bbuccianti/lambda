(ns lambda.parser
  (:require
   [lambda.lexer :refer [lex]]
   [lambda.normalizer :refer [restore next-x]]))

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
             "Y" (-> "λf.(λx.f (x x)) (λx.f (x x))" lex restore)})

(defn- find-matching-close [lxd]
  (reduce (fn [acc [i elx]]
            (if (= :cierra-p elx)
              (if (>= 0 (dec acc)) (reduced i) (dec acc))
              (inc acc)))
          0 (->> (map-indexed list lxd)
                 (filter #(#{:cierra-p :abre-p} (second %)))
                 rest)))

(defn- match [item]
  (case (:tipo item)
    :ident {:var (:string item)}
    :combi (map match (get combis (:string item)))
    (:tipo item)))

(defn- transform [lxd]
  (cond
    (and (= (first lxd) :abre-p) (= (second lxd) :lambda))
    (let [punto (next-x :punto lxd)]
      {:abst
       {:param {:ident (:var (nth lxd (dec punto)))}
        :cuerpo (transform (drop (inc punto) lxd))}})

    (= (first lxd) :abre-p)
    (if (and (>= 3 (count lxd)) (<= (count lxd) 5))
      {:apli {:opdor (nth lxd 1) :opndo (nth lxd 2)}}

      (let [c (find-matching-close lxd)]
        (if (map? (second lxd))
          {:apli {:opdor (nth lxd 1)
                  :opndo (transform (->> lxd (take c) (drop 2)))}}

          {:apli {:opdor (transform (->> lxd (take c) (drop 1)))
                  :opndo (transform (drop (inc c) lxd))}})))

    :else (first lxd)))

(defn parse [lexed]
  (-> (map match lexed)
      flatten
      transform))
