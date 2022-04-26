(ns mustadio-clj.util.map)

(defn select-values
  [map ks]
  (for [k ks] (get map k)))
