(ns lambda.alphonsiner
  (:require
   [clojure.walk :refer [prewalk postwalk]]))

(defn alphiser[m]
  (let [function (get-in m [:apli :opdor :abst :cuerpo] "default")
        to-change (get-in m [:apli :opdor :abst :param] "default")
        replacement (get-in m [:apli :opndo] "default")]
  (check-convert function to-change replacement)))

(defn- check-convert[function to-change replacement]
  (let  [operator (get-in function [:apli :opdor] "default")
         operand (get-in function [:apli :opndo] "default")
         current (get-in function [:apli :opdor :param] "default")]
    {:abst
     {:param current
      :cuerpo {
               :apli 
                      (if (= current to-change)
                        {:opdor operator
                         :opndo operand}
                        (if (not= operator operand to-change)
                          {:opdor
                           (check-convert operator
                                          to-change
                                          replacement)
                           :opndo
                           (check-convert operand
                                          to-change
                                          replacement)}
                          (do
                            (if (= current to-change)
                              (update replacement :index inc))
                            (if (= operator to-change)
                              {:opdor replacement
                               :opndo  (if (= operand to-change)
                                         replacement
                                         (check-convert operand
                                                        to-change
                                                        replacement))}))))}}}))
;; ver como quiere definir benja lo del alpha, este genera una reduccion, sino hay que aumentar los indices del parametro y opdores
;; hacer el caso de que la funcion sea solo una :var
