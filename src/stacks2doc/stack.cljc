(ns stacks2doc.stack
  (:require
   [clojure.string :as string]
   [stacks2doc.graph :refer [make-graph-by-edges make-graph-from-nodes-and-edges merge-graphs]]))

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

(defn tee [value] (println "tee" value) value)

(defn same-class? [stack-frame1 stack-frame2]
  (let [keys [:classname :package]]
    (= (select-keys stack-frame1 keys)
       (select-keys stack-frame2 keys))))

(defn classes-graph-from-one-source [source]
  (let [stack (stack-from-source source)
        nodes (map #(hash-map :node (:classname %)
                              :in (:package %))
                   stack)
        edges (map (fn [[stack-frame next-stack-frame]] {:from (str (:package stack-frame) ":" (:classname stack-frame))
                                                         :to (str (:package next-stack-frame) ":" (:classname next-stack-frame))
                                                         :label (:method next-stack-frame)})
                   (remove (fn [[stack-frame next-stack-frame]] (same-class? stack-frame next-stack-frame))
                           (partition 2 1 stack)))]
    (make-graph-from-nodes-and-edges nodes edges)))

(defn classes-graph-from-sources [sources]
  (let [graphs (map classes-graph-from-one-source sources)]
    (merge-graphs graphs))
)