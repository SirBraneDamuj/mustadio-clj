(ns mustadio-clj.fftbg
  (:require [mustadio-clj.fftbg.client :as client]
            [mustadio-clj.fftbg.fake]
            [mount.core :refer [defstate]])
  (:import mustadio_clj.fftbg.fake.FakeClient))

(declare fftbg-client)
(defstate fftbg-client :start (FakeClient.))

(defn get-dump-file
  [filename]
  (client/get-dump-file fftbg-client filename))


