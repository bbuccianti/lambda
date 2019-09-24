(ns lambda.reducer)

(defn- replace-param [param cuerpo opndo]
  (let [opvar (get-in cuerpo [:apli :opdor :var])
        opnvar (get-in cuerpo [:apli :opndo :var])]
    (-> cuerpo
        (update-in [:apli :opdor :var]
                   #(if (= (:var param) opvar)
                      (str (:var opndo))
                      opvar))
        (update-in [:apli :opndo :var]
                   #(if (= (:var param) opnvar)
                      (str (:var opndo))
                      opnvar)))))

(defn- transform [m]
  (let [{:keys [opdor opndo]} m]
    (if (find opdor :abst)
      (replace-param (:param (:abst opdor)) (:cuerpo (:abst opdor)) opndo)
      {:apli m})))

(defn reduce [m]
  (cond
    (find m :apli)
    (transform (:apli m))

    (find m :opdor)
    (transform m)

    :else m))
