(ns lambda.debruijnator-test
  (:require
   [cljs.test :refer [deftest is are]]
   [lambda.lexer :refer [lex]]
   [lambda.normalize :refer [restore]]
   [lambda.parser :refer [parse]]
   [lambda.reducer :refer [reduct]]
   [lambda.debruijnator :refer [debruijn]]))

(deftest one-level
  (are [exp act]
      (= (-> exp) (-> act lex restore parse debruijn))
    {:abst
     {:param
      {:var "x" :index 1}
     :cuerpo
     {:var "x" :index 1}}}
    "λx.x"

    {:abst
     {:param
      {:var "x" :index 1}
     :cuerpo
      {:apli {:opdor {:var "x" :index 1}
              :opndo {:var "x" :index 1}}}}}
    "λx.x x"

    {:abst
     {:param
      {:var "x" :index 1}
     :cuerpo
      {:apli {:opdor {:var "x" :index 1}
              :opndo {:var "y"}}}}}
    "λx.x y"
    ))
