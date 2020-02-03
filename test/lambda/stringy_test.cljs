(ns lambda.stringy-test
  (:require
   [cljs.test :refer [deftest is]]
   [lambda.stringy :refer [toString]]
   [lambda.parser :refer [parse]]
   [lambda.lexer :refer [lex]]
   [lambda.core :refer [reducir]]))

(deftest vars
  (is (= "(x x)"
         (toString (reducir "(x x)")))))

(deftest abst
  (is (= "(x (λx.(y y)))"
         (toString (reducir "(x (λx.(y y)))"))))
  (is (= "((λx.((y y) x)) z)"
         (toString (parse (lex "((λx.((y y) x)) z)")))))
  (is (= "((λy.(x x)) (λx.(y y)))"
         (toString (parse (lex "((λy.(x x)) (λx.(y y)))"))))))
