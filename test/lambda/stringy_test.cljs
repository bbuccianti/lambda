(ns lambda.stringy-test
  (:require
   [cljs.test :refer [deftest is are]]
   [lambda.stringy :refer [toString]]
   [lambda.parser :refer [parse]]
   [lambda.lexer :refer [lex]]
   [lambda.core :refer [reducir]]))

(deftest vars
  (is (= "(x x)" (toString (reducir "(x x)")))))

(deftest abst
  (are [exp act] (= exp (-> act lex parse toString))
    "(x (λx.(y y)))"                  "(x (λx.(y y)))"
    "((λx.((y y) x)) z)"              "((λx.((y y) x)) z)"
    "((λy.(x x)) (λx.(y y)))"         "((λy.(x x)) (λx.(y y)))"))
