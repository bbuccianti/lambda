(ns lambda.reducer-test
  (:require
   #?@(:clj [[clojure.test :refer [deftest are is]]]
       :cljs [[cljs.test :refer [deftest are is]]])
   [lambda.lexer :refer [lex]]
   [lambda.normalizer :refer [restore]]
   [lambda.parser :refer [parse]]
   [lambda.reducer :refer [all-reductions can-reduce?]]
   [lambda.stringy :refer [toString]]))

(deftest externs-from-the-left
  (are [exp act] (= exp (-> act lex restore parse can-reduce?))
    nil "x"
    nil "(x x)"
    [] "(λu.u (λt.t) ((λy.y) u)) ((λz.z) x)"
    []                           "(λx y.x) a"
    [:apli :opndo]               "a ((λx y.x) b)"
    [:apli :opdor]               "(λx y. y x) y x"
    [:apli :opdor :apli :opdor]  "(λy x z.z x y) a b c"
    [:abst :cuerpo]              "(λx.((λy.z y) z))"
    [:eta]                       "(λv.w w v)"))

(deftest simple-ones
    (are [exp act] (= exp (-> act lex restore parse
                              all-reductions last :reduction))
    "x"                "x"
    "x x"              "x x"
    "a a"              "(λy.y y) a"
    "a x"              "(λy.y x) a"
    "x a"              "(λy.x y) a"
    "a a (a a)"        "(λx.x x) (a a)"
    "z y z"            "(λx.x y x) z"
    "z a z"            "(λb y.b y b) z a"
    "a c"              "(λx y. x y) (λz. z c) a"
    "(λx y.x)"         "(λy.(λx y.x)) a"
    "(λx.z z)"         "(λx.((λy.z y) z))"
    "(λy.a b)"         "((λx y.x) (a b))"
    "n n"              "M n"
    "(λx y.x y)"       "(λb.(λx y.x y)) a"
    "x (λt.t) x"       "(λu.u (λt.t) ((λy.y) u)) ((λz. z) x)"

    "a (λx.x x) a"     "(λx.x (λx.x x) x) a"

    "n (λx.x x) z"    "(λx y x.x y z) (λx y.y) M n"
    "x y"              "(λx y. y x) y x"

    "(λx y z.x z (y z))"
    "(λy x y z.x z (y z)) a"

    "(λy x y z.x z (y z))"
    "((λx y.x) (λx y z.x z (y z)))"

    "(λy.y y (λx.y a) a)" "(λz.(λy.y y (λx.y z) z)) a"

    "a c (b c)"
    "(λx y z.x z (y z)) a b c"))

(deftest dificcile
  (are [exp act] (= exp (-> act lex restore parse all-reductions
                            last :reduction))
    ;; Eta rule
    "w w"
    "(λx.(λu.u) (λv.x v)) ((λt.t t) w)"

    ;; Pow 2 3 = 8
    "(λf x.f (f (f (f (f (f (f (f x))))))))"
    "(λm.λn.λf.λx.n m f x) (λf.λx.f (f x)) (λf.λx.f (f (f x)))"

    ;; Succ 3 = 4
    "(λf x.f (f (f (f x))))"
    "(λn.λf.λx.f (n f x)) (λf.λx.(f (f (f x))))"

    ;; Pred 5 = 4
    "(λf x.f (f (f (f x))))"
    "(λn.λf.λx.n (λg.λh.h (g f)) (λu.x) (λu.u)) (λf.λx.f (f (f (f (f x)))))"

    ;; Add 2 3 = 5
    "(λf x.f (f (f (f (f x)))))"
    "(λm.λn.λf.λx.m f (n f x)) (λf.λx.f (f x)) (λf.λx.f (f (f x)))"

    ;; Sub 3 1 = 2
    "(λf x.f (f x))"
    "(λm.λn.n (λn.λf.λx.n (λg.λh.h (g f)) (λu.x) (λu.u)) m) (λf.λx.f (f (f x))) (λf.λx.f x)"

    ;; Mul 2 3 = 6
    "(λf x.f (f (f (f (f (f x))))))"
    "(λm.λn.λf.λx.m (n f) x) (λf.λx.f (f x)) (λf.λx.f (f (f x)))"

    ;; Div 6 2 = 3
    "(λf x.f (f (f x)))"
    "(λn.((λf.(λx.f (x x)) (λx.f (x x))) (λc.λn.λm.λf.λx.(λd.(λp.λq.λr.p q r) ((λn.n (λz.(λx.λy.y))
    (λx.λy.x)) d) ((λf.λx.x) f x) (f (c d m f x))) ((λm.λn.n (λn.λf.λx.n (λg.λh.h (g f)) (λu.x)
    (λu.u)) m) n m))) ((λn.λf.λx.f (n f x)) n)) (λf.λx.f (f (f (f (f (f x)))))) (λf.λx.f (f x))"

    ;; Fibo 6 = 8
    "(λf x.f (f (f (f (f (f (f (f x))))))))"
    "(λn.n (λf.λa.λb.f b ((λm.λn.λf.λx.m f (n f x)) a b)) (λx.λy.x) (λf.λx.x) (λf.λx.f x)) (λf.λx.f (f (f (f (f (f x))))))"

    ;; Fact 3 = 6
    "(λf x.f (f (f (f (f (f x))))))"
    "((λf.(λx.f (x x)) (λx.f (x x))) (λf.λx.( λp.λq.λr.p q r) ((λn.n (λz.(λx.λy.y)) (λx.λy.x)) x) (λf.λx.f x) ((λm.λn.λf.λx.m (n f) x) x (f ((λn.λf.λx.n (λg.λh.h (g f)) (λu.x) (λu.u)) x))))) (λf.λx.f (f (f x)))"))
