(ns lambda.stringy
  (:require
   [clojure.string :refer [replace]]
   [lambda.state :as state]))

(defn toString
  ([m]
   (-> (toString m false false)
       (replace #"λ " "λ")
       (replace #"\. " ".")))
  ([m apli-operand inside-abst]
   (let [full? (:full? @state/config)]
     (cond
       (contains? m :abst)
       (let [abst (:abst m)]
         (if inside-abst
           (str (toString (:param abst) apli-operand true)
                (toString (:cuerpo abst) apli-operand true))
           (str "(λ" (toString (:param abst) apli-operand true)
                (toString (:cuerpo abst) apli-operand true) ")")))

       (contains? m :apli)
       (let [apli (:apli m)
             needed? (get-in apli [:opndo :apli] false)]
         (if inside-abst
           (str "." (toString m apli-operand false))
           (str (if (or full? apli-operand inside-abst) "(" "")
                (toString (:opdor apli) apli-operand inside-abst)
                " "
                (toString (:opndo apli) needed? inside-abst)
                (if (or full? apli-operand) ")" ""))))

       (contains? m :var)
       (str (when inside-abst ". ")
            (:var m)
            (when (contains? m :index) (str "_" (:index m))))

       (contains? m :ident)
       (str (when inside-abst " ")
            (:ident m)
            (when (contains? m :index) (str "_" (:index m))))))))
