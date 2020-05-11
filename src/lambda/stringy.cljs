(ns lambda.stringy
  (:require
   [lambda.state :as state]))

(defn toString [m]
  (let [full? (:full? @state/config)]
    (cond
      (contains? m :abst)
      (let [abst (:abst m)]
        (str "(λ" (toString (:param abst)) "."
             (toString (:cuerpo abst)) ")"))

      (contains? m :apli)
      (let [apli (:apli m)
            church-number? (= "f" (get-in apli [:opdor :var]))]
        (str (if (or full? church-number?) "(" "")
             (toString (:opdor apli))
             " "
             (toString (:opndo apli))
             (if (or full? church-number?) ")" "")))

      (or (contains? m :var) (contains? m :ident))
      (let [kw (if (contains? m :var) :var :ident)]
        (str (kw m) (when (contains? m :index) (str "_" (:index m))))))))
