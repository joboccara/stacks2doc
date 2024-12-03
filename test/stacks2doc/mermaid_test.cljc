(ns stacks2doc.mermaid-test
  (:require
   [clojure.test :refer [deftest is testing]]
   [stacks2doc.graph :refer [make-graph-by-edges
                             make-graph-from-nodes-and-edges]]
   [stacks2doc.mermaid :refer [to-detailed-flowchart to-flowchart]]))

(deftest test-empty-graph
  (testing
   (let [empty-graph (make-graph-by-edges [])]
     (is (= "flowchart TD"
            (to-flowchart empty-graph))))))

(def TEST_GRAPH
  (make-graph-by-edges [{:from "a" :to "b"}
                        {:from "a" :to "c"}
                        {:from "b" :to "c"}]))

(deftest test-graph
  (testing (is (= "flowchart TD\na --> c\na --> b\nb --> c"
                  (to-flowchart TEST_GRAPH)))))

(def TEST_DETAILED_ACYCLIC_GRAPH
  (let [nodes [{:node "a" :in "AB"}
               {:node "b" :in "AB"}
               {:node "c" :in "C"}]
        edges [{:from "AB:a" :to "AB:b"}
               {:from "AB:a" :to "C:c"}
               {:from "AB:b" :to "C:c"}]]
    (make-graph-from-nodes-and-edges nodes edges)))

(deftest test-detailed-graph
  (testing (is (= "flowchart TD\nsubgraph AB\nAB:a[a]\nAB:b[b]\nend\nsubgraph C\nC:c[c]\nend\nAB:a --> C:c\nAB:a --> AB:b\nAB:b --> C:c"
                  (to-detailed-flowchart TEST_DETAILED_ACYCLIC_GRAPH)))))
