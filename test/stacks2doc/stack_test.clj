(ns stacks2doc.stack-test
  (:require [clojure.test :refer [deftest testing is]]
            [stacks2doc.stack :refer [stack-from-source]]))

(deftest test-empty-stack
  (testing (is (= [] (stack-from-source "")))))

(def TEST_STACK_FRAME "main:11, ChatApp (com.example.chat)")

(deftest test-parse-method-name
  (testing (let [stack (stack-from-source TEST_STACK_FRAME)
                 stack-frame (first stack)]
             (is (= "main" (:method stack-frame))))))

(deftest test-parse-line-number
  (testing (let [stack (stack-from-source TEST_STACK_FRAME)
                 stack-frame (first stack)]
             (is (= 11 (:line-number stack-frame))))))
