(ns lambda.parser-test
  (:require
   [cljs.test :refer [deftest is are]]
   [lambda.lexer :refer [lex]]
   [lambda.parser :refer [parse]]
   [lambda.normalize :refer [restore]]))

(deftest variables
  (is (= {:var "x"} (-> "x" lex parse))))

(deftest aplicacion
  (is (= {:apli {:opdor {:var "x" }
                 :opndo {:var "x" }}}
         (-> "(x x)" lex parse)))
  (is (= {:apli
          {:opdor {:abst
                   {:param {:var "y" }
                    :cuerpo {:apli {:opdor {:var "y" }
                                    :opndo {:var "x" }}}}}
           :opndo {:var "a" }}}
         (-> "((λy.(y x)) a)" lex parse))))

(deftest abstraccion
  (is (= {:abst {:param {:var "x" }
                 :cuerpo {:apli {:opdor {:var "x" }
                                 :opndo {:var "x" }}}}}
         (-> "(λx.(x x))" lex parse))))

(deftest expresiones
  (are [exp act] (= exp (-> act lex parse))
    {:apli {:opdor
            {:apli
             {:opdor {:abst {:param {:var "x" }
                             :cuerpo {:apli {:opdor {:var "x" }
                                             :opndo {:var "y" }}}}}
              :opndo {:abst {:param {:var "y" }
                             :cuerpo {:apli {:opdor {:var "y" }
                                             :opndo {:var "y" }}}}}}}
            :opndo {:var "z" }}}
    "(((λx.(x y)) (λy.(y y))) z)"

    {:apli {:opdor
            {:apli {:opdor
                    {:abst {:param {:var "x" }
                            :cuerpo {:abst {:param {:var "y" }
                                            :cuerpo {:apli {:opdor {:var "y" }
                                                            :opndo {:var "x" }}}}}}}
                    :opndo {:var "a" }}}
            :opndo {:var "b" }}}
    "(((λx.(λy.(y x))) a) b)"

    {:apli {:opdor {:var "x" }
            :opndo {:abst {:param {:var "x" }
                           :cuerpo {:apli {:opdor {:var "y" }
                                           :opndo {:var "y" }}}}}}}
    "(x (λx.(y y)))"

    {:apli {:opdor {:abst {:param {:var "x" }
                           :cuerpo {:apli {:opdor {:apli
                                                   {:opdor {:var "y" }
                                                    :opndo {:var "y" }}}
                                           :opndo {:var "x" }}}}}
            :opndo {:var "z" }}}
    "((λx.((y y) x)) z)"

    {:apli {:opdor {:abst
                    {:param {:var "x" }
                     :cuerpo {:apli {:opdor {:apli {:opdor {:var "x" }
                                                    :opndo {:var "y" }}}
                                     :opndo {:var "x" }}}}}
            :opndo {:var "z" }}}
    "((λx.((x y) x)) z)"))

(deftest combinadores
  (are [exp act] (= exp (-> act lex parse))
    {:abst {:param {:var "x"}
            :cuerpo {:var "x"}}}
    "I"

    {:apli {:opdor {:abst {:param {:var "x"}
                           :cuerpo {:var "x"}}}
            :opndo {:var "y"}}}
    "(I y)"))

(deftest dificiles
  (are [exp act] (= exp (-> act lex restore parse))
    {:apli
     {:opdor
      {:abst
       {:param {:var "u"}
        :cuerpo
        {:apli {:opdor
                {:apli {:opdor {:var "u"}
                        :opndo {:abst {:param {:var "t"}
                                       :cuerpo {:var "t"}}}}}
                :opndo {:apli {:opdor {:abst {:param {:var "y"}
                                              :cuerpo {:var "y"}}}
                               :opndo {:var "u"}}}}}}}
      :opndo {:apli {:opdor {:abst {:param {:var "z"}
                                    :cuerpo {:var "z"}}}
                     :opndo {:var "x"}}}}}
    "(λu. u (λt. t) ((λy. y) u)) ((λz. z) x)"))
