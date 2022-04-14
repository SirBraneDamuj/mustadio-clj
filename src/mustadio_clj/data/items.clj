(ns mustadio-clj.data.items
  (:require [mustadio-clj.types :as types]
            [mustadio-clj.util.string :as str-util]
            [clojure.spec.alpha :as s]
            [clojure.string :as str]))

(defn assoc-stats
  [stats-tokens]
  (->> stats-tokens
       (mapcat #(-> % (str-util/split-at-first " ") reverse))
       (apply hash-map)))

(defn select-values [map ks]
  (reduce #(conj %1 (map %2)) [] ks))

(defn- string?->int
  [s]
  (when s (Integer/parseInt s)))

(defn- percent-string?->int
  [s]
  (when s
    (-> s
        (str/replace "%" "")
        string?->int)))

(defmulti parse-stats (fn [slot _] slot))
(defmethod parse-stats :item-slot/weapon
  [_ stats-map]
  (let [[wp
         absorb-wp
         heal-wp
         range
         evade] (select-values stats-map
                               ["WP" "WP (absorb)" "WP (heal)" "range" "evade"])]
    {:stat/wp (string?->int wp)
     :stat/absorb-wp (string?->int absorb-wp)
     :stat/heal-wp (string?->int heal-wp)
     :stat/range (string?->int range)
     :stat/ev-percent (percent-string?->int evade)}))
(defmethod parse-stats :item-slot/shield
  [_ stats-map]
  (let [[phys-ev-percent
         magic-ev-percent] (select-values stats-map
                                          ["phys evade" "magic evade"])]
    {:stat/phys-ev-percent (percent-string?->int phys-ev-percent)
     :stat/magic-ev-percent (percent-string?->int magic-ev-percent)}))
(defmethod parse-stats :item-slot/head
  [_ stats-map]
  (let [[hp mp] (select-values stats-map
                               ["HP" "MP"])]
    {:stat/hp (string?->int hp)
     :stat/mp (string?->int mp)}))
(defmethod parse-stats :item-slot/body
  [_ stats-map]
  (let [[hp mp] (select-values stats-map
                               ["HP" "MP"])]
    {:stat/hp (string?->int hp)
     :stat/mp (string?->int mp)}))
(defmethod parse-stats :item-slot/accessory
  [_ stats-map]
  (let [[phys-ev-percent
         magic-ev-percent] (select-values stats-map
                                          ["phys evade" "magic evade"])]
    {:stat/phys-ev-percent (percent-string?->int phys-ev-percent)
     :stat/magic-ev-percent (percent-string?->int magic-ev-percent)}))

(defn parse-info
  [info]
  (let [[stats effect] (str/split info #"(\. )|(\.)")
        stats-tokens (str/split stats #", ")
        only-stats (drop-last stats-tokens)
        stats-map (assoc-stats only-stats)
        item-type (last stats-tokens)
        item-slot (types/item-type->item-slot item-type)]
    (merge {:item/type item-type
            :item/slot item-slot}
           (parse-stats item-slot stats-map))))


(defn parse-line
  [line]
  {:post [(s/assert ::types/item %)]}
  (let [[name info] (str-util/split-at-first line ": ")
        parsed-info (parse-info info)]
    (merge {:item/name name
            :item/info info}
           parsed-info)))

(defn- ignored-line?
  [line]
  (or
   (str/includes? line "Shuriken.")
   (str/includes? line "Bomb.")
   (str/includes? line "Consumable.")))

(defn parse-file
  [content]
  {:post [(s/assert (s/coll-of (partial s/valid? ::types/item)) %)]}
  (->> content
       str/trim
       str/split-lines
       (filter #(not (ignored-line? %)))
       (map parse-line)))
