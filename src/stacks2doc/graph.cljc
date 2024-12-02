(ns stacks2doc.graph)

(defn make-graph [edges]
  (reduce (fn [result {:keys [from to]}]
            (update result from (fnil conj #{}) to))
          {}
          edges))

(defn all-edges [graph]
  (mapcat (fn [[from tos]] (map #(hash-map :from from :to %) tos))
          (seq graph)))