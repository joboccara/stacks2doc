(ns stacks2doc.stack-test
  (:require [clojure.test :refer [deftest testing is]]
            [stacks2doc.stack :refer [stack-from-source]]))

(deftest test-empty-stack
  (testing (is (= [] (stack-from-source "")))))

(def TEST_STACK_FRAME "main:11, ChatApp (com.example.chat)")

(def TEST_CALL_STACK "$bang:182, RepointableActorRef (akka.actor)
tell:131, ActorRef (akka.actor)")

(deftest test-parse-method-name
  (testing (let [stack (stack-from-source TEST_STACK_FRAME)
                 stack-frame (first stack)]
             (is (= "main" (:method stack-frame))))))

(deftest test-parse-line-number
  (testing (let [stack (stack-from-source TEST_STACK_FRAME)
                 stack-frame (first stack)]
             (is (= 11 (:line-number stack-frame))))))

(deftest test-parse-class-name
  (testing (let [stack (stack-from-source TEST_STACK_FRAME)
                 stack-frame (first stack)]
             (is (= "ChatApp" (:classname stack-frame))))))

(deftest test-parse-package-name
  (testing (let [stack (stack-from-source TEST_STACK_FRAME)
                 stack-frame (first stack)]
             (is (= "com.example.chat" (:package stack-frame))))))

(deftest test-parse-source
  (testing (let [[first-frame second-frame] (stack-from-source TEST_CALL_STACK)]
             (is (and (= {:method "$bang" :line-number 182 :classname "RepointableActorRef" :package "akka.actor"} first-frame)
                  (= {:method "tell" :line-number 131 :classname "ActorRef" :package "akka.actor"} second-frame)))
             )))