
(ns lambda.core-test
  (:require
   [cljs.test :refer-macros [deftest is testing]]
   [lambda.core :as rl]))

(deftest vars
;  (is (= "a" (rl/reducir "a")))
  )

(deftest abstracciones-de-cuerpo-variable
; (is (= "y" (rl/reducir "(λx.x) y")))
  )

;; TODO
;; pasar variable a la identidad => la misma variable
;; pasar abstraccion a la identidad => la misma abstraccion
;; pasar aplicacion a la identidad => la misma aplicacion

;; pasar variable a constante => la constante
;; pasar abstraccion a la constante => la constante
;; pasar aplicacion a la constante => la constante
