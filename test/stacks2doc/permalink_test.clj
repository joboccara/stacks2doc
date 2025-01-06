(ns stacks2doc.permalink-test
  (:require [clojure.test :refer [deftest is testing]]
            [stacks2doc.permalink :refer [encode decode]]))

(deftest encoding-decoding-is-identity
  (testing
   (let [input {:stacks [{:source "stack1"}
                         {:source "stack2"}
                         {:source "stack3"}]}]
     (is (= (decode (encode input))
            input)))))

(deftest encoding-decoding-is-identity-with-whitespace
  (testing
   (let [input {:stacks [{:source "stack 1"}
                         {:source "stack\t2"}
                         {:source "stack\n3"}]}]
     (is (= (decode (encode input))
            input)))))

(deftest encoding-has-no-whitespace
  (testing
   (let [input {:stacks [{:source "stack 1"}
                         {:source "stack\t2"}
                         {:source "stack\n3"}]}]
     (is (not (re-find #"\s" (encode input)))))))
