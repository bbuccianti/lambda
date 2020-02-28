(ns lambda.debruijnator-test
  (:require
   [cljs.test :refer [deftest are]]
   [lambda.lexer :refer [lex]]
   [lambda.normalize :refer [restore]]
   [lambda.parser :refer [parse]]
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
    "λx.x y"))

(deftest multi-level
  (are [exp act]
      (= (-> exp) (-> act lex restore parse debruijn))
    {:abst
     {:param
      {:var "x" :index 2}
     :cuerpo
      {:abst
       {:param
        {:var "y" :index 1}
        :cuerpo
        {:var "x" :index 2}}}}}
    "λx.λy.x"

    {:abst
     {:param
      {:var "x" :index 2}
     :cuerpo
      {:abst
       {:param
        {:var "y" :index 1}
        :cuerpo
        {:apli {:opdor {:var "x" :index 2}
              :opndo {:var "y" :index 1}}}}}}}
    "λx.λy.x y"

    {:abst
     {:param {:var "x" :index 4}
      :cuerpo
      {:abst
       {:param {:var "y" :index 3}
        :cuerpo
        {:abst
         {:param {:var "s" :index 2}
          :cuerpo
          {:abst
           {:param {:var "z" :index 1}
            :cuerpo
            {:apli
             {:opdor {:apli {:opdor {:var "x" :index 4}
                             :opndo {:var "s" :index 2}}}
              :opndo
              {:apli
               {:opdor {:apli {:opdor {:var "y" :index 3}
                               :opndo {:var "s" :index 2}}}
                :opndo {:var "z" :index 1}}}}}}}}}}}}}
    "λx.λy.λs.λz.x s (y s z)"))

(deftest nested
  (are [exp act]
      (= (-> exp) (-> act lex restore parse debruijn))
    {:apli
     {:opdor
      {:abst
       {:param {:var "x" :index 1}
        :cuerpo {:apli {:opdor {:var "x" :index 1}
                        :opndo {:var "x" :index 1}}}}}
      :opndo
      {:abst
       {:param {:var "x" :index 1}
        :cuerpo {:apli {:opdor {:var "x" :index 1}
                        :opndo {:var "x" :index 1}}}}}}}
    "(λx.x x) (λx.x x)"

    {:apli
     {:opdor
      {:abst
       {:param {:var "x" :index 1}
        :cuerpo {:apli {:opdor {:var "x" :index 1}
                        :opndo {:var "x" :index 1}}}}}
      :opndo
      {:abst
       {:param {:var "y" :index 1}
        :cuerpo {:apli {:opdor {:var "y" :index 1}
                        :opndo {:var "y" :index 1}}}}}}}
    "(λx.x x) (λy.y y)"))

