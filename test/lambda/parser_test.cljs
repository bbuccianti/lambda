(ns lambda.parser-test
  (:require
   [cljs.test :refer [deftest is are]]
   [lambda.lexer :refer [lex]]
   [lambda.parser :refer [parse]]
   [lambda.normalize :refer [restore]]))

(deftest variables
  (is (= {:var "x" :index 0} (-> "x" lex parse))))

(deftest aplicacion
  (is (= {:apli {:opdor {:var "x" :index 0}
                 :opndo {:var "x" :index 0}}}
         (-> "(x x)" lex parse)))
  (is (= {:apli
          {:opdor {:abst
                   {:param {:var "y" :index 0}
                    :cuerpo {:apli {:opdor {:var "y" :index 0}
                                    :opndo {:var "x" :index 0}}}}}
           :opndo {:var "a" :index 0}}}
         (-> "((λy.(y x)) a)" lex parse))))

(deftest abstraccion
  (is (= {:abst {:param {:var "x" :index 0}
                 :cuerpo {:apli {:opdor {:var "x" :index 0}
                                 :opndo {:var "x" :index 0}}}}}
         (-> "(λx.(x x))" lex parse))))

(deftest expresiones
  (are [exp act] (= exp (-> act lex parse))
    {:apli {:opdor
            {:apli
             {:opdor {:abst {:param {:var "x" :index 0}
                             :cuerpo {:apli {:opdor {:var "x" :index 0}
                                             :opndo {:var "y" :index 0}}}}}
              :opndo {:abst {:param {:var "y" :index 0}
                             :cuerpo {:apli {:opdor {:var "y" :index 0}
                                             :opndo {:var "y" :index 0}}}}}}}
            :opndo {:var "z" :index 0}}}
    "(((λx.(x y)) (λy.(y y))) z)"

    {:apli {:opdor
            {:apli {:opdor
                    {:abst {:param {:var "x" :index 0}
                            :cuerpo {:abst {:param {:var "y" :index 0}
                                            :cuerpo {:apli {:opdor {:var "y" :index 0}
                                                            :opndo {:var "x" :index 0}}}}}}}
                    :opndo {:var "a" :index 0}}}
            :opndo {:var "b" :index 0}}}
    "(((λx.(λy.(y x))) a) b)"

    {:apli {:opdor {:var "x" :index 0}
            :opndo {:abst {:param {:var "x" :index 0}
                           :cuerpo {:apli {:opdor {:var "y" :index 0}
                                           :opndo {:var "y" :index 0}}}}}}}
    "(x (λx.(y y)))"

    {:apli {:opdor {:abst {:param {:var "x" :index 0}
                           :cuerpo {:apli {:opdor {:apli
                                                   {:opdor {:var "y" :index 0}
                                                    :opndo {:var "y" :index 0}}}
                                           :opndo {:var "x" :index 0}}}}}
            :opndo {:var "z" :index 0}}}
    "((λx.((y y) x)) z)"

    {:apli {:opdor {:abst
                    {:param {:var "x" :index 0}
                     :cuerpo {:apli {:opdor {:apli {:opdor {:var "x" :index 0}
                                                    :opndo {:var "y" :index 0}}}
                                     :opndo {:var "x" :index 0}}}}}
            :opndo {:var "z" :index 0}}}
    "((λx.((x y) x)) z)"))
