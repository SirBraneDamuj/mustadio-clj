(ns mustadio-clj.util.string-test
  (:require [mustadio-clj.util.string :as str-util]
            [clojure.test :refer [deftest testing is]]))

(deftest split-at-first
  (testing "an empty string"
    (let [s ""]
      (testing "and an empty split value"
        (let [value ""
              result (str-util/split-at-first s value)]
          (is (= ["" ""] result))))
      (testing "and a non-empty split value"
        (let [value "nonempty"
              result (str-util/split-at-first s value)]
          (is (= [""] result))))))
  (testing "a nonempty string"
    (let [s "Foo:Bar, Baz"]
      (testing "an empty string split value"
        (let [value ""
              result (str-util/split-at-first s value)]
          (is (= ["" s] result))))
      (testing "a split value that isn't there"
        (let [value "-"
              result (str-util/split-at-first s value)]
          (is (= [s] result))))
      (testing "a single character split value that is there"
        (let [value ":"
              result (str-util/split-at-first s value)]
          (is (= ["Foo" "Bar, Baz"] result))))
      (testing "a multi character split value that is there"
        (let [value ", "
              result (str-util/split-at-first s value)]
          (is (= ["Foo:Bar" "Baz"] result))))
      (testing "a split value at the end of the string"
        (let [value "z"
              result (str-util/split-at-first s value)]
          (is (= ["Foo:Bar, Ba" ""] result))))
      (testing "a split value at the beginning of the string"
        (let [value "F"
              result (str-util/split-at-first s value)]
          (is (= ["" "oo:Bar, Baz"] result))))
      (testing "a split value that appears multiple times in the string"
        (let [value "B"
              result (str-util/split-at-first s value)]
          (is (= ["Foo:" "ar, Baz"] result)))))))

(deftest string?->int
  (testing "nil"
    (let [s nil
          result (str-util/string?->int s)]
      (is (= nil result))))
  (testing "an empty string"
    (let [s ""
          result (str-util/string?->int s)]
      (is (= nil result))))
  (testing "a number string"
    (let [s "45"
          result (str-util/string?->int s)]
      (is (= 45 result))))
  (testing "a number string with unary + operator"
    (let [s "+53"
          result (str-util/string?->int s)]
      (is (= 53 result))))
  (testing "a number string with unary - operator"
    (let [s "-2"
          result (str-util/string?->int s)]
      (is (= -2 result)))))

(deftest percent-string?->int
  (testing "nil"
    (let [s nil
          result (str-util/percent-string?->int s)]
      (is (= nil result))))
  (testing "an empty string"
    (let [s ""
          result (str-util/percent-string?->int s)]
      (is (= nil result))))
  (testing "a percent string"
    (let [s "32%"
          result (str-util/percent-string?->int s)]
      (is (= 32 result))))
  (testing "a percent string with unary + operator"
    (let [s "+22%"
          result (str-util/percent-string?->int s)]
      (is (= 22 result))))
  (testing "a percent string with unary - operator"
    (let [s "-9%"
          result (str-util/percent-string?->int s)]
      (is (= -9 result)))))
