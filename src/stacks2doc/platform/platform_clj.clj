(ns stacks2doc.platform.platform-clj
  (:require [cheshire.core :as cheshire]))

(import java.util.Base64)

(defn from-base64 [to-decode]
  (String. (.decode (Base64/getDecoder) to-decode)))

(defn to-base64 [to-encode]
  (.encodeToString (Base64/getEncoder) (.getBytes to-encode)))

(def from-json cheshire/parse-string)

(def to-json cheshire/generate-string)