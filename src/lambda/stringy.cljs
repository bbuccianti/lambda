(ns lambda.stringy)

(defn toString
  ([m]
   (toString m false))
  ([m full?]
   (cond
     (contains? m :abst)
     (let [abst (:abst m)]
       (str "(λ" (toString (:param abst) full?) "."
            (toString (:cuerpo abst) full?) ")"))

     (contains? m :apli)
     (let [apli (:apli m)]
       (str (if full? "(" "")
            (toString (:opdor apli) full?)
            " "
            (toString (:opndo apli) full?)
            (if full? ")" "")))

     (contains? m :var)
     (if (contains? m :index)
       (str (:var m) (if(> (:index m) 1)
                       (str"_" (:index m))))
       (:var m))

     :else m)))
