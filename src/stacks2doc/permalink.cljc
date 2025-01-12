(ns stacks2doc.permalink
  (:require
   [clojure.string :as string]
   [clojure.walk]
   [stacks2doc.platform.platform :refer [inflate deflate
                                         from-base64 to-base64
                                         from-json to-json]]))

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

(defn encode [input]
  (-> input
      to-json
      deflate
      to-base64
      base-64-to-url))

(defn decode [input]
  (if (empty? input)
    {:stacks [""]}
    (-> input
        url-to-base64
        from-base64
        inflate
        from-json
        clojure.walk/keywordize-keys)))
