(ns lambda.normalize-test
  (:require
   [cljs.test :refer-macros [deftest is testing]]
   [lambda.normalize :as n]
   [lambda.lexer :as l]))

(deftest lambda-restore
  (is (= (l/lex "(λx.λy.y x) a b")
         (n/restore-lambda (l/lex "(λx y.y x) a b")))
      (= (l/lex "(λx.λy.(λy.λx. c v)) a b")
         (n/restore-lambda (l/lex "(λx y.(λy x. c v)) a b")))))

(deftest restore  
  (is (= (l/lex "(((λx.(λy.(y x))) a) b)")
         (n/restore (l/lex "(λx y.y x) a b"))))
  (is (= (l/lex "(((x x) x) x)")
         (n/restore (l/lex "x x x x"))))
  (is (= (l/lex "(λx.(x (λy.y)))")
         (n/restore (l/lex "λx.x λy.y"))))
  (is (= (l/lex "(λx.((x (λy.((y x) x))) x))")
         (n/restore (l/lex "λx.(x λy.y x x) x"))))
  )
