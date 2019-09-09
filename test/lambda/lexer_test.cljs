(ns lambda.lexer-test
  (:require
   [cljs.test :refer-macros [deftest is testing]]
   [lambda.lexer :as l]))

(deftest tokens
  (is (= '({:tipo :abre-p :string "("}
           {:tipo :cierra-p :string ")"}
           {:tipo :punto :string "."}
           {:tipo :lambda :string "λ"}
           {:tipo :ident :string "x"}
           {:tipo :combi :string "I"})
         (l/lex "( ) . λ x I")))
  (is (= '({:tipo :abre-p :string "("}
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
         (l/lex "((λx.(x y))z)"))))


