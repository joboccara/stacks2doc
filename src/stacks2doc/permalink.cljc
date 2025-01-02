(ns stacks2doc.permalink
  (:require [clojure.walk]
            [stacks2doc.platform.platform :refer [from-base64 from-json to-base64 to-json]]))

(defn encode [input]
  (to-base64 (to-json input)))

(defn decode [input]
  (clojure.walk/keywordize-keys (from-json (from-base64 input))))