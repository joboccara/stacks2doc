(ns stacks2doc.stack
  (:require
   [clojure.string :as string]))

(def split
  (comp #(remove empty? %)
        string/split))

(defn stack-frame-from-source [source]
  (let [[method line-number] (split (first (split source #", ")) #":")]
    {:method method
     :line-number (parse-long line-number)}))

(defn stack-from-source [source]
  (let [stack-frames (split source #"\n")]
    (map stack-frame-from-source
         stack-frames)))
