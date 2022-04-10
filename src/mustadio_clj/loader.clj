(ns mustadio-clj.loader
  (:require [mustadio-clj.fftbg :as fftbg]))

(defn load-abilities
  []
  (let [dump (fftbg/get-dump-file "infoability.txt")]
    (println dump)))

(comment
  (do
    (require '[mustadio-clj.loader :as loader] :reload-all)
    (require '[mount.core :as mount] :reload-all)
    (require '[mustadio-clj.fftbg :as fftbg] :reload-all)
    (mount/stop)
    (mount/start)
    (let [loaded (loader/load-abilities)]
      (println loaded))))
