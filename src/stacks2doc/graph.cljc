(ns stacks2doc.graph
  (:require
   [stacks2doc.utils :refer [tee]]))

; Internal structure of a graph:
;
; {"AB:a" {:node "a", :in "AB", :to #{{:target "AB:b" :label "myMethod" :github-link "https://github.com/path/to/source"}}},
;  "AB:b" {:node "b", :in "AB", :to #{{:target "AB:a"}, {:target "C:c"}}},
;  "C:c" {:node "c", :in "C", :to #{}}}

(defn make-graph-by-edges
  "edges: [{:from enclosing-graph:name :to enclosing-graph:name (:label MyLabel) (:github-link MyLink)}]
   returns: {enclosing-graph:name {:to #{{:target enclosing-graph:name :label label :github-link end-link}}}}"
  [edges]
  (reduce (fn [result {:keys [from to label link skipped]}]
            (update result
                    from
                    (fn [value to label link skipped]
                      (let [edge (cond-> {:target to}
                                   label (assoc :label label)
                                   link (assoc :link link)
                                   skipped (assoc :skipped skipped))]
                        {:to (if (nil? value)
                               #{edge}
                               (conj (:to value) edge))}))
                    to
                    label
                    link
                    skipped))
          {}
          edges))

(defn make-graph-from-nodes-and-edges
  "nodes: [{:node name :in enclosing-graph}]
   edges: [{:from enclosing-graph:name :to enclosing-graph:name (:label MyLabel) (:github-link MyLink)}]
   returns: graph"
  [nodes edges]
  (let [graph-with-keys-and-to (make-graph-by-edges edges)]
    (reduce (fn [result {:keys [node in]}]
              (update result
                      (str in ":" node)
                      #(assoc % :node node :in in)))
            graph-with-keys-and-to
            nodes)))

(defn all-edges [graph]
  (mapcat (fn [[id node]]
            (map #(cond-> (hash-map :from id :to (:target %))
                    (:label %) (assoc :label (:label %))
                    (:link %) (assoc :link (:link %))
                    (:skipped %) (assoc :skipped (:skipped %)))
                 (:to node)))
          (seq graph)))

(defn all-nodes [graph]
  (map #(hash-map :node (:node %) :in (:in %))
       (vals graph)))

(defn merge-graphs [graph-coll]
  (let [merged-nodes (distinct (flatten (map all-nodes graph-coll)))
        merged-edges (distinct (flatten (map all-edges graph-coll)))]
    (make-graph-from-nodes-and-edges merged-nodes merged-edges)))