(ns stacks2doc.stack 
  (:require
   [clojure.string :as string]))

(defn split [s re]
  (remove empty? (string/split s re)))

(defn stack-from-source [source]
  (let [stack-frames (split source #"\n")]
    (map #(hash-map :method (first (split % #":")))
         stack-frames)))
