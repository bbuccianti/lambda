(ns lambda.reducer-test
  (:require
   [cljs.test :refer [deftest are]]
   [lambda.lexer :refer [lex]]
   [lambda.normalizer :refer [restore]]
   [lambda.parser :refer [parse]]
   [lambda.reducer :refer [all-reductions count-replacements]]
   [lambda.debruijnator :refer [debruijn]]
   [lambda.stringy :refer [toString]]))

(deftest aplicaciones
  (are [exp act] (= (-> exp lex restore parse debruijn)
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
    "(λf x.f (f x))" "(λm n f x.m f (n f x)) (λf x.f x) (λf x.f x)"
    "(λx y.x)" "(λy.(λx y.x)) a"))

(deftest dificcile
  (are [exp act] (= (-> exp lex restore parse toString)
                    (-> act lex restore parse all-reductions last toString))
    "(λx.z)" "(λx.((λy.z) z))"

    "x y" "(λx y. y x) y x"

    "(x (λt.t)) x"   "(λu.u (λt.t) ((λy.y) u)) ((λz. z) x)"

    ;; note that z have different index's here
    "(λz y z.z (y z))"
    "(λx y z.x z (y z)) ((λx y.x) (λx y z.x z (y z))) (λx y.x)"

    "(λy.(λx y z.x z (y z)))" "((λx y.x) (λx y z.x z (y z)))"
    "(λx y.x y)" "(λb.(λx y.x y)) a"
    "n M z" "(λx y x.x y z) (λx y.y) M n"

    ;; Pow 2 3 = 8
    "λf.λx.f (f (f (f (f (f (f (f x)))))))"
    "(λm.λn.λf.λx.n m f x) (λf.λx.f (f x)) (λf.λx.f (f (f x)))"

    ;; Pred 5 = 4
    "λf.λx.f (f (f (f x)))"
    "(λn.λf.λx.n (λg.λh.h (g f)) (λu.x) (λu.u)) (λf.λx.f (f (f (f (f x)))))"

    ;; Add 2 3 = 5
    "λf.λx.f (f (f (f (f x))))"
    "(λm.λn.λf.λx.m f (n f x)) (λf.λx.f (f x)) (λf.λx.f (f (f x)))"

    ;; Sub 3 1 = 2
    "λf.λx.f (f x)"
    "(λm.λn.n (λn.λf.λx.n (λg.λh.h (g f)) (λu.x) (λu.u)) m) (λf.λx.f (f (f x))) (λf.λx.f x)"

    ;; Mul 2 3 = 6
    "λf.λx.f (f (f (f (f (f x)))))"
    "(λm.λn.λf.λx.m (n f) x) (λf.λx.f (f x)) (λf.λx.f (f (f x)))"))
