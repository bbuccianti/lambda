(ns lambda.reducer-test
  (:require
   [cljs.test :refer [deftest are]]
   [lambda.lexer :refer [lex]]
   [lambda.normalize :refer [restore]]
   [lambda.parser :refer [parse]]
   [lambda.reducer :refer [all-reductions]]))

(deftest aplicaciones
  (are [exp act] (= (-> exp lex restore parse)
                    (-> act lex restore parse all-reductions last))
    "x"                "x"
    "(x x)"            "x x"
    "a a"              "(λy.y y) a"
    "a x"              "(λy.y x) a"
    "x a"              "(λy.x y) a"
    "(a a) (a a)"      "(λx.x x) (a a)"
    "z y z"            "(λx.x y x) z"
    "z a z"            "(λx y. x y x) z a"
    "a c"              "(λx y. x y) (λz. z c) a"
    "((x (λt.t)) x)"   "(λu. u (λt. t) ((λy. y) u)) ((λz. z) x)"
    "λf.λx.f (f x)"    "(λm.λn.λf.λx.m f (n f x)) (λf.λx.f x) (λf.λx.f x)"))

