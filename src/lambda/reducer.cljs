(ns lambda.reducer)

(defn- replace-param [param cuerpo opndo]
  (let [op (get-in cuerpo [:apli :opdor])
        opn (get-in cuerpo [:apli :opndo])]
    (-> cuerpo
        (update-in [:apli :opdor]
                   #(if (= (:var param) (:var op)) opndo op))
        (update-in [:apli :opndo]
                   #(if (= (:var param) (:var opn)) opndo opn)))))

(defn- transform [m]
  (let [{:keys [opdor opndo]} m]
    (if (find opdor :abst)
      (replace-param (:param (:abst opdor)) (:cuerpo (:abst opdor)) opndo)
      {:apli m})))

(defn reduct [m]
  (cond
    (find m :apli)
    (transform (:apli m))

    (find m :opdor)
    (transform m)

    :else m))
