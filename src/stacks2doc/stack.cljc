(ns stacks2doc.stack 
  (:require
   [clojure.string :as string]))

(def split
  (comp #(remove empty? %)
        string/split))

(defn stack-from-source [source]
  (let [stack-frames (split source #"\n")]
    (map #(hash-map :method (first (split % #":")))
         stack-frames)))
