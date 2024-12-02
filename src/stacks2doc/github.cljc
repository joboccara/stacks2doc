(ns stacks2doc.github 
  (:require
   [clojure.string :as string]))

(defn github-link
  [code_path
   extension
   package
   classname
   line-number]
  (str code_path
       "/"
       (string/replace package #"\." "/")
       "/"
       classname
       extension
       "#L"
       line-number
       ))