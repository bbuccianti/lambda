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
                 :cuerpo {:apli {:opdor {:var "x"}
                                 :opndo {:var "x"}}}}}
         (p/parse (l/lex "(λx.(x x))")))))

(deftest expresiones
  (is (= {:apli
          {:opdor {:apli
                   {:opdor {:abst {:param {:var "x"}
                                   :cuerpo {:apli {:opdor {:var "x"}
                                                   :opndo {:var "y"}}}}}
                    :opndo {:abst {:param {:var "y"}
                                   :cuerpo {:apli {:opdor {:var "y"}
                                                   :opndo {:var "y"}}}}}}}
           :opndo {:var "z"}}}
         (p/parse (l/lex "(((λx.(x y)) (λy.(y y))) z)")))))
