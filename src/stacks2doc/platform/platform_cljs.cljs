(ns stacks2doc.platform.platform-cljs
  (:require ["pako" :as pako]))

(def from-base64 js/atob)

(def to-base64 js/btoa)

(defn from-json [json]
  (js->clj (js/JSON.parse json)))

(defn to-json [structure]
  (js/JSON.stringify (clj->js structure)))

(def deflate pako/gzip)

(defn to-uint8-array [arr]
  (js/Uint8Array. (clj->js arr)))

(defn string-to-int-array [s]
  (mapv js/parseInt (clojure.string/split s #",")))

(defn ascii-int-array-to-string [s]
  (apply str (map js/String.fromCharCode s)))

(defn inflate [s]
  (-> s
      string-to-int-array
      to-uint8-array
      pako/ungzip
      ascii-int-array-to-string))
