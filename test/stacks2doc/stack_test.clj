(ns stacks2doc.stack-test
  (:require [clojure.test :refer [deftest testing is]]
            [stacks2doc.stack :refer [stack-from-source]]))

(deftest test-empty-stack
  (testing (is (= [] (stack-from-source "")))))

(deftest test-parse-method-name
  (testing (let [stack (stack-from-source "main:11, ChatApp (com.example.chat)")
                  stack-frame (first stack)]
              (is (= "main" (:method stack-frame))))))
