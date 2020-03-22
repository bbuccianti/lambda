(ns lambda.normalizer-test
  (:require
   [cljs.test :refer [deftest are]]
   [lambda.normalizer :refer [restore next-close regroup isolate]]
   [lambda.lexer :refer [lex]]))

(deftest closing
  (are [exp act] (= exp (-> act lex next-close))
    3 "(())"
    7 "(((())))"))

(deftest isolation
  (are [exp act] (= exp (-> act lex isolate))
    (list {:tipo :ident :string "x"}
          {:tipo :ident :string "x"})
    "x x"

    (list {:tipo :ident :string "x"}
          (list {:tipo :abre-p :string "("}
                {:tipo :ident :string "a"}
                {:tipo :ident :string "b"}
                {:tipo :cierra-p :string ")"}))
    "x (a b)"

    (list {:tipo :combi :string "A"}
          (list {:tipo :abre-p :string "("}
                {:tipo :ident :string "b"}
                {:tipo :combi :string "C"}
                {:tipo :cierra-p :string ")"})
          {:tipo :combi :string "D"})
    "A (b C) D"

    (list (list {:tipo :abre-p :string "("}
                {:tipo :lambda :string "λ"}
                {:tipo :ident :string "x"}
                {:tipo :punto :string "."}
                {:tipo :ident :string "x"}
                {:tipo :cierra-p :string ")"})
          (list {:tipo :abre-p :string "("}
                {:tipo :lambda :string "λ"}
                {:tipo :ident :string "y"}
                {:tipo :punto :string "."}
                {:tipo :ident :string "y"}
                {:tipo :cierra-p :string ")"})
          {:tipo :ident :string "a"}
          {:tipo :combi :string "B"})
    "(λx.x) (λy.y) a B"))

(deftest regrouping
  (are [exp act] (= (lex exp) (-> act lex restore))
    "(x x)" "x x"
    "(K a)" "K a"
    "(λx.x)" "(λx.x)"
    "(λx.(x y))" "(λx.x y)"
    "((x x) x)" "x x x"
    "(((x A) x) A)" "x A x A"
    "((x x) (x x))" "x x (x x)"
    "((A (b b)) A)" "A (b b) A"
    "((A ((b c) d)) E)" "A (b c d) E"
    "((λx.(x x)) (a a))" "(λx.x x) (a a)"
    "((λx.(x y)) (λa.(b c)))" "(λx.x y) (λa.b c)"
    "(((λx.((c x) e)) (a a)) b)" "(λx.c x e) (a a) b"))

(deftest restoring
  (are [exp act] (= (lex exp) (-> act lex restore))
    "(I x)" "I x"
    "((S K) K)" "S K K"
    "(((λx.(λy.(y x))) a) b)" "(λx y.y x) a b"
    "((λx.((x y) x)) z)" "(λx.x y x) z"
    "(((λx.(λy.(y x))) a) b)" "(λx y.y x) a b"
    "(((λx.(λy.(x y)))(λx.(x x)))a)" "(λx y.x y)(λx.x x) a"
    "(((λx.(λy.(y x))) a) (λx.(x x)))" "(λx y.y x) a (λx.x x)"
    "((λx.(((x x) x) z)) a)" "(λx.(x x x) z) a"
    "(((λx.((x y) x)) (λz.((z z) z))) a)" "(λx.(x y x)) (λz.(z z z)) a"
    "((x (λt.t)) x)" "(x (λt.t)) x"

    "((((λx.(λy.(λz.((y z)x)))) (λh.(z z)))(λx.(x x)))(λy.(y y)))"
    "(λ x y z.y z x) (λh.z z) (λx.x x) (λy.y y)"

    "(λx.(λy.(λs.(λz.((x s) ((y s) z))))))" "(λx y s z.x s (y s z))"

    "((((((λx.(λy.(λz.((x z) y)))) (λx.(λy.(λz.((x z) y))))) a) b) c) a) "
    "(λx y z. x z y) (λx y z. x z y) a b c a"

    "((λx.((λu.u) (λv.(x v)))) ((λt.(t t)) w))"
    "(λx.(λu.u) (λv.x v)) ((λt.t t) w)"))
