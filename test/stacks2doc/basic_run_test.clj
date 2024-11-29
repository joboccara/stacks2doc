(ns stacks2doc.basic-run-test
  (:require [clojure.test :refer [deftest is testing]]
            [stacks2doc.dummy-calculator :refer [my_sum]]))

(deftest test-that-tests-run
  (testing "If this test passes, it means that the Clojure part of the project is configured correctly"
    (is (= 85 (my_sum 42 43)))))