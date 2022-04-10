(ns mustadio-clj.data.abilities
  (:require [clojure.string :as str]
            [clojure.spec.alpha :as s]
            [mustadio-clj.types :as types]))

(def ^:private file-slots-to-real-slots
  {"Reaction" :ability-slot/react
   "Support" :ability-slot/support
   "Movement" :ability-slot/move})

(defn- find-ability-slot
  "given ability info, determine which ability type it is"
  [info]
  (let [file-type (some #(when (str/starts-with? info (str % ". ")) %)
                        (keys file-slots-to-real-slots))]
    (get file-slots-to-real-slots
         file-type
         :ability-slot/active)))

(defn parse-line
  "Parses a single ability line"
  [line]
  (let [first-colon (str/index-of line ":")
        name (subs line 0 first-colon)
        info (subs line (+ first-colon 2))
        ability-slot (find-ability-slot info)]
    {:ability/name name
     :ability/info info
     :ability/slot ability-slot}))

(defn- ignored-line?
  [line]
  (str/includes? line "Skillset."))

(defn parse-file
  "Parses abilities from infoability.txt"
  [content]
  {:post [(s/valid? (s/coll-of (partial s/valid? ::types/ability)) %)]}
  (let [lines (->> content
                   str/trim
                   str/split-lines
                   (filter #(not (ignored-line? %))))]
    (map parse-line lines)))
