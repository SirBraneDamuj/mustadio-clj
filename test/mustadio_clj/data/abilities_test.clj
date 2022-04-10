(ns mustadio-clj.data.abilities-test
  (:require [clojure.test :refer [deftest testing is]]
            [mustadio-clj.data.abilities :as abilities]
            [mustadio-clj.fftbg.client :refer [get-dump-file]]
            [mustadio-clj.fftbg.fake :refer [fake-client]]))

(deftest parse-line
  (testing "An active ability"
    (let [s "Grand Cross: 4 range, 2 AoE, 5 CT, 35 MP. Effect: Add Petrify, Darkness, Confusion, Silence, Berserk, Frog, Poison, Slow, Sleep (Separate)."
          ability-name "Grand Cross"
          info "4 range, 2 AoE, 5 CT, 35 MP. Effect: Add Petrify, Darkness, Confusion, Silence, Berserk, Frog, Poison, Slow, Sleep (Separate)."
          parsed (abilities/parse-line s)]
      (is (= {:ability/slot :ability-slot/active
              :ability/info info
              :ability/name ability-name}
             parsed))))
  (testing "A react ability"
    (let [s "Counter Flood: Reaction. After being targeted by certain attacks, use Elemental back at the attacker."
          ability-name "Counter Flood"
          info "Reaction. After being targeted by certain attacks, use Elemental back at the attacker."
          parsed (abilities/parse-line s)]
      (is (= {:ability/slot :ability-slot/react
              :ability/info info
              :ability/name ability-name}
             parsed))))

  (testing "A support ability"
    (let [s "Maintenance: Support. Unit's items cannot be broken or stolen."
          ability-name "Maintenance"
          info "Support. Unit's items cannot be broken or stolen."
          parsed (abilities/parse-line s)]
      (is (= {:ability/slot :ability-slot/support
              :ability/info info
              :ability/name ability-name}
             parsed))))
  (testing "A support ability"
    (let [s "Ignore Height: Movement. Jump any height vertically."
          ability-name "Ignore Height"
          info "Movement. Jump any height vertically."
          parsed (abilities/parse-line s)]
      (is (= {:ability/slot :ability-slot/move
              :ability/info info
              :ability/name ability-name}
             parsed)))))

(deftest parse-file
  (let [file-content (get-dump-file fake-client "infoability.txt")
        result (abilities/parse-file file-content)]
    (testing "Loads all abilities"
      (is (= 437 (count result))))
    (testing "Loads all expected abilities of each type"
      (let [{:keys [:ability-slot/active
                    :ability-slot/react
                    :ability-slot/support
                    :ability-slot/move]} (group-by :ability/slot result)]
        (is (= 359 (count active)))
        (is (= 29 (count react)))
        (is (= 29 (count support)))
        (is (= 20 (count move)))))))
