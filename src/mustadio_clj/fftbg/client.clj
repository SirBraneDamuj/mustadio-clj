(ns mustadio-clj.fftbg.client)

(defprotocol Client
  "A client for the FFTBG dump"
  (get-dump-file [this file-name]))
