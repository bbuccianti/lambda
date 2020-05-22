(ns lambda.instaparser
  (:require
   [clojure.string :refer [replace join]]
   [instaparse.core :as insta :refer-macros [defparser]]
   [lambda.state :as state]
   [lambda.lexer :refer [lex]]
   [lambda.normalizer :refer [restore]]
   [lambda.parser :refer [parse]]
   [lambda.stringy :refer [toString]]
   [lambda.semantic :as ui]))

(defparser lambda-parser
  "expr = var | <s>* abstraction | <s>* application
   application = <'('> <s>* expr <s>* expr <s>* <')'> | (<s>* expr)*
   abstraction = lambda (<s>* ident <s>*)+ <'.'> <s>* body
   lambda = <'λ'>
   s = #'\\s+'
   body = expr
   ident = #'[a-z]+'
   var = #'[A-Za-z]+'")

(defn failure? [command]
  (and (not (empty? command)) (insta/failure? (lambda-parser command))))

(defn expectings [failure]
  (let [failures (map (comp str :expecting) (-> failure :reason))
        cleaned (remove #(= (str %) "/^\\s+/") failures)
        connectors (conj (vec (repeat (- (count cleaned) 2) ", "))
                         " o " ", ")]
    (-> (apply str (butlast (interleave cleaned connectors)))
        (replace "/^[a-z]+/" "un parámetro")
        (replace "/^[A-Za-z]+/" "un identificador"))))

(defn get-error [command]
  (let [failure (insta/get-failure (lambda-parser command))]
    (merge failure {:expectings (expectings failure)})))
