(ns stacks2doc.stack-test
  (:require [clojure.test :refer [deftest testing is]]
            [stacks2doc.stack :refer [stack-from-source]]))

(deftest test-empty-stack
  (testing (is (= [] (stack-from-source "")))))