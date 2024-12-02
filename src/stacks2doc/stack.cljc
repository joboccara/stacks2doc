(ns stacks2doc.stack
  (:require
   [clojure.string :as string]))

(def split-lines
  (comp #(remove empty? %)
        string/split))

(def split-frame #(clojure.string/split % #":|,\s|\s\(|\)"))

(defn stack-frame-from-source [source-frame]
  (let [[method line-number classname package] (split-frame source-frame)] 
    {:method method
     :line-number (parse-long line-number)
     :classname classname
     :package package}))

(defn stack-from-source [source]
  (let [stack-frames (split-lines source #"\n")]
    (map stack-frame-from-source
         stack-frames)))