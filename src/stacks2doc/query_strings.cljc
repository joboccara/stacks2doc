(ns stacks2doc.query-strings
  (:require [clojure.string]))

(defn add-to-query-strings [query-strings key value]
  (str query-strings
       (if (empty? query-strings) "?" "&")
       key
       "="
       value))

(defn query-strings-to-map [query-strings]
  (apply hash-map (remove empty? (clojure.string/split query-strings #"[?=&]"))))