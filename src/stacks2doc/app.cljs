(ns stacks2doc.app
  (:require [reagent.core :as r]
            [stacks2doc.stack :refer [packages-graph]]
            [stacks2doc.mermaid :refer [to-flowchart]]))

(declare mermaid-output stack-input)

(defn app []
  (let [stack-source (r/atom "")]
    (fn []
    [:<>
     (stack-input stack-source)
     (mermaid-output (to-flowchart (packages-graph @stack-source)) "packages-graph")])))

(defn stack-input [stack]
  [:div
    [:div "Paste your stack here"]
    [:textarea {:type "text"
                :id "diagram-input"
                :name  "diagram-input"
                :value @stack
                :on-change #(reset! stack (-> % .-target .-value))}]])

(defn mermaid-output [diagram id]
  (let [promise (.render js/window.mermaid "mermaid-css-id", diagram)]
    (set! *warn-on-infer* false)
    (.then promise (fn [result] (set! (.-innerHTML (js/document.getElementById id)) (.-svg result))))
    (set! *warn-on-infer* true)
    [:div {:id id}]))