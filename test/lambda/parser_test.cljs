(ns lambda.parser-test
  (:require
   [cljs.test :refer-macros [deftest is testing]]
   [lambda.lexer :as l]
   [lambda.parser :as p]))

(deftest variables
  (is (= [{:var "x"}]
         (p/parse (l/lex "x")))))

(deftest aplicacion
  (is (= {:apli {:opdor {:var "x"}
                 :opndo {:var "x"}}}
         (p/parse (l/lex "(x x)")))))

(deftest abstraccion
  (is (= {:abst {:param {:var "x"}
                 :cuerpo {:apli {:opdor {:var "x"}
                                 :opndo {:var "x"}}}}}
         (p/parse (l/lex "(λx.(x x))")))))
