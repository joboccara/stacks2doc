(ns stacks2doc.graph)

; Internal structure of a graph:
;
; {"AB:a" {:node "a", :in "AB", :to #{{:target "AB:b" :label myMethod}}},
;  "AB:b" {:node "b", :in "AB", :to #{{:target "AB:a"}, {:target "C:c"}}},
;  "C:c" {:node "c", :in "C", :to #{}}}

(defn make-graph-by-edges
  "edges: [{:from enclosing-graph:name :to enclosing-graph:name}]
   returns: {enclosing-graph:name {:to #{{:target enclosing-graph:name}}}}"
  [edges]
  (reduce (fn [result {:keys [from to]}]
            (update result
                    from 
                    (fn [value, to] {:to (if (nil? value)
                                           #{{:target to}}
                                           (conj (:to value) {:target to}))})
                    to))
          {}
          edges))

 (defn make-graph-from-nodes-and-edges
   "nodes: [{:node name :in enclosing-graph}]
    edges: [{:from enclosing-graph:name :to enclosing-graph:name (:label myLabel)}]
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

 (defn tee [value] (println "tee" value) value)

(defn all-edges [graph]
  (mapcat (fn [[id node]] (map #(hash-map :from id :to (:target %)) (:to node)))
          (seq graph)))

(defn all-nodes [graph]
  (map #(hash-map :node (:node %) :in (:in %))
       (vals graph)))

(defn tee [value] (println "tee" value) value)

(defn merge-graphs [graph-coll]
  (let [merged-nodes (distinct (flatten (map all-nodes graph-coll)))
        merged-edges (distinct (flatten (map all-edges graph-coll)))]
    (tee merged-edges)
    (make-graph-from-nodes-and-edges merged-nodes merged-edges)
    ))


(def edges [{:from "AB:a" :to "AB:b"}
            {:from "AB:b" :to "C:c"}
            {:from "AB:b" :to "D:d"}
            {:from "AB:a" :to "E:e"}
            {:from "AB:a" :to "E:e"}])
(distinct edges)