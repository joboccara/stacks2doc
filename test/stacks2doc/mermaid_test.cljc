(ns stacks2doc.mermaid-test
  (:require
   [clojure.test :refer [deftest is testing]]
   [stacks2doc.graph :refer [make-graph-by-edges
                             make-graph-from-nodes-and-edges]]
   [stacks2doc.mermaid :refer [to-flowchart]]))

(deftest test-empty-graph
  (testing
   (let [empty-graph (make-graph-by-edges [])]
     (is (= "flowchart TD"
            (to-flowchart empty-graph :detailed false))))))

(def TEST_SIMPLE_GRAPH
  (make-graph-by-edges [{:from "a" :to "b"}
                        {:from "a" :to "c"}
                        {:from "b" :to "c"}]))

(deftest test-simple-graph
  (testing (is (= "flowchart TD\na --> c\na --> b\nb --> c"
                  (to-flowchart TEST_SIMPLE_GRAPH :detailed false)))))

(def TEST_ACYCLIC_GRAPH
  (let [nodes [{:node "a" :in "AB"}
               {:node "b" :in "AB"}
               {:node "c" :in "C"}]
        edges [{:from "AB:a" :to "AB:b"}
               {:from "AB:a" :to "C:c"}
               {:from "AB:b" :to "C:c"}]]
    (make-graph-from-nodes-and-edges nodes edges)))

(deftest test-graph
  (testing (is (= "flowchart TD\nsubgraph AB\nAB:a[a]\nAB:b[b]\nend\nsubgraph C\nC:c[c]\nend\nAB:a --> C:c\nAB:a --> AB:b\nAB:b --> C:c"
                  (to-flowchart TEST_ACYCLIC_GRAPH)))))

(def TEST_SKIP_GRAPH
  (let [nodes [{:node "a" :in "AB"}
               {:node "b" :in "AB"}
               {:node "c" :in "C"}]
        edges [{:from "AB:a" :to "AB:b" :skipped true}
               {:from "AB:a" :to "C:c" :skipped true}
               {:from "AB:b" :to "C:c"}]]
    (make-graph-from-nodes-and-edges nodes edges)))

(deftest test-skip-graph
  (testing (is (= "flowchart TD
subgraph AB
AB:a[a]
AB:b[b]
end
subgraph C
C:c[c]
end
subgraph AB
AB:a --- skip!AB:a!AB:b@{ shape: text, label: \"...\" }
skip!AB:a!AB:b --> AB:b
end
AB:a --- skip!AB:a!C:c@{ shape: text, label: \"...\" }
skip!AB:a!C:c --> C:c
AB:b --> C:c"
                  (to-flowchart TEST_SKIP_GRAPH)))))

(def TEST_SKIP_GRAPH_REDUNDANT_SKIP_EDGE
  (let [nodes [{:node "a" :in "AB"}
               {:node "b" :in "AB"}]
        edges [{:from "AB:a" :to "AB:b" :label "label1" :skipped true}
               {:from "AB:a" :to "AB:b" :label "label2" :skipped true}]]
    (make-graph-from-nodes-and-edges nodes edges)))

(deftest test-skip-graph-two-redundant-skip-edge
  (testing (is (= "flowchart TD
subgraph AB
AB:a[a]
AB:b[b]
end
subgraph AB
AB:a --- skip!AB:a!AB:b@{ shape: text, label: \"...\" }
skip!AB:a!AB:b -->|<a href=\"\" target=\"_blank\">label2</a>| AB:b
end
subgraph AB
skip!AB:a!AB:b -->|<a href=\"\" target=\"_blank\">label1</a>| AB:b
end"
                  (to-flowchart TEST_SKIP_GRAPH_REDUNDANT_SKIP_EDGE)))))