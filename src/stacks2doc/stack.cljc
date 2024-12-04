(ns stacks2doc.stack
  (:require
   [clojure.string :as string]
   [stacks2doc.github :refer [github-link]]
   [stacks2doc.graph :refer [make-graph-by-edges
                             make-graph-from-nodes-and-edges merge-graphs]]
   [stacks2doc.utils :refer [tee]]))

(def split-lines
  (comp #(remove empty? %)
        string/split))

(def split-frame #(clojure.string/split % #":|,\s|\s\(|\)"))

(defn marked+? [line]
  (= (first line) \>))

(defn marked-? [line]
  (= (first line) \-))

(defn marked? [line]
  (or (marked+? line) (marked-? line)))

(defn mark- [line]
  (str "-" line))

(defn mark-lines [lines]
  (if (not-any? marked+? lines)
    lines
    (map #(if (marked+? %) % (mark- %)) lines)))

(defn unmark-line [line]
  (if (marked? line) (subs line 1 (count line)) line))

(defn unmark+-lines [lines]
  (map (fn [line] (if (marked+? line) (unmark-line line) line)) lines))

(defn stack-frame-from-source [source-frame]
  (if (marked-? source-frame)
    {:skipped true}
    (let [[method line-number classname package] (split-frame (string/trim (unmark-line source-frame)))]
      {:method method
       :line-number (parse-long line-number)
       :classname classname
       :package package})))

(defn collapse-marked--lines [lines]
  (reduce (fn [result line]
            (if (or (empty? result)
                    (not (and (marked-? line) (marked-? (last result)))))
              (conj result line)
              result))
          []
          lines))

(defn drop-while-from-last [pred coll]
  (reverse (drop-while pred (reverse coll))))

(defn trim-top-bottom-marked--lines [lines]
  (drop-while marked-? (drop-while-from-last marked-? lines)))

(defn stack-from-source [source]
  (let [lines (map string/trim (split-lines source #"\n"))
        stack-frames-source (unmark+-lines (trim-top-bottom-marked--lines (collapse-marked--lines (mark-lines lines))))]
    (reverse (map stack-frame-from-source
                  stack-frames-source))))

(defn mark-skipped [edges]
  (reduce (fn [result edge]
            (if (= (:from edge) :skipped)
              (conj (vec (butlast result)) (assoc (last result) :to (:to edge) :label (:label edge) :link (:link edge) :skipped true))
              (conj result edge)))
          []
          edges))

(defn same-package? [stack-frame1 stack-frame2]
  (let [keys [:package]]
    (= (select-keys stack-frame1 keys)
       (select-keys stack-frame2 keys))))

(defn packages-graph [source]
  (let [stack (stack-from-source source)]
    (make-graph-by-edges
     (mark-skipped (map (fn [[stack-frame next-stack-frame]] {:from (if (:skipped stack-frame) :skipped (:package stack-frame))
                                                                     :to (if (:skipped next-stack-frame) :skipped (:package next-stack-frame))})
                               (remove (fn [[stack-frame next-stack-frame]] (same-package? stack-frame next-stack-frame)) (partition 2 1 stack)))))))

(defn same-class? [stack-frame1 stack-frame2]
  (let [keys [:classname :package]]
    (= (select-keys stack-frame1 keys)
       (select-keys stack-frame2 keys))))

(defn classes-graph-from-one-source [source base-url extension]
  (let [stack (stack-from-source source)
        nodes (map #(hash-map :node (:classname %)
                              :in (:package %))
                   (remove #(get % :skipped) stack))
        edges (mark-skipped
               (map (fn [[stack-frame next-stack-frame]] {:from (if (:skipped stack-frame) :skipped (str (:package stack-frame) ":" (:classname stack-frame)))
                                                          :to (if (:skipped next-stack-frame) :skipped (str (:package next-stack-frame) ":" (:classname next-stack-frame)))
                                                          :label (if (:skipped next-stack-frame) :skipped (:method next-stack-frame))
                                                          :link (if (:skipped next-stack-frame) :skipped (github-link
                                                                                                          (or base-url "")
                                                                                                          (if (clojure.string/starts-with? extension ".") extension (str "." extension))
                                                                                                          (:package next-stack-frame)
                                                                                                          (:classname next-stack-frame)
                                                                                                          (:line-number next-stack-frame)))})
                    (remove (fn [[stack-frame next-stack-frame]] (same-class? stack-frame next-stack-frame))
                            (partition 2 1 stack))))]
    (make-graph-from-nodes-and-edges nodes edges)))

(defn classes-graph-from-sources [sources base-url extension]
  (let [graphs (map #(classes-graph-from-one-source % base-url extension) sources)]
    (merge-graphs graphs)))