(ns stacks2doc.query-strings-test
  (:require [clojure.test :refer [deftest is testing]]
            [stacks2doc.query-strings :refer [add-to-query-strings map-to-query-strings query-strings-to-map]]))

(deftest add-to-empty-query-strings
  (testing
   (is (= "?key=value"
          (add-to-query-strings "" "key" "value")))))

(deftest append-to-query-strings
  (testing
   (is (= "?key1=value1&key2=value2"
          (add-to-query-strings "?key1=value1" "key2" "value2")))))

(deftest empty-query-strings-to-map-test
  (testing
   (is (= {}
          (query-strings-to-map "")))))

(deftest query-strings-to-map-test
  (testing
   (is (= {"key1" "value1", "key2" "value2"}
          (query-strings-to-map "?key1=value1&key2=value2")))))

(deftest ?-query-strings-to-map-test
  (testing
   (is (= {}
          (query-strings-to-map "?")))))

(deftest map-to-query-strings-test
  (testing
   (is (= "?key1=value1&key2=value2"
          (map-to-query-strings {"key1" "value1", "key2" "value2"})))))

(deftest empty-map-to-query-strings-test
  (testing
   (is (= ""
          (map-to-query-strings {})))))
