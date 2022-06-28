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

(def stats-labels
  {"WP" :stat/wp
   "WP (absorb)" :stat/absorb-wp
   "WP (heal)" :stat/heal-wp
   "range" :stat/range
   "evade" :stat/ev-percent
   "phys evade" :stat/phys-ev-percent
   "magic evade" :stat/magic-ev-percent
   "HP" :stat/hp
   "MP" :stat/mp
   "Speed" :stat/speed
   "Move" :stat/move
   "Jump" :stat/jump
   "PA" :stat/pa
   "MA" :stat/ma})

(defn- handle-stat-pair
  [[label value]]
  (let [k (get stats-labels label)
        v (cond
            (contains?
             #{:stat/ev-percent :stat/phys-ev-percent :stat/magic-ev-percent}
             k)
            (str-util/percent-string?->int value)

            :else
            (str-util/string?->int value))]
    [k v]))

(defn parse-stats-tokens
  [stats-tokens]
  (->> stats-tokens
       assoc-stats
       (map handle-stat-pair)
       flatten
       (apply hash-map)))

(defn parse-csv-from-prefix
  [prefix s]
  (let [[_ rest] (str/split s (re-pattern prefix))]
    (str/split rest #", ")))

;; todo programmatically iterate through these
(def csv-effect-prefixes
  {"Immune" :stat/status-immunities
   "Initial" :stat/initial-statuses
   "Permanent" :stat/perm-statuses
   "Chance to Add" :stat/chance-to-add
   "Chance to add" :stat/chance-to-add
   "Chance to Cancel" :stat/chance-to-cancel
   "Absorb" :stat/absorbed-elements
   "Strengthens" :stat/strengthened-elements})

(defn parse-effect-token
  [effect-token]
  (cond
    (str/starts-with? effect-token "+")
    (parse-stats-tokens (str/split effect-token #", "))

    (str/starts-with? effect-token "Immune ")
    {:stat/status-immunities (parse-csv-from-prefix
                              "Immune "
                              effect-token)}

    (str/starts-with? effect-token "Initial ")
    {:stat/initial-statuses (parse-csv-from-prefix
                             "Initial "
                             effect-token)}

    (str/starts-with? effect-token "Permanent ")
    {:stat/perm-statuses (parse-csv-from-prefix
                          "Permanent "
                          effect-token)}

    (str/starts-with? effect-token "Chance to Add ")
    {:stat/chance-to-add (parse-csv-from-prefix
                          "Chance to Add "
                          effect-token)}

    (str/starts-with? effect-token "Chance to add ")
    {:stat/chance-to-add (parse-csv-from-prefix
                          "Chance to add "
                          effect-token)}

    (str/starts-with? effect-token "Chance to Cancel ")
    {:stat/chance-to-cancel (parse-csv-from-prefix
                             "Chance to Cancel "
                             effect-token)}

    (str/starts-with? effect-token "Absorb ")
    {:stat/absorbed-elements (parse-csv-from-prefix
                              "Absorb "
                              effect-token)}

    (str/starts-with? effect-token "Strengthens ")
    {:stat/strengthened-elements (parse-csv-from-prefix
                                  "Strengthens "
                                  effect-token)}

    (str/starts-with? effect-token "Chance to cast ")
    {:stat/chance-to-cast (parse-csv-from-prefix
                           "Chance to cast "
                           effect-token)}

    (str/starts-with? effect-token "Chance to Cast ")
    {:stat/chance-to-cast (parse-csv-from-prefix
                           "Chance to Cast "
                           effect-token)}

    :else
    {}))

(defn parse-effect
  [effect]
  (let [tokens (str/split effect #"; ")
        effects (map parse-effect-token tokens)]
    (apply merge effects)))

(defn parse-misc-token
  [misc-token]
  (let [[prefix suffix] (str-util/split-at-first misc-token ": ")]
    (cond
      (= prefix "Element")
      {:stat/element suffix}

      (= prefix "Effect")
      (merge {:item/effect misc-token}
             (parse-effect suffix))

      :else
      {})))

(defn parse-misc
  [misc-tokens]
  (apply merge (map parse-misc-token misc-tokens)))

(defn parse-info
  [info]
  (let [[stats & misc-tokens] (str/split info #"(\. )|(\.)")
        stats-tokens (str/split stats #", ")
        only-stats (drop-last stats-tokens)
        #_#_stats-map (assoc-stats only-stats)
        item-type (last stats-tokens)
        item-slot (types/item-type->item-slot item-type)]
    (merge {:item/type item-type
            :item/slot item-slot}
           (parse-stats-tokens only-stats)
           (parse-misc misc-tokens))))

(defn parse-line
  [line]
  {:post [(s/assert ::types/item %)]}
  (let [[name info] (str-util/split-at-first line ": ")
        parsed-info (parse-info info)]
    (merge {:item/name name
            :item/info info}
           parsed-info)))

;; I consider these to be abilities and do not parse them as equipable items.
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
