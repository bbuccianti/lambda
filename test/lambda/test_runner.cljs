(ns lambda.test-runner
  (:require
   [lambda.core-test]
   [lambda.lexer-test]
   [lambda.parser-test]
   [pjstadig.humane-test-output]
   [cljs.test :refer-macros [run-tests]]
   [cljs-test-display.core :refer [init!]]))

(defn run-all
  ([]
   (run-all nil))
  ([flag]
   (run-tests
    (and flag (init! "app"))
    'lambda.core-test
    'lambda.lexer-test
    'lambda.parser-test)))
