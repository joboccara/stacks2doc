(ns stacks2doc.graph)

(defn make-graph-by-edges [edges]
  (reduce (fn [result {:keys [from to]}]
            (update result
                    from
                    (fnil #(update %1 :to conj %2) {:node from :to #{}})
                    ;; If it's the first time node was encountered, creates a minimal node
                    ;; Otherwise, appends `to` inside the node's `:to`
                    to))
          {}
          edges))

(defn make-graph-by-nodes [nodes]
  (into {} (map
            (fn [node] (let [key (str (:in node) "." (:node node))]
                         [key (update node :to #(into #{} %))]))
            nodes)))

(defn all-edges [graph]
  (mapcat (fn [[id node]] (map #(hash-map :from id :to %) (:to node)))
          (seq graph)))