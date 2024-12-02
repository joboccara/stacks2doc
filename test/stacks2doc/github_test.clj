(ns stacks2doc.github-test
  (:require [clojure.test :refer [deftest testing is]]
            [stacks2doc.github :refer [github-link]]))

(deftest test-github-link
  (testing (is (= "https://github.com/akka/akka/blob/main/akka-actor/src/main/scala/akka/actor/ActorRef.scala#L121"
                  (github-link "https://github.com/akka/akka/blob/main/akka-actor/src/main/scala"
                               ".scala"
                               "akka.actor"
                               "ActorRef"
                               121)))))