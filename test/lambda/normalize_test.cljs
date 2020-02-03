(ns lambda.normalize-test
  (:require
   [cljs.test :refer [deftest is]]
   [lambda.normalize :as n]
   [lambda.lexer :as l]))

(deftest lambda-restore
  (is (= (l/lex "(λx.λy.y x) a b")
         (n/restore-lambda (l/lex "(λx y.y x) a b")))
      ))

(deftest restore
  (is (= (l/lex "(((λx.(λy.(y x))) a) b)")
         (n/restore (l/lex "(λx y.y x) a b"))))
  (is (= (l/lex "(((x x) x) x)")
         (n/restore (l/lex "x x x x"))))
  (is (= (l/lex "(λx.(x (λy.y)))")
         (n/restore (l/lex "λx.x λy.y"))))
  (is (= (l/lex "(λx.((x (λy.((y x) x))) x))")
         (n/restore (l/lex "λx.(x λy.y x x) x"))))
  (is (= (l/lex "((λx.(x x))(a a))")
         (n/restore (l/lex "(λx.x x)(a a)"))))
  (is (= (l/lex "(((λx.(λy.(x y)))(λx.(x x)))a)")
         (n/restore (l/lex "(λx y.x y)(λx.x x) a"))))
  (is (= (l/lex "(((λx.(λy.(y x))) a) (λx.(x x)))")
         (n/restore (l/lex "(λx y.y x) a (λx.x x)"))))
  (is (= (l/lex "((((λx.(λy.(λz.((y z)x)))) (λh.(z z)))(λx.(x x)))(λy.(y y)))")
         (n/restore (l/lex "(λ x y z.y z x) (λh.z z) (λx.x x) (λy.y y)"))))
  )
