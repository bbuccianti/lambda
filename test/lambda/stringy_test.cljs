(ns lambda.stringy-test
  (:require
   [cljs.test :refer-macros [deftest is testing]]
   [lambda.stringy :refer [toString]]
   [lambda.parser :refer [parse]]
   [lambda.lexer :refer [lex]]
   [lambda.core :refer [reducir]]))

(deftest vars
  (is (= "(x x)"
         (toString (reducir "(x x)")))))

(deftest abst
  (is (= "(x (λx.(y y)))"
         (toString (parse (lex "(x (λx.(y y)))"))))))
