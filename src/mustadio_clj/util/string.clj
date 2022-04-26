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

(defn string?->int
  "\"23\" -> 23. nil -> nil."
  [s]
  (when s (Integer/parseInt s)))

(defn percent-string?->int
  "\"23%\" -> 23. nil -> nil"
  [s]
  (when s
    (-> s
        (str/replace "%" "")
        string?->int)))
