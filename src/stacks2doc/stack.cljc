(ns stacks2doc.stack
  (:require
   [clojure.string :as string]
   [stacks2doc.graph :refer [make-graph-by-edges all-edges]]))

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
    (reverse (map stack-frame-from-source
                  stack-frames))))

(defn packages-graph [source]
  (let [stack (stack-from-source source)
        packages (map :package stack)]
    (make-graph-by-edges (remove nil?
                                 (map (fn [[package next-package]] (if (= package next-package) nil {:from package :to next-package}))
                                      (partition 2 1 packages))))))
