(ns stacks2doc.stack-test
  (:require
   [clojure.test :refer [deftest is testing]]
   [stacks2doc.github :refer [github-link]]
   [stacks2doc.graph :refer [all-edges]]
   [stacks2doc.stack :refer [classes-graph-from-one-source packages-graph
                             stack-frame-from-source stack-from-source]]
   [stacks2doc.utils :refer [tee]]))

(deftest test-empty-stack
  (testing (is (= [] (stack-from-source "")))))

(def TEST_STACK_FRAME "main:11, ChatApp (com.example.chat)")

(def TEST_CALL_STACK "$bang:182, RepointableActorRef (akka.actor)
tell:131, ActorRef (akka.actor)")

(def TEST_BASE_URL "https://github.com/DataDog/logs-backend/tree/prod/domains/event-platform/shared/libs/service/src/main/java")
(def TEST_EXTENSION ".java")

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

(deftest test-unmarked-frames-are-reduced-to-dots
  (let [stack-source "> sendMessage:410, ActorCell (akka.event)
                      addLogger:205, LoggingBus (akka.event)
                      > thirdMethod:53, LoggingBus (foobar)
                      secondMethod:53, LoggingBus (foobar)
                      firstMethod:129, LoggingBus (foobar)
                      > apply:-1, LoggingBus$$Lambda/0x000000e0011f5b90 (akka.event)"
        [frame1 frame2 frame3 frame4 frame5] (stack-from-source stack-source)]
    (testing (is (and
                  (and (= (:package frame1) "akka.event") (= (:classname frame1) "LoggingBus$$Lambda/0x000000e0011f5b90") (= (:method frame1) "apply"))
                  (= frame2 {:skipped true})
                  (and (= (:package frame3) "foobar") (= (:classname frame3) "LoggingBus") (= (:method frame3) "thirdMethod"))
                  (= frame4 {:skipped true})
                  (and (= (:package frame5) "akka.event") (= (:classname frame5) "ActorCell") (= (:method frame5) "sendMessage")))))))

(deftest test-unmarked-frames-at-top-and-botton-are-ignored
  (let [stack-source "sendMessage:410, ActorCell (akka.event)
                      > addLogger:205, LoggingBus (akka.event)
                      thirdMethod:53, LoggingBus (foobar)   
                      > secondMethod:53, LoggingBus (foobar)
                      firstMethod:129, LoggingBus (foobar)
                      apply:-1, LoggingBus$$Lambda/0x000000e0011f5b90 (akka.event)"
        [frame1 frame2 frame3] (stack-from-source stack-source)]
    (testing (is (and
                  (and (= (:package frame1) "foobar") (= (:classname frame1) "LoggingBus") (= (:method frame1) "secondMethod"))
                  (= frame2 {:skipped true})
                  (and (= (:package frame3) "akka.event") (= (:classname frame3) "LoggingBus") (= (:method frame3) "addLogger")))))))

(deftest test-packages-graph-two-stackframe
  (testing (let [stack-source "sendMessage:163, Dispatch (akka.actor.dungeon)
                               addLogger:205, LoggingBus (akka.event)"]
             (is (= [{:from "akka.event" :to "akka.actor.dungeon"}]
                    (all-edges (packages-graph stack-source)))))))

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

(deftest test-marked-packages-graph
  (testing (let [stack-source "sendMessage:163, Dispatch (akka.actor.dungeon)
                               > sendMessage$:157, Dispatch (akka.actor.dungeon)
                               > sendMessage:410, ActorCell (akka.other)
                               sendMessage:410, ActorCell (akka.actor)
                               > addLogger:205, LoggingBus (akka.event)
                               $anonfun$startDefaultLoggers$4:129, LoggingBus (akka.event)
                               > apply:-1, LoggingBus$$Lambda/0x000000e0011f5b90 (akka.event)"]
             (is (= [{:from "akka.event" :to "akka.event" :skipped true}
                     {:from "akka.event" :to "akka.other" :skipped true}
                     {:from "akka.other" :to "akka.actor.dungeon"}]
                    (all-edges (packages-graph stack-source)))))))

(defn github-link-from-source [source code_path extension]
  (let [stack (stack-frame-from-source source)
        package (:package stack)
        classname (:classname stack)
        line-number (:line-number stack)]
    (github-link code_path extension package classname line-number)))

(def SOURCE "$anonfun$startDefaultLoggers$4:129, LoggingBus (foobar)")

(deftest test-class-graph
  (testing (let [stack-source "sendMessage:410, ActorCell (akka.event)
                               addLogger:205, LoggingBus (akka.event)
                               $anonfun$startDefaultLoggers$4:129, LoggingBus (foobar)
                               apply:-1, LoggingBus$$Lambda/0x000000e0011f5b90 (akka.event)"]
             (is (= (set [{:from "akka.event:LoggingBus$$Lambda/0x000000e0011f5b90" :to "foobar:LoggingBus" :label "$anonfun$startDefaultLoggers$4" :link "https://github.com/DataDog/logs-backend/tree/prod/domains/event-platform/shared/libs/service/src/main/java/foobar/LoggingBus.java#L129"}
                          {:from "foobar:LoggingBus" :to "akka.event:LoggingBus" :label "addLogger" :link "https://github.com/DataDog/logs-backend/tree/prod/domains/event-platform/shared/libs/service/src/main/java/akka/event/LoggingBus.java#L205"}
                          {:from  "akka.event:LoggingBus" :to "akka.event:ActorCell" :label "sendMessage" :link "https://github.com/DataDog/logs-backend/tree/prod/domains/event-platform/shared/libs/service/src/main/java/akka/event/ActorCell.java#L410"}])
                    (set (all-edges (classes-graph-from-one-source stack-source TEST_BASE_URL TEST_EXTENSION))))))))

(deftest test-class-graph-with-duplicates
  (testing (let [stack-source "sendMessage:410, ActorCell (akka.event)
                               addLogger:205, LoggingBus (akka.event)
                               secondMethod:53, LoggingBus (foobar)
                               firstMethod:129, LoggingBus (foobar)
                               firstMethod:129, LoggingBus (foobar)
                               apply:-1, LoggingBus$$Lambda/0x000000e0011f5b90 (akka.event)"]
             (is (= (set [{:from "akka.event:LoggingBus$$Lambda/0x000000e0011f5b90" :to "foobar:LoggingBus" :label "firstMethod" :link "https://github.com/DataDog/logs-backend/tree/prod/domains/event-platform/shared/libs/service/src/main/java/foobar/LoggingBus.java#L129"}
                          {:from "foobar:LoggingBus" :to "foobar:LoggingBus" :label "secondMethod" :link "https://github.com/DataDog/logs-backend/tree/prod/domains/event-platform/shared/libs/service/src/main/java/foobar/LoggingBus.java#L53"}
                          {:from "foobar:LoggingBus" :to "akka.event:LoggingBus" :label "addLogger" :link "https://github.com/DataDog/logs-backend/tree/prod/domains/event-platform/shared/libs/service/src/main/java/akka/event/LoggingBus.java#L205"}
                          {:from  "akka.event:LoggingBus" :to "akka.event:ActorCell" :label "sendMessage" :link "https://github.com/DataDog/logs-backend/tree/prod/domains/event-platform/shared/libs/service/src/main/java/akka/event/ActorCell.java#L410"}])
                    (set (all-edges (classes-graph-from-one-source stack-source TEST_BASE_URL TEST_EXTENSION))))))))

(deftest test-marked-class-graph
  (testing (let [stack-source "> sendMessage:410, ActorCell (akka.event)
                               addLogger:205, LoggingBus (akka.event)
                               > thirdMethod:205, ThirdLoggingBus (akka.event)
                               > secondMethod:205, SecondLoggingBus (akka.event)
                               firstMethod:205, LoggingBus (akka.event)
                               $anonfun$startDefaultLoggers$4:129, LoggingBus (foobar)
                               > apply:-1, LoggingBus$$Lambda/0x000000e0011f5b90 (akka.event)"]
             (is (= (set [{:from "akka.event:LoggingBus$$Lambda/0x000000e0011f5b90" :to "akka.event:SecondLoggingBus" :label "secondMethod" :link "https://github.com/DataDog/logs-backend/tree/prod/domains/event-platform/shared/libs/service/src/main/java/akka/event/SecondLoggingBus.java#L205" :skipped true}
                          {:from "akka.event:SecondLoggingBus" :to "akka.event:ThirdLoggingBus" :label "thirdMethod" :link "https://github.com/DataDog/logs-backend/tree/prod/domains/event-platform/shared/libs/service/src/main/java/akka/event/ThirdLoggingBus.java#L205"}
                          {:from "akka.event:ThirdLoggingBus" :to "akka.event:ActorCell" :label "sendMessage" :link "https://github.com/DataDog/logs-backend/tree/prod/domains/event-platform/shared/libs/service/src/main/java/akka/event/ActorCell.java#L410" :skipped true}])
                    (set (all-edges (classes-graph-from-one-source stack-source TEST_BASE_URL TEST_EXTENSION))))))))
