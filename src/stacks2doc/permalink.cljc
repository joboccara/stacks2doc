(ns stacks2doc.permalink
  (:require
   [clojure.string :as string]
   [clojure.walk]
   ["pako" :as pako]
   [stacks2doc.platform.platform :refer [from-base64 from-json to-base64
                                         to-json]]))

(defn base-64-to-url [input_base64]
  (-> input_base64
      (string/replace "+" "-")
      (string/replace "/" "_")
      (string/replace "=" "~")))

(defn url-to-base64 [input_base64]
  (-> input_base64
      (string/replace "-" "+")
      (string/replace "_" "/")
      (string/replace "~" "=")))

(defn tee [value] (js/console.log "tee" value) value)

(defn encode [input]
  (-> input
      to-json
      pako/gzip
      tee
      to-base64
      base-64-to-url))

(defn to-uint8-array [arr]
  (js/Uint8Array. (clj->js arr)))

(defn string-to-int-array [s]
  (mapv js/parseInt (clojure.string/split s #",")))

(defn ascii-int-array-to-string [s]
  (apply str (map js/String.fromCharCode s)))

(defn decode [input]
  (if (empty? input)
    {:stacks [""]}
    (-> input
        url-to-base64
        from-base64
        string-to-int-array
        to-uint8-array
        pako/ungzip
        ascii-int-array-to-string
        from-json
        clojure.walk/keywordize-keys)))
