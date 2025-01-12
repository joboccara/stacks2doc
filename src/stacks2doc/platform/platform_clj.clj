(ns stacks2doc.platform.platform-clj
  (:require [cheshire.core :as cheshire]))

(import java.util.Base64)

(defn from-base64 [input_base64]
  (String. (.decode (Base64/getDecoder) input_base64)))

(defn to-base64 [input_str]
  (.encodeToString (Base64/getEncoder) (.getBytes input_str)))

(def from-json cheshire/parse-string)

(def to-json cheshire/generate-string)

(def deflate identity)
(def inflate identity)