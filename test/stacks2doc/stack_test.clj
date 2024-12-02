(ns stacks2doc.stack-test
  (:require [clojure.test :refer [deftest testing is]]
            [stacks2doc.stack :refer [classes-graph packages-graph stack-from-source]]
            [stacks2doc.graph :refer [all-edges]]))

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
             (is (and (= (:method second-frame) "$bang")
                      (= (:line-number second-frame) 182)
                      (= (:classname second-frame) "RepointableActorRef")
                      (= (:package second-frame) "akka.actor")
                      (= (:method first-frame) "tell")
                      (= (:line-number first-frame) 131)
                      (= (:classname first-frame) "ActorRef")
                      (= (:package first-frame) "akka.actor"))))))

(deftest test-packages-graph-two-stackframe
  (testing (let [stack-source "sendMessage:163, Dispatch (akka.actor.dungeon)
                               addLogger:205, LoggingBus (akka.event)"
                 ]
             (is (= [{:from "akka.event" :to "akka.actor.dungeon"}] 
                    (all-edges (packages-graph stack-source))))
             ))
  )

(deftest test-packages-graph
  (testing (let [stack-source "sendMessage:163, Dispatch (akka.actor.dungeon)
                               sendMessage$:157, Dispatch (akka.actor.dungeon)
                               sendMessage:410, ActorCell (akka.actor)
                               addLogger:205, LoggingBus (akka.event)
                               $anonfun$startDefaultLoggers$4:129, LoggingBus (akka.event)
                               apply:-1, LoggingBus$$Lambda/0x000000e0011f5b90 (akka.event)"]
             (is (= [{:from "akka.event" :to "akka.actor"}, {:from "akka.actor" :to "akka.actor.dungeon"}]
                    (all-edges (packages-graph stack-source)))))))

(deftest test-packages-graph-2
  (testing (let [stack-source "sendMessage:163, Dispatch (akka.actor.dungeon)
                               sendMessage$:157, Dispatch (akka.actor.dungeon)
                               sendMessage:410, ActorCell (akka.actor)
                               addLogger:205, LoggingBus (akka.event)
                               $anonfun$startDefaultLoggers$4:129, LoggingBus (foobar)
                               apply:-1, LoggingBus$$Lambda/0x000000e0011f5b90 (akka.event)"]
     (is (= (set [{:from "akka.event" :to "foobar"}
             {:from "foobar" :to "akka.event"}
             {:from "akka.event" :to "akka.actor"}
             {:from "akka.actor" :to "akka.actor.dungeon"}])
            (set (all-edges (packages-graph stack-source))))))))

(deftest test-class-graph
  (testing (let [stack-source "sendMessage:410, ActorCell (akka.event)
                               addLogger:205, LoggingBus (akka.event)
                               $anonfun$startDefaultLoggers$4:129, LoggingBus (foobar)
                               apply:-1, LoggingBus$$Lambda/0x000000e0011f5b90 (akka.event)"]
             (is (= (set [{:from "akka.event:LoggingBus$$Lambda/0x000000e0011f5b90" :to "foobar:LoggingBus"}
                          {:from "foobar:LoggingBus" :to "akka.event:LoggingBus"}
                          {:from  "akka.event:LoggingBus" :to "akka.event:ActorCell"}])
                    (set (all-edges (classes-graph stack-source))))))))

#_(deftest test-class-graph_with_duplicates
  (testing (let [stack-source "sendMessage:410, ActorCell (akka.event)
                               addLogger:205, LoggingBus (akka.event)
                               $anonfun$startDefaultLoggers$4:129, LoggingBus (foobar)
                               $anonfun$startDefaultLoggers$4:129, LoggingBus (foobar)
                               apply:-1, LoggingBus$$Lambda/0x000000e0011f5b90 (akka.event)"]
             (is (= (set [{:from "akka.event:LoggingBus$$Lambda/0x000000e0011f5b90" :to "foobar:LoggingBus"}
                          {:from "foobar:LoggingBus" :to "akka.event:LoggingBus"}
                          {:from  "akka.event:LoggingBus" :to "akka.event:ActorCell"}])
                    (set (all-edges (classes-graph stack-source))))))))