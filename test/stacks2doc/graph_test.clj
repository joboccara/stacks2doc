(ns stacks2doc.graph-test
  (:require [clojure.test :refer (deftest testing is)]
            [stacks2doc.graph :refer (make-graph-by-edges make-graph-by-nodes all-edges)]))

(def TEST_GRAPH
  (make-graph-by-edges [{:from "a" :to "b"}
                        {:from "a" :to "c"}
                        {:from "b" :to "c"}]))

(deftest test-all-edges-empty-graph
  (let [empty-graph (make-graph-by-edges [])]
    (testing (is (= []
                    (all-edges empty-graph))))))

(deftest test-all-edges
  (testing (is (= [{:from "a" :to "b"}
                   {:from "a" :to "c"}
                   {:from "b" :to "c"}]
                  (all-edges TEST_GRAPH)))))

(def TEST_DETAILED_ACYCLIC_GRAPH
  (make-graph-by-nodes [{:node "a" :in "AB" :to ["AB.b", "C.c"]}
                        {:node "b" :in "AB" :to ["C.c"]}
                        {:node "c" :in "C" :to []}]))

(deftest test-all-detailed-edges
  (testing (is (= [{:from "AB.a" :to "AB.b"}
                   {:from "AB.a" :to "C.c"}
                   {:from "AB.b" :to "C.c"}]
                  (all-edges TEST_DETAILED_ACYCLIC_GRAPH)))))
