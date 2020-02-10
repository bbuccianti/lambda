(ns lambda.normalize-test
  (:require
   [cljs.test :refer [deftest is are]]
   [lambda.normalize :refer [restore restore-lambda]]
   [lambda.lexer :refer [lex]]))

(deftest lambda-restoring
  (is (= (lex "(λx.λy.y x) a b")
         (restore-lambda (lex "(λx y.y x) a b")))
      ))

(deftest restoring
  (are [expected actual] (= (lex expected) (-> actual lex restore))
    "(((λx.(λy.(y x))) a) b)" "(λx y.y x) a b"

    "(((x x) x) x)" "x x x x"

    "(λx.(x (λy.y)))" "λx.x λy.y"

    "(λx.((x (λy.((y x) x))) x))" "λx.(x λy.y x x) x"

    "((λx.(x x))(a a))" "(λx.x x)(a a)"

    "(((λx.(λy.(x y)))(λx.(x x)))a)" "(λx y.x y)(λx.x x) a"

    "(((λx.(λy.(y x))) a) (λx.(x x)))" "(λx y.y x) a (λx.x x)"

    "((((λx.(λy.(λz.((y z)x)))) (λh.(z z)))(λx.(x x)))(λy.(y y)))"
    "(λ x y z.y z x) (λh.z z) (λx.x x) (λy.y y)"

    "((λx.((x y) x)) z)" "(λx.(x y x)) z"))
