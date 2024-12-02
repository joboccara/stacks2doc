(ns stacks2doc.mermaid
  (:require [stacks2doc.graph :as graph]))

(defn to-flowchart [graph]
  (let [graph-edges (graph/all-edges graph)
        mermaid-edges (map #(str (:from %) "-->" (:to %)) graph-edges)]
    (apply str (interpose "\n" (cons "flowchart LR" mermaid-edges)))))
