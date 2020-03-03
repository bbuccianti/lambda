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

      (contains? m :var)
      (if (and (contains? m :index) (:index? @state/config))
        (str (:var m) "_" (:index m))
        (:var m)))))
