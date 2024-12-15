(ns stacks2doc.mermaid
  (:require
   [clojure.string :as string]
   [stacks2doc.graph :as graph]))

(defn edge-to-skipped? [edge]
  (and (clojure.string/includes? edge " --- ")
       (clojure.string/includes? edge "skip!")
       (clojure.string/includes? edge "@{")))

(defn dedup-edges-to-skipped [edges]
  (:result
   (reduce (fn [acc edge]
            (let [edges-to-skipped (:edges-to-skipped acc)
                  result (:result acc)]
              {:result (if (contains? edges-to-skipped edge)
                         result
                         (conj result edge))
               :edges-to-skipped (if (edge-to-skipped? edge)
                                   (conj edges-to-skipped edge)
                                   edges-to-skipped)}))
          {:result [] :edges-to-skipped #{}}
          (mapcat #(clojure.string/split % #"\n") edges))))

(defn to-flowchart [graph & {:keys [detailed, label] :or {detailed true, label true}}]
  (let [graph-nodes (graph/all-nodes graph)
        graph-edges (graph/all-edges graph)]
    (if detailed
      (let [subgraphs
            (map (fn [[group nodes]]
                   (let [node-representations (map #(str (:in %) ":" (:node %) "[" (:node %) "]") nodes)]
                     (str "subgraph " group "\n"
                          (apply str (interpose "\n" node-representations)) "\n"
                          "end")))
                 (group-by :in graph-nodes))
            edges
            (dedup-edges-to-skipped
              (map (fn [edge] (let [from (:from edge)
                                    to (:to edge)
                                     edge-link (if (contains? edge :link) (edge :link) "")
                                     edge-label (if (and label (contains? edge :label))
                                                  (str "|<a href=\"" edge-link "\" target=\"_blank\">" (:label edge) "</a>| ")
                                                  " ")]
                                 (if (:skipped edge)
                                   (let [skip-id (str "skip!" (:from edge) "!" (:to edge))]
                                     ((let [[from-in to-in] (map #(first (string/split % #":")) [from to])]
                                        (if (= from-in to-in) #(str "subgraph " from-in "\n" % "\n" "end") identity))
                                      (str from " --- " skip-id "@{ shape: text, label: \"...\" }" "\n"
                                           skip-id " -->"edge-label  to)))
                                   (str from " -->" edge-label to))))
                    graph-edges))]
        (apply str (interpose "\n" (concat
                                    ["flowchart TD"]
                                    subgraphs
                                    edges))))
      (let [mermaid-edges (map #(str (:from %) " --> " (:to %)) graph-edges)]
        (apply str (interpose "\n" (cons "flowchart TD" mermaid-edges)))))))
