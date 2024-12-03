(ns stacks2doc.stack
  (:require
   [clojure.string :as string]
   [stacks2doc.graph :refer [make-graph-by-edges make-graph-from-nodes-and-edges merge-graphs]]))

(def split-lines
  (comp #(remove empty? %)
        string/split))

(def split-frame #(clojure.string/split % #":|,\s|\s\(|\)"))

(defn marked+? [line]
  (= (last line) \<))

(defn marked-? [line]
  (= (last line) \-))

(defn marked? [line]
  (or (marked+? line) (marked-? line)))

(defn mark- [line]
  (str line "-"))

(defn mark-lines [lines]
  (if (not-any? marked+? lines)
      lines
      (map #(if (marked+? %) % (mark- %)) lines)))

(defn unmark-line [line]
  (if (marked? line) (subs line 0 (dec (count line))) line))

(defn unmark+-lines [lines]
  (map (fn [line] (if (marked+? line) (unmark-line line) line)) lines))

(defn stack-frame-from-source [source-frame]
  (if (marked-? source-frame)
    {:skipped true}
    (let [[method line-number classname package] (split-frame (unmark-line source-frame))]
      {:method method
       :line-number (parse-long line-number)
       :classname classname
       :package package})))

(defn collapse-unmarked-lines [lines]
  (reduce (fn [result line]
            (if (or (empty? result)
                    (not (and (marked-? line) (marked-? (last result)))))
              (conj result line)
              result))
          []
          lines))

(defn stack-from-source [source]
  (let [lines (split-lines source #"\n")
        stack-frames-source (unmark+-lines (collapse-unmarked-lines (mark-lines lines)))]
    (reverse (map stack-frame-from-source
                  stack-frames-source))))

(defn packages-graph [source]
  (let [stack (stack-from-source source)
        packages (map :package stack)]
    (make-graph-by-edges (remove nil?
                                 (map (fn [[package next-package]] (if (= package next-package) nil {:from package :to next-package}))
                                      (partition 2 1 packages))))))

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