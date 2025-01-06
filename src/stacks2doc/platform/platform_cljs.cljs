(ns stacks2doc.platform.platform-cljs)

(def from-base64 js/atob)

(def to-base64 js/btoa)

(defn from-json [json]
  (js->clj (js/JSON.parse json)))

(defn to-json [structure]
  (js/JSON.stringify (clj->js structure)))