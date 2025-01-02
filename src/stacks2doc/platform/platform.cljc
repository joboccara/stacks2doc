(ns stacks2doc.platform.platform
  #?(:clj (:require [stacks2doc.platform.platform-clj :as p-clj])
     :cljs (:require [stacks2doc.platform.platform-cljs :as p-cljs])))

(def from-base64
  #?(:clj p-clj/from-base64
     :cljs p-cljs/from-base64))

(def to-base64
  #?(:clj p-clj/to-base64
     :cljs p-cljs/to-base64))

(def from-json
  #?(:clj p-clj/from-json
     :cljs p-cljs/from-json))

(def to-json
  #?(:clj p-clj/to-json
     :cljs p-cljs/to-json))