(ns stacks2doc.app
  (:require [reagent.core :as r]
            [stacks2doc.stack :refer [classes-graph-from-sources]]
            [stacks2doc.mermaid :refer [to-flowchart]]))

(declare mermaid-output remove-nth stack-input)

(defn app []
  (let [stack-sources (r/atom [""])]
    (fn []
      [:<>
       (map #(stack-input stack-sources %) (vec (range (count @stack-sources))))
       (mermaid-output (to-flowchart (classes-graph-from-sources @stack-sources)) "graph")])))

(defn stack-input [stack-sources position]
  [:div {:class "sidebar"}
   [:div {:class "main-area"}
    [:div "Paste your stack here"]
    [:textarea {:type "text"
                :id "diagram-input"
                :name  "diagram-input"
                :value (nth @stack-sources position)
                :on-change #(swap! stack-sources assoc position (-> % .-target .-value))}]]
   [:button {:on-click #(swap! stack-sources conj "")} "+"]
   [:button {:on-click #(swap! stack-sources remove-nth position)} "❌"]])

(defn remove-nth [arr n]
  (vec (concat (subvec arr 0 n) (subvec arr (inc n)))))

(defn mermaid-output [diagram id]
  (let [promise (.render js/window.mermaid "mermaid-css-id", diagram)]
    (set! *warn-on-infer* false)
    (.then promise (fn [result] (set! (.-innerHTML (js/document.getElementById id)) (.-svg result))))
    (set! *warn-on-infer* true)
    [:div {:id id :class "display"}]))