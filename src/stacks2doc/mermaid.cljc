(ns stacks2doc.mermaid
  (:require
   [stacks2doc.graph :as graph]))

(defn to-flowchart [graph]
  (let [graph-edges (graph/all-edges graph)
        mermaid-edges (map #(str (:from %) "-->" (:to %)) graph-edges)]
    (apply str (interpose "\n" (cons "flowchart LR" mermaid-edges)))))

(defn to-detailed-flowchart [graph]
  (let [subgraphs
        (map (fn [[group nodes]]
               (let [node-representations (map #(str (:in %) ":" (:node %) "[" (:node %) "]") nodes)]
                 (str "subgraph " group "\n"
                      (apply str (interpose "\n" node-representations)) "\n"
                      "end")))
             (group-by :in (graph/all-nodes graph)))
        arrows
        (map (fn [edge] (str (:from edge) "-->" (:to edge)))
             (graph/all-edges graph))]
    (apply str (interpose "\n" (concat
                                ["flowchart LR"]
                                subgraphs
                                arrows)))))