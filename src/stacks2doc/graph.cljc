(ns stacks2doc.graph)

; Internal structure of a graph:
;
; {"AB:a" {:node "a", :in "AB", :to ["AB:b"]},
;  "AB:b" {:node "b", :in "AB", :to ["AB:a", "C:c"]},
;  "C:c" {:node "c", :in "C", :to []}}

(defn make-graph-by-edges
  "edges: [{:from enclosing-graph:name :to enclosing-graph:name}]
   returns: {enclosing-graph:name {:to #{enclosing-graph:name}}}"
  [edges]
  (reduce (fn [result {:keys [from to]}]
            (update result
                    from 
                    (fn [value, to] {:to (if (nil? value)
                                           #{to}
                                           (conj (:to value) to))})
                    to))
          {}
          edges))

 (defn make-graph-from-nodes-and-edges
   "nodes: [{:node name :in enclosing-graph}]
    edges: [{:from enclosing-graph:name :to enclosing-graph:name}]
    returns: graph"
    [nodes edges]
   (let [graph-with-keys-and-to (make-graph-by-edges edges)]
     (reduce (fn [result {:keys [node in]}]
               (update result
                       (str in ":" node)
                       #(assoc % :node node :in in)))
             graph-with-keys-and-to
             nodes))
   )

(defn all-edges [graph]
  (mapcat (fn [[id node]] (map #(hash-map :from id :to %) (:to node)))
          (seq graph)))

(defn all-nodes [graph]
  (map #(hash-map :node (:node %) :in (:in %))
       (vals graph)))