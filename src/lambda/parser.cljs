(ns lambda.parser)

(defn match [s]
  (case (:tipo s)
    :ident {:var (:string s)}
    {:error s}))

(defn parse [lexed]
  (mapv match lexed))
