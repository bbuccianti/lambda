(ns lambda.lexer-test
  (:require
   #?@(:clj  [[clojure.test :refer [deftest are]]]
       :cljs [[cljs.test :refer [deftest are]]])
   [lambda.lexer :refer [lex]]))

(deftest tokens
  (are [exp act] (= exp (lex act))
    [{:tipo :abre-p :string "("}
     {:tipo :cierra-p :string ")"}
     {:tipo :punto :string "."}
     {:tipo :lambda :string "λ"}
     {:tipo :ident :string "x"}
     {:tipo :combi :string "I"}]
    "( ) . λ x I"

    '({:tipo :abre-p :string "("}
      {:tipo :abre-p :string "("}
      {:tipo :lambda :string "λ"}
      {:tipo :ident :string "x"}
      {:tipo :punto :string "."}
      {:tipo :abre-p :string "("}
      {:tipo :ident :string "x"}
      {:tipo :ident :string "y"}
      {:tipo :cierra-p :string ")"}
      {:tipo :cierra-p :string ")"}
      {:tipo :ident :string "z"}
      {:tipo :cierra-p :string ")"})
    "((λx.(x y))z)"))


