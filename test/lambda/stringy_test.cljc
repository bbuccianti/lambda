(ns lambda.stringy-test
  (:require
   #?@(:clj [[clojure.test :refer [deftest is are]]]
       :cljs [[cljs.test :refer [deftest is are]]])
   [lambda.stringy :refer [toString]]
   [lambda.lexer :refer [lex]]
   [lambda.parser :refer [parse]]))

(deftest vars
  (is (= "x x" (-> "(x x)" lex parse toString))))

(deftest abst
  (are [exp act] (= exp (-> act lex parse toString))
    "x (λx.y y)"                 "(x (λx.(y y)))"
    "(λx.y y x) z"               "((λx.((y y) x)) z)"
    "(λy.x x) (λx.y y)"          "((λy.(x x)) (λx.(y y)))"
    "(λf x.f (f (f x)))"         "(λf.(λx.(f (f (f x)))))"))
