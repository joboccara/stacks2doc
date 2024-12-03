(ns stacks2doc.app
  (:require
   [reagent.core :as r]
   [stacks2doc.mermaid :refer [to-detailed-flowchart to-flowchart]]
   [stacks2doc.stack :refer [classes-graph-from-sources]]))

(declare mermaid-output remove-nth stack-input)

(def use-detailed-graph (r/atom true))

(defn app []
  (let [stack-sources (r/atom [""])]
    (fn []
      [:div {:class "p-4 space-y-4"}
       [:button {:class "bg-blue-500 text-white px-4 py-2 rounded hover:bg-blue-600"
                 :on-click #(swap! use-detailed-graph not)}
        "Toggle Graph Type"]
       [:div {:class "grid grid-cols-3 gap-4"}
        (map #(stack-input stack-sources %) (vec (range (count @stack-sources))))]
       (try
         (mermaid-output
          ((if @use-detailed-graph to-detailed-flowchart to-flowchart)
           (classes-graph-from-sources @stack-sources)) "graph")
         (catch :default _
           [:div {:class "text-red-500 font-bold"}
            "Error: Invalid stack trace format."]))])))

(defn stack-input [stack-sources position]
  [:div {:class "sidebar"}
   [:button {:on-click #(swap! use-detailed-graph not)}]
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