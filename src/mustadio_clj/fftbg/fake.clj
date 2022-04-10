(ns mustadio-clj.fftbg.fake
  (:require [mustadio-clj.fftbg.client :refer [Client]]
            [clojure.java.io :as io]))

(defrecord FakeClient []
  Client
  (get-dump-file
    [_ file-name]
    (slurp (io/file "resources/fftbg_fake" file-name))))

(def fake-client (FakeClient.))
