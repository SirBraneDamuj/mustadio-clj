(ns mustadio-clj.data.items
  (:require [clojure.string :as str]))

(defn parse-line
  [line]
  (let [first-colon (str/index-of line ":")
        name (subs line 0 first-colon)
        info (subs line (+ first-colon 2))]
    {:item/name name
     :item/info info}))

(defn- ignored-line?
  [line]
  (or
   (str/includes? line "Shuriken.")
   (str/includes? line "Bomb.")))

(defn parse-file
  [content]
  (->> content
       str/trim
       str/split-lines
       (filter #(not (ignored-line? %)))
       (map parse-line)))
