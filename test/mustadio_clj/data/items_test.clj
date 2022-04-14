(ns mustadio-clj.data.items-test
  (:require [mustadio-clj.data.items :as items]
            [mustadio-clj.types :as types]
            [mustadio-clj.fftbg.client :refer [get-dump-file]]
            [mustadio-clj.fftbg.fake :refer [fake-client]]
            [clojure.spec.alpha :as s]
            [clojure.test :refer [is testing deftest]]))

(deftest parse-line
  (testing "A regular weapon"
    (let [line "Flame Whip: 13 WP, 1 range, 1% evade, Flail. Element: Fire. Effect: Chance to cast Fire 2."
          {:keys [:stat/wp
                  :stat/absorb-wp
                  :stat/heal-wp
                  :stat/range
                  :stat/ev-percent
                  :item/slot]} (items/parse-line line)]
      (is (= 13 wp))
      (is (= nil absorb-wp))
      (is (= nil heal-wp))
      (is (= 1 range))
      (is (= 1 ev-percent))
      (is (= :item-slot/weapon slot))))
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
      (is (= :item-slot/accessory slot)))))

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
            (println body)
            (is (= 36 (count body))))
          (testing "accessories"
            (is (= 32 (count accessory)))))))))
