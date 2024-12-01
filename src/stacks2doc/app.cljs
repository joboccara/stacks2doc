(ns stacks2doc.app
  (:require [reagent.core :as r]
            [stacks2doc.dummy-calculator :as dummy-calculator]))

(declare diagram-textarea diagram-result mermaid-form operands-form operand-input operand-textarea)

(def app
  (let [operands (r/atom {:operand1 2, :operand2 3})
        diagram (r/atom "graph LR;A-->B")]
    (fn []
    (let [value1 (js/parseInt (:operand1 @operands))
          value2 (js/parseInt (:operand2 @operands))
          result (dummy-calculator/my_sum value1 value2)]
      [:<>
       [:div
        [:p {:class "dummy-style"} (str "Below this there should be two textboxes to input numbers, and the sum displayed here: " result ". This text should be in red.")]
        (operands-form operands)]
       [:hr]
       (mermaid-form diagram)
       ]))))

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

(defn mermaid-form [diagram] 
    [:div
      [:p "Below this there should be a text input for a mermaid diagram, that should be rendered below it:"]
      (diagram-textarea diagram)
      (diagram-result @diagram)])

(defn diagram-textarea [diagram]
  [:textarea {:type "text"
              :id "diagram-input"
              :name  "diagram-input"
              :value @diagram
              :on-change #(reset! diagram (-> % .-target .-value))}])

(defn diagram-result [diagram]
  (let [id (str "mermaid-diagram")
        promise (.render js/window.mermaid "mermaid-css-id", diagram)]
    (.then promise (fn [result] (set! (.-innerHTML (js/document.getElementById id)) (.-svg result))))
    [:div {:id id}]))