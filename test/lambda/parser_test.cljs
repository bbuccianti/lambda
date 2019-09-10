(ns lambda.parser-test
  (:require
   [cljs.test :refer-macros [deftest is testing]]
   [lambda.lexer :as l]
   [lambda.parser :as p]))

(deftest variables
  (is (= [{:var "x"}]
         (p/parse (l/lex "x")))))

(deftest aplicacion
  (is (= {:apl [{:var "x"} {:var "x"}]}
         (p/parse (l/lex "(x x)")))))
