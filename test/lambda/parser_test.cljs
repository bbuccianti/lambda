(ns lambda.parser-test
  (:require
   [cljs.test :refer-macros [deftest is testing]]
   [lambda.lexer :as l]
   [lambda.parser :as p]))

(deftest expressions
  (is (= '((λ x (x y)) (w z))
         (p/parse (l/lex "((λ.(x y)) z)")))))
