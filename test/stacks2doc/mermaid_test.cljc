(ns stacks2doc.mermaid-test
  (:require [clojure.test :refer [deftest testing is]]
            [stacks2doc.mermaid :refer [to-flowchart]]
            [stacks2doc.graph :refer [make-graph-by-edges]]))

(deftest test-empty-graph
  (testing
   (let [empty-graph (make-graph-by-edges [])]
     (is (= "flowchart LR"
            (to-flowchart empty-graph))))))

(def TEST_GRAPH
  (make-graph-by-edges [{:from "a" :to "b"}
                        {:from "a" :to "c"}
                        {:from "b" :to "c"}]))

(deftest test-graph
  (testing (is (= "flowchart LR\na-->b\na-->c\nb-->c"
                  (to-flowchart TEST_GRAPH)))))