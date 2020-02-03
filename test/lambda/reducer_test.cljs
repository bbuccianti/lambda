(ns lambda.reducer-test
  (:require
   [cljs.test :refer-macros [deftest is]]
   [lambda.lexer :as l]
   [lambda.parser :as p]
   [lambda.reducer :as r]))

(deftest vars
  (is (= {:var "x"}
         (r/reduct (p/parse (l/lex "x")))))
  (is (= {:apli
          {:opdor {:var "x"}
           :opndo {:var "x"}}}
         (r/reduct (p/parse (l/lex "(x x)"))))))

(deftest aplicacion
  (is (= {:apli
          {:opdor {:var "a"}
           :opndo {:var "a"}}}
         (r/reduct (p/parse (l/lex "((λy.(y y)) a)")))))
  (is (= {:apli
          {:opdor {:var "a"}
           :opndo {:var "x"}}}
         (r/reduct (p/parse (l/lex "((λy.(y x)) a)")))))
  (is (= {:apli
          {:opdor {:var "x"}
           :opndo {:var "a"}}}
         (r/reduct (p/parse (l/lex "((λy.(x y)) a)"))))))

(deftest expresion
  (is (= {:apli
          {:opdor {:apli
                   {:opdor {:var "a"}
                    :opndo {:var "a"}}}
           :opndo {:apli
                   {:opdor {:var "a"}
                    :opndo {:var "a"}}}}}
         (r/reduct (p/parse (l/lex "((λx.(x x)) (a a))"))))))

