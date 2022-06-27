(ns mustadio-clj.data.items-test
  (:require [mustadio-clj.data.items :as items]
            [mustadio-clj.types :as types]
            [mustadio-clj.fftbg.client :refer [get-dump-file]]
            [mustadio-clj.fftbg.fake :refer [fake-client]]
            [clojure.spec.alpha :as s]
            [clojure.test :refer [is testing deftest]]))
(deftest parse-effect
  (testing "An effect with all stats"
    (let [effect "+2 PA, +3 MA, +4 Speed, +5 Move, +6 Jump"
          {:keys [:stat/pa
                  :stat/ma
                  :stat/speed
                  :stat/move
                  :stat/jump]} (items/parse-effect effect)]
      (is (= 2 pa))
      (is (= 3 ma))
      (is (= 4 speed))
      (is (= 5 move))
      (is (= 6 jump))))
  (testing "An effect with status immunities"
    (let [effect "Immune Silence, Frog, Innocent"
          {:keys [:stat/status-immunities]} (items/parse-effect effect)]
      (is (= #{"Silence" "Frog" "Innocent"} (set status-immunities)))))
  (testing "An effect with initial statuses"
    (let [effect "Initial Silence, Frog, Innocent"
          {:keys [:stat/initial-statuses]} (items/parse-effect effect)]
      (is (= #{"Silence" "Frog" "Innocent"} (set initial-statuses)))))
  (testing "An effect with permanent statuses"
    (let [effect "Permanent Silence, Frog, Innocent"
          {:keys [:stat/perm-statuses]} (items/parse-effect effect)]
      (is (= #{"Silence" "Frog" "Innocent"} (set perm-statuses)))))
  (testing "An effect with status cancels"
    (let [effect "Chance to Cancel Silence, Frog, Innocent"
          {:keys [:stat/chance-to-cancel]} (items/parse-effect effect)]
      (is (= #{"Silence" "Frog" "Innocent"} (set chance-to-cancel)))))
  (testing "An effect with status adds"
    (let [effect "Chance to Add Silence, Frog, Innocent"
          {:keys [:stat/chance-to-add]} (items/parse-effect effect)]
      (is (= #{"Silence" "Frog" "Innocent"} (set chance-to-add)))))
  (testing "An effect with status adds (lowercase)"
    (let [effect "Chance to add Silence, Frog, Innocent"
          {:keys [:stat/chance-to-add]} (items/parse-effect effect)]
      (is (= #{"Silence" "Frog" "Innocent"} (set chance-to-add)))))
  (testing "An effect with absorbed element"
    (let [effect "Absorb Fire, Ice"
          {:keys [:stat/absorbed-elements]} (items/parse-effect effect)]
      (is (= #{"Fire" "Ice"} (set absorbed-elements)))))
  (testing "An effect with strengthened element"
    (let [effect "Strengthens Fire, Ice"
          {:keys [:stat/strengthened-elements]} (items/parse-effect effect)]
      (is (= #{"Fire" "Ice"} (set strengthened-elements)))))
  (testing "An effect with stats and something else"
    (let [effect "Foo Bar; +3 Speed, +1 PA; Xyz Abc"
          {:keys [:stat/speed
                  :stat/pa]} (items/parse-effect effect)]
      (is (= 3 speed))
      (is (= 1 pa)))))

(deftest parse-line
  (testing "A regular weapon"
    (let [line "Flame Whip: 13 WP, 1 range, 1% evade, Flail. Element: Fire. Effect: Chance to cast Fire 2."
          {:keys [:stat/wp
                  :stat/absorb-wp
                  :stat/heal-wp
                  :stat/range
                  :stat/ev-percent
                  :stat/element
                  :stat/chance-to-cast
                  :item/slot
                  :item/effect]} (items/parse-line line)]
      (is (= 13 wp))
      (is (= nil absorb-wp))
      (is (= nil heal-wp))
      (is (= 1 range))
      (is (= 1 ev-percent))
      (is (= :item-slot/weapon slot))
      (is (= "Fire" element))
      (is (= #{"Fire 2"} (set chance-to-cast)))
      (is (= "Effect: Chance to cast Fire 2" effect))))
  (testing "A healing weapon"
    (let [line "Healing Staff: 4 WP (heal), 1 range, 17% evade, Staff."
          {:keys [:stat/wp
                  :stat/absorb-wp
                  :stat/heal-wp
                  :stat/range
                  :stat/ev-percent
                  :item/slot]} (items/parse-line line)]
      (is (= nil wp))
      (is (= nil absorb-wp))
      (is (= 4 heal-wp))
      (is (= 1 range))
      (is (= 17 ev-percent))
      (is (= :item-slot/weapon slot))))
  (testing "An absorb weapon"
    (let [line "Bloody Strings: 12 WP (absorb), 3 range, 12% evade, Harp. Effect: Immune Silence."
          {:keys [:stat/wp
                  :stat/absorb-wp
                  :stat/heal-wp
                  :stat/range
                  :stat/ev-percent
                  :item/slot]} (items/parse-line line)]
      (is (= nil wp))
      (is (= 12 absorb-wp))
      (is (= nil heal-wp))
      (is (= 3 range))
      (is (= 12 ev-percent))
      (is (= :item-slot/weapon slot))))
  (testing "A shield"
    (let [line "Kaiser Plate: 43% phys evade, 25% magic evade, Shield. Effect: Strengthen Fire, Lightning, Ice."
          {:keys [:stat/phys-ev-percent
                  :stat/magic-ev-percent
                  :item/slot]} (items/parse-line line)]
      (is (= 43 phys-ev-percent))
      (is (= 25 magic-ev-percent))
      (is (= :item-slot/shield slot))))
  (testing "A helmet"
    (let [line "Triangle Hat: +46 HP, +12 MP, Hat. Effect: +1 MA, +1 Jump."
          {:keys [:stat/hp
                  :stat/mp
                  :item/slot]} (items/parse-line line)]
      (is (= 46 hp))
      (is (= 12 mp))
      (is (= :item-slot/head slot))))
  (testing "A body"
    (let [line "Rubber Costume: +115 HP, +30 MP, Clothes. Effect: Cancel Lightning; Immune Don't Act."
          {:keys [:stat/hp
                  :stat/mp
                  :item/slot]
           :as parsed} (items/parse-line line)]
      (is (= 115 hp))
      (is (= 30 mp))
      (is (= :item-slot/body slot))
      (is (s/valid? ::types/item parsed))))
  (testing "A shield-like accessory"
    (let [line "Dracula Mantle: 15% phys evade, 30% magic evade, Accessory."
          {:keys [:stat/phys-ev-percent
                  :stat/magic-ev-percent
                  :item/slot]} (items/parse-line line)]
      (is (= 15 phys-ev-percent))
      (is (= 30 magic-ev-percent))
      (is (= :item-slot/accessory slot))))
  (testing "A plain accessory"
    (let [line "Chantage: Accessory. Effect: Permanent Regen; Initial Reraise."
          {:keys [:stat/phys-ev-percent
                  :stat/magic-ev-percent
                  :item/slot]} (items/parse-line line)]
      (is (= nil phys-ev-percent))
      (is (= nil magic-ev-percent))
      (is (= :item-slot/accessory slot))))
  (testing "A weapon without any misc"
    (let [line "Main Gauche: 7 WP, 1 range, 40% evade, Knife."
          {:keys [:stat/wp
                  :stat/range
                  :stat/ev-percent
                  :item/name
                  :item/slot]} (items/parse-line line)]
      (is (= :item-slot/weapon slot))
      (is (= 7 wp))
      (is (= 40 ev-percent))
      (is (= 1 range))
      (is (= "Main Gauche" name)))))
(deftest parse-file
  (testing "Parses the entire sample file"
    (let [file-content (get-dump-file fake-client "infoitem.txt")
          parsed (items/parse-file file-content)]
      (is (= 233 (count parsed)))
      (testing "Parses all items correctly"
        (let [grouped (group-by :item/slot parsed)
              {:keys [:item-slot/weapon
                      :item-slot/shield
                      :item-slot/head
                      :item-slot/body
                      :item-slot/accessory]} grouped]
          (testing "weapons"
            (is (= 121 (count weapon))))
          (testing "shields"
            (is (= 16 (count shield))))
          (testing "helmets"
            (is (= 28 (count head))))
          (testing "bodies"
            (is (= 36 (count body))))
          (testing "accessories"
            (is (= 32 (count accessory)))))))))
