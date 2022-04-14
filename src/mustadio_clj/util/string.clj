(ns mustadio-clj.util.string
  (:require [clojure.string :as str]))

(defn split-at-first
  [s value]
  (let [index (str/index-of s value)]
    (if index
      (let [left (subs s 0 index)
            right (subs s (+ index (count value)))]
        [left right])
      [s])))
