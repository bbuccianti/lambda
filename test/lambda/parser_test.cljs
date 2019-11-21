(ns lambda.parser-test
  (:require
   [cljs.test :refer-macros [deftest is testing]]
   [lambda.lexer :as l]
   [lambda.parser :as p]))

(deftest variables
  (is (= {:var "x"}
         (p/parse (l/lex "x")))))

(deftest aplicacion
  (is (= {:apli {:opdor {:var "x"}
                 :opndo {:var "x"}}}
         (p/parse (l/lex "(x x)"))))
  (is (= {:apli
          {:opdor {:abst
                   {:param {:var "y"}
                    :cuerpo {:apli {:opdor {:var "y"}
                                    :opndo {:var "x"}}}}}
           :opndo {:var "a"}}}
         (p/parse (l/lex "((λy.(y x)) a)")))))

(deftest abstraccion
  (is (= {:abst {:param {:var "x"}
                 :cuerpo {:apli {:opdor {:var "x"}
                                 :opndo {:var "x"}}}}}
         (p/parse (l/lex "(λx.(x x))")))))

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
         (p/parse (l/lex "(((λx.(x y)) (λy.(y y))) z)"))))
  (is (= {:apli
          {:opdor {:apli
                   {:opdor
                    {:abst {:param {:var "x"}
                            :cuerpo {:abst {:param {:var "y"}
                                            :cuerpo {:apli {:opdor {:var "y"}
                                                            :opndo {:var "x"}}}}}}}
                    :opndo {:var "a"}}}
           :opndo {:var "b"}}}
         (p/parse (l/lex "(((λx.(λy.(y x))) a) b)"))))
  (is (= {:apli
          {:opdor {:var "x"}
           :opndo {:abst {:param {:var "x"}
                          :cuerpo {:apli {:opdor {:var "y"}
                                          :opndo {:var "y"}}}}}}}
         (p/parse (l/lex "(x (λx.(y y)))"))))
  (is (= {:apli
          {:opdor {:abst {:param {:var "x"}
                          :cuerpo {:apli {:opdor {:apli
                                                  {:opdor {:var "y"}
                                                   :opndo {:var "y"}}}
                                          :opndo {:var "x"}}}}}
           :opndo {:var "z"}}}
         (p/parse (l/lex "((λx.((y y) x)) z)")))))
