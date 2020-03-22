(ns lambda.normalize
  (:require
   [lambda.lexer :as l]))

(def abre {:tipo :abre-p :string "("})
(def cierra {:tipo :cierra-p :string ")"})
(def lamb {:tipo :lambda :string "λ"})
(def point {:tipo :punto :string "."})

(declare restore-abstr)

(defn restore-lambda [lexed]
  (cond
    (= (first lexed) nil) nil

    (= (:tipo (first lexed)) :lambda)
    (if (= (:tipo (nth lexed 2)) :punto)
      (concat (take 3 lexed) (restore-lambda (drop 3 lexed)))
      (concat (take 2 lexed)
              (restore-lambda (concat [point] [lamb] (drop 2 lexed)))))

    :else (concat [(first lexed)] (restore-lambda (rest lexed)))))

(defn next-close-var [lexed counter stack]
  (cond
    (or (and (= (:tipo (first lexed)) :cierra-p) (= 0 stack))
        (= nil (first lexed)))
    counter

    (= (:tipo (first lexed)) :cierra-p)
    (next-close-var (rest lexed) (inc counter) (dec stack))

    (= (:tipo (first lexed)) :abre-p)
    (next-close-var (rest lexed) (inc counter) (inc stack))

    :else
    (next-close-var (rest lexed) (inc counter) stack)))

(defn group-ident [lexed]
  (cond

    (= (:tipo (first lexed)) :lambda)
    (concat [abre]
                 (restore-abstr lexed)
                 [cierra])

    (and (#{:ident :combi} (:tipo (first lexed))) (or (nil? (second lexed)) (= (second lexed) cierra)))
      lexed
      

    (every? #{:ident :combi} (map #(:tipo %) (take 2 lexed)))
    (if (= (first (nthrest lexed 2)) nil)
      [abre (first lexed) (second lexed) cierra]
      (group-ident (concat [abre (first lexed) (second lexed) cierra]
                           (nthrest lexed 2))))

    (and (#{:ident :combi} (:tipo (first lexed)))
         (= abre (second lexed)))
    (let [corte (next-close-var (nthrest lexed 2) 0 0)
          sobra (nthrest (nthrest lexed 2) (inc corte))]
      (if (nil? (second sobra))
        (concat [abre] [(first lexed)]
                (group-ident (take corte (nthrest lexed 2)))
                [cierra])
        (group-ident
         (concat [abre] [(first lexed)]
                 (group-ident (take corte (nthrest lexed 2)))
                 [cierra] sobra))))

    (= (first lexed) abre)
    (let [corte (next-close-var (rest lexed) 0 0)
          sobra (nthrest (rest lexed) (+ 1 corte))]
      (cond
        (nil? (first sobra))
        (concat (group-ident (take corte (rest lexed))))
        
        (#{:ident :combi} (:tipo (first sobra)))
        (if (nil? (second sobra))
          (concat [abre] (group-ident (take corte (rest lexed)))
                  [(first sobra)] [cierra])
          (group-ident (concat [abre] (group-ident (take corte (rest lexed)))
                               [(first sobra)] [cierra] (rest sobra))))

        (= (first sobra) abre)
        (let [corte2 (next-close-var (rest sobra) 0 0)
              sobra2 (nthrest (rest sobra) (+ 1 corte2))]
          (if (nil? (first sobra2))
            (concat [abre] (group-ident (take corte (rest lexed)))
                    (group-ident (take corte2 (rest sobra)))
                    [cierra])
            (group-ident (concat [abre]
                                 (group-ident (take corte (rest lexed)))
                                 (group-ident (take corte2 (rest sobra)))
                                 [cierra] sobra2))))
        :else lexed))))

(defn restore-abstr [lexed]
  (cond
    (= nil (first lexed))    nil

    (= (first lexed) point)
    (let [corte (next-close-var (rest lexed) 0 0)
          sobra (nthrest (rest lexed) (+ 1 corte))]
     (if (some? (first sobra))
        (concat [(first lexed)]
                (group-ident (take corte (rest lexed)))
                (restore-abstr sobra))
        (concat [(first lexed)]
                (group-ident (take corte (rest lexed))))))

    :else
    (concat [(first lexed)] (restore-abstr (rest lexed)))))


(defn restore-exp [lexed]
  (cond
    (= nil (first lexed)) nil

    (= (first lexed) abre)
    (if (= (second lexed) lamb)
      (concat [(first lexed)] [(second lexed)]
              (restore-exp (nthrest lexed 2)))
      (concat [(first lexed)] (restore-exp (rest lexed))))

    (= (first lexed) lamb)
    (let [corte (next-close-var lexed 0 0)]
      (concat [abre] [(first lexed)] (restore-exp (take corte (rest lexed)))
              [cierra] (restore-exp (nthrest (rest lexed) corte))))

    :else (concat [(first lexed)] (restore-exp (rest lexed)))))

(defn restore[lexed]
  (group-ident (restore-exp (restore-lambda lexed))))
