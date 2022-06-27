(ns mustadio-clj.loader
  (:require [mustadio-clj.fftbg :as fftbg]))

(defn load-abilities
  []
  (let [dump (fftbg/get-dump-file "infoability.txt")]
    (println dump)))
