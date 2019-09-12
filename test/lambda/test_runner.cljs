(ns lambda.test-runner
  (:require
   [lambda.core-test]
   [lambda.lexer-test]
   [lambda.parser-test]
   [pjstadig.humane-test-output]
   [cljs.test :refer-macros [run-tests]]))

(defn run-all []
  (run-tests
   'lambda.core-test
   'lambda.lexer-test
   'lambda.parser-test))
