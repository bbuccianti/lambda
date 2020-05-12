(ns lambda.lexer
  (:require
   [clojure.string :refer [lower-case upper-case split trim]]))

(defn- all? [f s]
  (and (re-matches #"[A-Za-z]*" s) (= s (f s))))

(defn- translate [s]
  (cond
    (= "(" s)           :abre-p
    (= ")" s)           :cierra-p
    (= "λ" s)           :lambda
    (= "\\" s)          :lambda
    (= "." s)           :punto
    (all? lower-case s) :ident
    (all? upper-case s) :combi))

(defn lex [s]
  (->> (re-seq #"(\(|\)|\.|[A-Za-z]+|λ)" s)
       (map (comp trim first))
       #_(remove empty?)
       (map #(into {} {:tipo (translate %) :string %}))))
