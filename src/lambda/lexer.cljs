(ns lambda.lexer
  (:require
   [clojure.string :as string]))

(defn- all? [f s]
  (and (re-matches #"[A-Za-z]*" s) (= s (f s))))

(defn- traducir [s]
  (cond
    (= "(" s)                    :abre-p
    (= ")" s)                    :cierra-p
    (= "λ" s)                    :lambda
    (= "." s)                    :punto
    (all? #'string/lower-case s) :ident
    (all? #'string/upper-case s) :combi))

(defn lex [s]
  (->> (string/split s #"(\(|\)|\.|[A-Za-z]+)")
       (map string/trim)
       (remove empty?)
       (mapv #(into {} {:tipo (traducir %) :string %}))))
