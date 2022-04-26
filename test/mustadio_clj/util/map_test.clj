(ns mustadio-clj.util.map-test
  (:require [clojure.test :refer [deftest testing is]]
            [mustadio-clj.util.map :as map-util]))

(deftest select-values
  (testing "an empty map"
    (let [m {}]
      (testing "no keys"
        (let [ks []
              result (map-util/select-values m ks)]
          (is (= [] result))))
      (testing "some keys"
        (let [ks [:a :b :c]
              result (map-util/select-values m ks)]
          (is (= [nil nil nil] result))))))
  (testing "a map with a single key"
    (let [m {:a :x}]
      (testing "no keys"
        (let [ks []
              result (map-util/select-values m ks)]
          (is (= [] result))))
      (testing "a single key"
        (let [ks [:a]
              result (map-util/select-values m ks)]
          (is (= [:x] result))))
      (testing "multiple keys, one of which is present"
        (let [ks [:a :b :c]
              result (map-util/select-values m ks)]
          (is (= [:x nil nil] result))))
      (testing "multiple keys, none of which are present"
        (let [ks [:d :e :f]
              result (map-util/select-values m ks)]
          (is (= [nil nil nil] result))))))
  (testing "a map with many keys"
    (let [m {:a :x :b :y :c :z}]
      (testing "no keys"
        (let [ks []
              result (map-util/select-values m ks)]
          (is (= [] result))))
      (testing "a single key"
        (let [ks [:a]
              result (map-util/select-values m ks)]
          (is (= [:x] result))))
      (testing "multiple keys, all of which are present"
        (let [ks [:a :b :c]
              result (map-util/select-values m ks)]
          (is (= [:x :y :z] result))))
      (testing "multiple keys, some of which are present"
        (let [ks [:a :b :f]
              result (map-util/select-values m ks)]
          (is (= [:x :y nil] result))))
      (testing "multiple keys, some of which are present and in a different order"
        (let [ks [:f :b :a]
              result (map-util/select-values m ks)]
          (is (= [nil :y :x] result))))
      (testing "multiple keys, none of which are present"
        (let [ks [:d :e :f]
              result (map-util/select-values m ks)]
          (is (= [nil nil nil] result)))))))
