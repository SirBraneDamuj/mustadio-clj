(ns mustadio-clj.types
  (:require [clojure.spec.alpha :as s]
            [clojure.set :as set]))

;; Abilities

(def ability-slots #{:ability-slot/active
                     :ability-slot/react
                     :ability-slot/support
                     :ability-slot/move})

(s/def :ability/name string?)
(s/def :ability/info string?)
(s/def :ability/slot ability-slots)

(s/def ::ability (s/keys :req [:ability/slot
                               :ability/name
                               :ability/info]))

;; Stats

(s/def :stat/hp nat-int?)
(s/def :stat/mp nat-int?)
(s/def :stat/move nat-int?)
(s/def :stat/jump nat-int?)
(s/def :stat/speed nat-int?)
(s/def :stat/pa nat-int?)
(s/def :stat/ma nat-int?)
(s/def :stat/ev-percent nat-int?)
(s/def :stat/wp nat-int?)
(s/def :stat/heal-wp nat-int?)
(s/def :stat/absorb-wp nat-int?)
(s/def :stat/range nat-int?)
(s/def :stat/ev-percent nat-int?)
(s/def :stat/phys-ev-percent nat-int?)
(s/def :stat/magic-ev-percent nat-int?)
(s/def :stat/element string?)

;; Jobs

(def genders #{:gender/male
               :gender/female
               :gender/monster})

(s/def :job/name string?)
(s/def :job/gender genders)
(s/def :job/raw string?)
(s/def :job/innates (s/coll-of string?))

(s/def :job/stats (s/keys :req [:stat/hp
                                :stat/mp
                                :stat/move
                                :stat/jump
                                :stat/speed
                                :stat/pa
                                :stat/ma
                                :stat/ev-percent]))

(s/def ::job (s/keys :req [:job/name
                           :job/gender
                           :job/stats
                           :job/innates
                           :job/raw]))

;; Items

(def item-slots #{:item-slot/weapon
                  :item-slot/shield
                  :item-slot/head
                  :item-slot/body
                  :item-slot/accessory})

(def weapon-types #{"Knife" "Ninja Blade" "Sword" "Knight Sword" "Katana"
                    "Axe" "Rod" "Staff" "Flail" "Gun"
                    "Crossbow" "Bow" "Harp" "Book" "Spear"
                    "Pole" "Bag" "Fabric"})
(def shield-types #{"Shield"})
(def helmet-types #{"Helmet" "Hat"})
(def body-types #{"Armor" "Clothes" "Clothing" "Robe"})
(def accessory-types #{"Accessory"})
(def item-types (set/union weapon-types shield-types helmet-types body-types accessory-types))

(defn item-type->item-slot
  [item-type]
  (cond
    (weapon-types item-type) :item-slot/weapon
    (shield-types item-type) :item-slot/shield
    (helmet-types item-type) :item-slot/head
    (body-types item-type) :item-slot/body
    (accessory-types item-type) :item-slot/accessory))

(s/def :item/name string?)
(s/def :item/type item-types)
(s/def :item/slot item-slots)
(s/def :item/info string?)
(s/def :item/effect string?)
(s/def :item/initial-statuses (s/coll-of string?))
(s/def :item/perm-statuses (s/coll-of string?))

(s/def :item/base
  (s/keys :req [:item/name
                :item/type
                :item/slot
                :item/info]
          :opt [:stat/element
                :stat/speed
                :stat/move
                :stat/jump
                :stat/pa
                :stat/ma]))

(s/def :item/weapon
  (s/keys :req [(or :stat/wp :stat/absorb-wp :stat/heal-wp)
                :stat/range
                :stat/ev-percent]))

(s/def :item/shield
  (s/keys :req [:stat/phys-ev-percent
                :stat/magic-ev-percent]))

(s/def :item/helmet
  (s/keys :req [:stat/hp
                :stat/mp]))

(s/def :item/body
  (s/keys :req [:stat/hp
                :stat/mp]))

(s/def :item/accessory
  (s/keys :req []))

(defmulti item-slot #(-> % :item/type item-type->item-slot))
(defmethod item-slot :item-slot/weapon [_]
  (s/merge :item/base :item/weapon))
(defmethod item-slot :item-slot/shield [_]
  (s/merge :item/base :item/shield))
(defmethod item-slot :item-slot/helmet [_]
  (s/merge :item/base :item/helmet))
(defmethod item-slot :item-slot/body [_]
  (s/merge :item/base :item/body))
(defmethod item-slot :item-slot/accessory [_]
  (s/merge :item/base :item/accessory))

(s/def ::item (s/multi-spec item-slot :item/slot))

;; Units
