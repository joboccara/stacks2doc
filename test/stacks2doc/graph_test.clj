(ns stacks2doc.graph-test
  (:require [clojure.test :refer (deftest testing is)]
            [stacks2doc.graph :refer (make-graph all-edges)]))

(def TEST_GRAPH
  (make-graph [{:from "a" :to "b"}
               {:from "a" :to "c"}
               {:from "b" :to "c"}]))

(deftest test-empty-graph
  (let [empty-graph (make-graph [])]
    (testing (is (= []
                    (all-edges empty-graph))))))