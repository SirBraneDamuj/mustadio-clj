(ns mustadio-clj.data.items-test
  (:require [mustadio-clj.data.items :as items]
            [clojure.test :refer [is testing deftest]]
            [mustadio-clj.fftbg.client :refer [get-dump-file]]
            [mustadio-clj.fftbg.fake :refer [fake-client]]))

(deftest parse-file
  (testing "Parses the entire sample file"
    (let [file-content (get-dump-file fake-client "infoitem.txt")
          parsed (items/parse-file file-content)]
      (is (= (+ 135 112) (count parsed))))))
