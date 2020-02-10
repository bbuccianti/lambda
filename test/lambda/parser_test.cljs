(ns lambda.parser-test
  (:require
   [cljs.test :refer [deftest is]]
   [lambda.lexer :refer [lex]]
   [lambda.parser :refer [parse]]
   [lambda.normalize :refer [restore]]))

(deftest variables
  (is (= {:var "x"}
         (-> "x" lex parse))))

(deftest aplicacion
  (is (= {:apli {:opdor {:var "x"}
                 :opndo {:var "x"}}}
         (-> "(x x)" lex parse)))
  (is (= {:apli
          {:opdor {:abst
                   {:param {:var "y"}
                    :cuerpo {:apli {:opdor {:var "y"}
                                    :opndo {:var "x"}}}}}
           :opndo {:var "a"}}}
         (-> "((λy.(y x)) a)" lex parse))))

(deftest abstraccion
  (is (= {:abst {:param {:var "x"}
                 :cuerpo {:apli {:opdor {:var "x"}
                                 :opndo {:var "x"}}}}}
         (-> "(λx.(x x))" lex parse))))

(deftest expresiones
  (is (= {:apli
          {:opdor {:apli
                   {:opdor {:abst {:param {:var "x"}
                                   :cuerpo {:apli {:opdor {:var "x"}
                                                   :opndo {:var "y"}}}}}
                    :opndo {:abst {:param {:var "y"}
                                   :cuerpo {:apli {:opdor {:var "y"}
                                                   :opndo {:var "y"}}}}}}}
           :opndo {:var "z"}}}
         (-> "(((λx.(x y)) (λy.(y y))) z)" lex parse)))
  (is (= {:apli
          {:opdor {:apli
                   {:opdor
                    {:abst {:param {:var "x"}
                            :cuerpo {:abst {:param {:var "y"}
                                            :cuerpo {:apli {:opdor {:var "y"}
                                                            :opndo {:var "x"}}}}}}}
                    :opndo {:var "a"}}}
           :opndo {:var "b"}}}
         (-> "(((λx.(λy.(y x))) a) b)" lex parse)))
  (is (= {:apli
          {:opdor {:var "x"}
           :opndo {:abst {:param {:var "x"}
                          :cuerpo {:apli {:opdor {:var "y"}
                                          :opndo {:var "y"}}}}}}}
         (-> "(x (λx.(y y)))" lex parse)))
  (is (= {:apli
          {:opdor {:abst {:param {:var "x"}
                          :cuerpo {:apli {:opdor {:apli
                                                  {:opdor {:var "y"}
                                                   :opndo {:var "y"}}}
                                          :opndo {:var "x"}}}}}
           :opndo {:var "z"}}}
         (-> "((λx.((y y) x)) z)" lex parse)))
  (is (= {:apli
          {:opdor {:abst
                   {:param {:var "x"}
                    :cuerpo {:apli {:opdor {:apli {:opdor {:var "x"}
                                                   :opndo {:var "y"}}}
                                    :opndo {:var "x"}}}}}
           :opndo {:var "z"}}}
         (-> "((λx.((x y) x)) z)" lex parse)))

  (is (= (-> "((λx.((x y) x)) z)" lex parse)
         (-> "(λx.(x y x)) z" lex restore parse))))
