(ns lambda.stringy
  (:require
   [lambda.state :as state]))

(defn toString
  ([m] (toString m false))
  ([m flag]
   (let [full? (:full? @state/config)]
     (cond
       (contains? m :abst)
       (let [abst (:abst m)]
         (str "(λ" (toString (:param abst) flag) "."
              (toString (:cuerpo abst) flag) ")"))

       (contains? m :apli)
       (let [apli (:apli m)
             needed? (get-in apli [:opndo :apli] false)]
         (str (if (or full? flag) "(" "")
              (toString (:opdor apli) flag)
              " "
              (toString (:opndo apli) needed?)
              (if (or full? flag) ")" "")))

       (or (contains? m :var) (contains? m :ident))
       (let [kw (if (contains? m :var) :var :ident)]
         (str (kw m) (when (contains? m :index) (str "_" (:index m)))))))))
