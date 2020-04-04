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
      (let [apli (:apli m)]
        (str (if full? "(" "")
             (toString (:opdor apli))
             " "
             (toString (:opndo apli))
             (if full? ")" "")))

      (or (contains? m :var) (contains? m :ident))
      (let [kw (if (contains? m :var) :var :ident)]
        (if (and (contains? m :index) (:index? @state/config))
          (str (kw m) "_" (:index m))
          (kw m))))))
