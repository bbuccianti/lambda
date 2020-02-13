(ns lambda.reducer-test
  (:require
   [cljs.test :refer [deftest is are]]
   [lambda.lexer :refer [lex]]
   [lambda.normalize :refer [restore]]
   [lambda.parser :refer [parse]]
   [lambda.reducer :refer [reduct]]))

(deftest vars
  (is (= {:var "x"} (-> "x" lex parse reduct)))
  (is (= {:apli {:opdor {:var "x"} :opndo {:var "x"}}}
         (-> "(x x)" lex parse reduct))))

(deftest aplicacion
  (is (= {:apli {:opdor {:var "a"} :opndo {:var "a"}}}
         (-> "((λy.(y y)) a)" lex parse reduct)))
  (is (= {:apli {:opdor {:var "a"} :opndo {:var "x"}}}
         (-> "((λy.(y x)) a)" lex parse reduct)))
  (is (= {:apli {:opdor {:var "x"} :opndo {:var "a"}}}
         (-> "((λy.(x y)) a)" lex parse reduct))))

(deftest expresion
  (are [exp act] (= (-> exp lex restore parse)
                    (-> act lex restore parse reduct))
    "((a a) (a a))" "(λx.x x) (a a)"
    "z y z" "(λx.x y x) z"
    "z a z" "(λx y. x y x) z a"))

