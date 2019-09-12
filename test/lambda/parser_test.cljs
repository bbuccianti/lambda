(ns lambda.parser-test
  (:require
   [cljs.test :refer-macros [deftest is testing run-tests]]
   [lambda.lexer :as l]
   [lambda.parser :as p]))

(deftest variables
  (is (= [{:var "x"}]
         (p/parse (l/lex "x")))))

(deftest aplicacion
  (is (= {:apli {:opdor {:var "x"}
                 :opndo {:var "x"}}}
         (p/parse (l/lex "(x x)"))))
  (is (= {:apli
          {:opdor {:abst
                   {:param {:var "y"}
                    :cuerpo {:apli {:opdor {:var "y"}
                                    :opndo {:var "x"}}}}}
           :opndo {:var "a"}}}
         (p/parse (l/lex "((λy.(y x)) a)")))))

(deftest abstraccion
  (is (= {:abst {:param {:var "x"}
                 :cuerpo {:apli {:opdor {:apli {:opdor {:var "x"}
                                                :opndo {:var "x"}}}
                                 :opndo nil}}}}
         (p/parse (l/lex "(λx.(x x))")))))
