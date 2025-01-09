(ns stacks2doc.query-strings
  (:require [clojure.string]))

(defn add-to-query-strings [query-strings & keysvalues]
  (str "?" (subs
            (apply str
                   (flatten [query-strings
                             (map (fn [[key value]] (str "&" key "=" value)) (partition 2 keysvalues))]))
            1)))

(defn query-strings-to-map [query-strings]
  (apply hash-map (remove empty? (clojure.string/split query-strings #"[?=&]"))))

(defn map-to-query-strings [m]
  (if (empty? m)
    ""
    (str "?" (subs
              (apply str (map (fn [[key value]] (str "&" key "=" value)) m))
              1))))