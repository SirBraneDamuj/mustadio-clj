(ns mustadio-clj.util.string
  (:require [clojure.string :as str]))

(defn empty-or-nil?
  [s]
  (or (nil? s) (= "" s)))

(defn split-at-first
  [s value]
  (let [index (str/index-of s value)]
    (if index
      (let [left (subs s 0 index)
            right (subs s (+ index (count value)))]
        [left right])
      [s])))

(defn string?->int
  "\"23\" -> 23. \"\" -> nil. nil -> nil."
  [s]
  (cond
    (nil? s)
    nil

    (= "" s)
    nil

    :else
    (Integer/parseInt s)))

(defn percent-string?->int
  "\"23%\" -> 23. \"\" -> nil. nil -> nil"
  [s]
  (cond
    (nil? s)
    nil

    (= "" s)
    nil

    :else
    (-> s
        (str/replace "%" "")
        Integer/parseInt)))
