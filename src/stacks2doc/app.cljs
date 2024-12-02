(ns stacks2doc.app
  (:require [reagent.core :as r]
            [stacks2doc.dummy-calculator :as dummy-calculator]
            [stacks2doc.stack :refer [packages-graph]]
            [stacks2doc.graph :refer [make-graph]]
            [stacks2doc.mermaid :refer [to-flowchart]]))

(declare diagram-textarea mermaid-output operands-form operand-input operand-textarea sum-form)

(declare real-app stack-input)

(def app
  (fn []
    [:<>
     [real-app]
     [:hr]
     [sum-form]]))

(defn real-app []
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

(defn sum-form []
  (let [operands (r/atom {:operand1 2, :operand2 3})]
    (fn []
      (let [value1 (js/parseInt (:operand1 @operands))
            value2 (js/parseInt (:operand2 @operands))
            result (dummy-calculator/my_sum value1 value2)]
        [:div
         [:p {:class "dummy-style"} (str "Below this there should be two textboxes to input numbers, and the sum displayed here: " result ". This text should be in red.")]
         (operands-form operands)]))))

(defn operands-form [operands]
  [:form
   (operand-input operands :operand1 "First number:")
   (operand-input operands :operand2 "Second number:")])

(defn operand-input [operands kw display]
  [:div
   [:label {:for (name kw)} display]
   (operand-textarea operands kw)])

(defn operand-textarea [operands kw]
  [:textarea {:type "text"
              :id (name kw)
              :name (name kw)
              :value (kw @operands)
              :on-change #(swap! operands assoc kw (-> % .-target .-value))}])

(defn mermaid-form []
  (let [diagram (r/atom "graph LR;A-->B")]
  (fn []
    [:div
     [:p "Below this there should be a text input for a mermaid diagram, that should be rendered below it:"]
     (diagram-textarea diagram)
     #_[mermaid-output @diagram "test-mermaid"]])))

(defn diagram-textarea [diagram]
  [:textarea {:type "text"
              :id "diagram-input"
              :name  "diagram-input"
              :value @diagram
              :on-change #(reset! diagram (-> % .-target .-value))}])

(defn mermaid-output [diagram id]
  (let [promise (.render js/window.mermaid "mermaid-css-id", diagram)]
    (set! *warn-on-infer* false)
    (.then promise (fn [result] (set! (.-innerHTML (js/document.getElementById id)) (.-svg result))))
    (set! *warn-on-infer* true)
    [:div {:id id}]))