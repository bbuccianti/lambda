(ns lambda.lexer
  (:require
   [clojure.string :refer [lower-case upper-case split trim]]))

(defn- translate [s]
  (condp = s
    "(" :abre-p
    ")" :cierra-p
    "λ" :lambda
    "." :punto
    (lower-case s) :ident
    (upper-case s) :combi))

(defn lex [s]
  (->> (re-seq #"(\(|\)|\.|[A-Za-z]+|λ)" s)
       (map #(let [trimmed ((comp trim first) %)]
               (hash-map :tipo (translate trimmed)
                         :string trimmed)))))
