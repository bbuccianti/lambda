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
  "expr = var | <whitespace>* abstraction | <whitespace>* application
    application = <'('> <whitespace>* expr <whitespace>* expr <whitespace>* <')'> | (<whitespace>* expr)*
    abstraction = <'λ'> (<whitespace>* ident <whitespace>*)+ <'.'> body
    whitespace = #'\\s+'
    body = expr
    ident = #'[a-z]+'
    var = #'[A-Za-z]+'")

(defn failure? []
  (and (not (empty? @state/command))
       (insta/failure? (lambda-parser @state/command))))

(defn expectings [failure]
  (let [failures (map (comp str :expecting) (-> failure :reason))
        cleaned (remove #(= (str %) "/^\\s+/") failures)]
    (-> (str (-> cleaned (interleave (repeat "o")) butlast ((partial join " "))))
        (replace "/^[a-z]+/" "un parámetro")
        (replace "/^[A-Za-z]+/" "un identificador"))))

(defn help-label []
  (let [failure (insta/get-failure (lambda-parser @state/command))]
    (when (failure?)
      [:> ui/label
       {:pointing "above"
        :size "big"
        :color "red"}
       (str "¿Falta " (expectings failure)
            " cerca de la posición " (-> failure :column) "?")])))
