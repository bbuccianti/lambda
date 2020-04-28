(ns lambda.reducer
  (:require
   [clojure.walk :refer [postwalk prewalk]]
   [lambda.debruijnator :refer [debruijn cleaner]]))

(def clean? (atom false))

(defn- count-replacements [m]
  (let [{:keys [opdor opndo]} (:apli m)
        param (get-in opdor [:abst :param])
        counter (atom 0)]
    (prewalk
     (fn [target]
       (if (and (= (:var target) (:ident param))
                (= (:index target) (:index param)))
         (do (swap! counter inc) target)
         target))
     (get-in opdor [:abst :cuerpo]))
    @counter))

(defn- transform [m]
  (let [{:keys [opdor opndo]} (:apli m)]
    (if (and (contains? opdor :abst) (not (nil? opndo)))
      (do
        (when (> (count-replacements m) 1)
          (reset! clean? true))
        (postwalk (fn [target]
                    (if (and (= (:var target)
                                (get-in opdor [:abst :param :ident]))
                             (= (:index target)
                                (get-in opdor [:abst :param :index])))
                      opndo
                      target))
                  (get-in opdor [:abst :cuerpo])))
      m)))

(defn- keep-reducing [m]
  (let [flag (atom true)]
    (prewalk (fn [target]
               (if (and @flag (get-in target [:apli :opdor :abst] false))
                 (do (reset! flag false) (transform target))
                 target))
             (if @clean?
               (do (reset! clean? false)
                   (-> m cleaner debruijn))
               m))))

(defn all-reductions [m]
  ;; TODO: fix 20 for managing infinite recursion
  (distinct (take 20 (iterate keep-reducing (debruijn m)))))


