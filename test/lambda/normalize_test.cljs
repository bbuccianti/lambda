(ns lambda.normalize-test
  (:require
   [cljs.test :refer [deftest is are]]
   [lambda.normalize :refer [restore restore-lambda]]
   [lambda.lexer :refer [lex]]))

(deftest lambda-restoring
  (is (= (lex "(λx.λy.y x) a b") (-> "(λx y.y x) a b" lex restore-lambda))))

(deftest restoring
  (are [expected actual] (= (lex expected) (-> actual lex restore))
    "(I x)"                                  "I x"
    "((S K) K)"                              "S K K"
    "(((x x) x) x)"                          "x x x x"
    "(λx.(x (λy.y)))"                        "λx.x λy.y"
    "((λx.(x x))(a a))"                      "(λx.x x)(a a)"
    "(λx.((x (λy.((y x) x))) x))"            "λx.(x λy.y x x) x"
    "(((λx.(λy.(y x))) a) b)"                "(λx y.y x) a b"
    "(((λx.(λy.(x y)))(λx.(x x)))a)"         "(λx y.x y)(λx.x x) a"
    "(((λx.(λy.(y x))) a) (λx.(x x)))"       "(λx y.y x) a (λx.x x)"
    "((λx.((x y) x)) z)"                     "(λx.(x y x)) z"
    "(λx.(((x x) x) z))"                     "λx.((x x x) z)"
    "(((λx.((x y) x)) (λz.((z z) z))) a)"    "(λx.(x y x)) (λz.(z z z)) a"

    "((((λx.(λy.(λz.((y z)x)))) (λh.(z z)))(λx.(x x)))(λy.(y y)))"
    "(λ x y z.y z x) (λh.z z) (λx.x x) (λy.y y)"

    "((((((λx.(λy.(λz.((x z) y)))) (λx.(λy.(λz.((x z) y))))) a) b) c) a) "
    "(λx y z. x z y) (λx y z. x z y) a b c a"))

