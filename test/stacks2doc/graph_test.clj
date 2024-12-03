(ns stacks2doc.graph-test
  (:require [clojure.test :refer (deftest testing is)]
            [stacks2doc.graph :refer [all-edges all-nodes make-graph-by-edges make-graph-from-nodes-and-edges]]))

(defn same-elements? [coll1 coll2]
  (= (set coll1) (set coll2)))

(def TEST_GRAPH
  (make-graph-by-edges [{:from "a" :to "b"}
                        {:from "a" :to "c"}
                        {:from "b" :to "c"}]))

(deftest test-all-edges-empty-graph
  (let [empty-graph (make-graph-by-edges [])]
    (testing (is (= []
                    (all-edges empty-graph))))))

(deftest test-all-edges
  (testing (is (same-elements? [{:from "a" :to "b"}
                                {:from "a" :to "c"}
                                {:from "b" :to "c"}]
                               (all-edges TEST_GRAPH)))))

(deftest test-graph-from-nodes-and-egdes
  (let [nodes [{:node "a" :in "AB"}
               {:node "b" :in "AB"}
               {:node "c" :in "C"}]
        edges [{:from "AB:a" :to "AB:b"}
               {:from "AB:a" :to "C:c"}
               {:from "AB:b" :to "C:c"}]
        graph (make-graph-from-nodes-and-edges nodes edges)]
    (testing (and (same-elements? [{:from "AB:a" :to "AB:b"}
                                   {:from "AB:a" :to "C:c"}
                                   {:from "AB:b" :to "C:c"}]
                                  (all-edges graph))
                  (is (same-elements? [{:node "a" :in "AB"}
                                       {:node "b" :in "AB"}
                                       {:node "c" :in "C"}]
                                      (all-nodes graph)))))))