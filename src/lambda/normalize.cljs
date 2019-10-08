(ns lambda.normalize
  (:require
   [lambda.lexer :as l]))

(def abre {:tipo :abre-p :string "("})
(def cierra {:tipo :cierra-p :string ")"})
(def lamb {:tipo :lambda :string "λ"})
(def point {:tipo :punto :string "."})

(def tes (l/lex "(λx y.x y z) a b"))


(defn restore-lambda [lexed]
  (cond (= (first lexed) nil)
        nil

        (= (:tipo (first lexed)) :lambda)
        (if (= (:tipo (nth lexed 2)) :punto)
          (into [] (concat (take 3 lexed)
                           (restore-lambda (nthrest lexed 3))))
          (into [] (concat (take 2 lexed)
                           (restore-lambda (concat [point]
                                                   [lamb]
                                                   (nthrest lexed 2))))))

        true
        (into [] (concat [(first lexed)] (restore-lambda (rest lexed))))
        ))

(defn next-close-var [lexed counter stack]
  (cond (or (and (= (:tipo (first lexed)) :cierra-p)
                 (= 0 stack))
            (= nil (first lexed)))
        counter

        (= (:tipo (first lexed)) :cierra-p)
        (next-close-var (rest lexed) (inc counter) (dec stack))

        (= (:tipo (first lexed)) :abre-p)
        (next-close-var (rest lexed) (inc counter) (inc stack))

        true
        (next-close-var (rest lexed) (inc counter) stack))
  )

(defn group-ident[lexed]
  (cond (= (first lexed) nil)
        nil

        (= (:tipo (first lexed)) :lambda)
        (into [] (concat [abre]
                         (restore-abstr lexed)
                         [cierra]))
        
        
        
        (= (:tipo (first lexed))
           (:tipo (second lexed))
           :ident)             
        (into [] (concat [abre]
                         [(first lexed)]
                         [(second lexed)]
                         [cierra]
                         ))

        (and (= (:tipo (first lexed)) :ident)
             (= (:tipo (second lexed)) :abre-p))
        (let [corte (next-close-var (nthrest lexed 2) 0 0)]
          (into [] (concat [abre]
                           [(first lexed)]
                           (group-ident (take corte (nthrest lexed 2)))
                           [cierra]
                           )))

        (= (:tipo (first lexed)) :abre-p)
        (let [corte (next-close-var (rest lexed) 0 0)]
          (into [] (concat [abre]                           
                           (group-ident (take corte (rest lexed)))
                           (let [sobra (nthrest (rest lexed) (+ 1 corte))]
                             (if (= (:tipo (first sobra))
                                    :ident)
                               [(first sobra)]
                               (group-ident sobra)))
                           [cierra]
                           ))) 
    ))

(defn restore-abstr [lexed]
  (cond (= nil (first lexed))
        nil
 
        (= (:tipo (first lexed)) :punto)
        (cond (and (= (:tipo (second lexed))
                      :ident)
                   (= (:tipo (nth lexed 2))
                      :cierra-p))
              (into [] (concat [(first lexed)] (restore-abstr (nthrest lexed 2))))

              (= (:tipo (second lexed)) :lambda)              
              (into [] (concat [(first lexed)] (restore-abstr (rest lexed))))

              true
              (let [corte (next-close-var (rest lexed) 0 0)]
                (into [] (concat [(first lexed)]
                                 (group-ident (take corte (rest lexed)))
                                 (restore-abstr (nthrest (rest lexed) corte))))))
        true
        (into [] (concat [(first lexed)] (restore-abstr (rest lexed))))
        
   ))

;;(not (= (:tipo (last lexed)) :cierra-p)) (restore-abstr (into [] (concat [abre] lexed [cierra])))

(defn restore-exp [lexed]
  (cond (= nil (first lexed))
        nil

        (= (:tipo (first lexed)) :abre-p)
        (if (= (:tipo (second lexed)) :lambda)
          (into [] (concat [(first lexed)]
                           [(second lexed)]
                           (restore-exp (nthrest lexed 3))))
          (into [] (concat [(first lexed)] (restore-exp (rest lexed)))))        

        (= (:tipo (first lexed)) :lambda)
        (let [corte (next-close-var lexed 0 0)]
          (into [] (concat [abre]
                           [(first lexed)]
                           (restore-exp (take corte (rest lexed)))
                           [cierra]
                           (restore-exp (nthrest (rest lexed) corte)))))
        
        true
        (into [] (concat [(first lexed)] (restore-exp (rest lexed))))
         ))

(defn restore[lexed]
  (restore-abstr (restore-lambda lexed)))

;; (use 'figwheel-sidecar.repl-api)
;; (start-figwheel!)
;; (cljs-repl)
