(ns stacks2doc.mermaid
  (:require 
   [stacks2doc.graph :as graph]))

(defn to-flowchart [graph & {:keys [detailed label] :or {detailed true, label true}}]
  (if detailed
    (let [subgraphs
          (map (fn [[group nodes]]
                 (let [node-representations (map #(str (:in %) ":" (:node %) "[" (:node %) "]") nodes)]
                   (str "subgraph " group "\n"
                        (apply str (interpose "\n" node-representations)) "\n"
                        "end")))
               (group-by :in (graph/all-nodes graph)))
          arrows
          (map (fn [edge] (let [edge-link (if (contains? edge :link) (edge :link) "")
                                edge-label (if (and label (contains? edge :label))
                                             (str "|<a href=\"" edge-link "\" target=\"_blank\">" (:label edge) "</a>| ")
                                             "")]
                            (str (:from edge) " --> " edge-label (:to edge))))
               (graph/all-edges graph))]
      (apply str (interpose "\n" (concat
                                  ["flowchart TD"]
                                  subgraphs
                                  arrows))))
    (let [graph-edges (graph/all-edges graph)
          mermaid-edges (map #(str (:from %) " --> " (:to %)) graph-edges)]
      (apply str (interpose "\n" (cons "flowchart TD" mermaid-edges))))))
