(ns stacks2doc.app
  (:require [reagent.core :as r]
            [stacks2doc.dummy-calculator :as dummy-calculator]))

(declare operands-form operand-input text-input)

(def app
  (let [operands (r/atom {:operand1 2, :operand2 3})]
    (fn []
    (let [value1 (js/parseInt (:operand1 @operands))
          value2 (js/parseInt (:operand2 @operands))
          result (dummy-calculator/my_sum value1 value2)]
      [:<>
       [:div
        [:p {:class "dummy-style"} (str "This page should display two textboxes to input numbers, and the sum displayed here: " result ". This text should be in red.")]
        (operands-form operands)]]))))

(defn operands-form [operands]
  [:form
   (operand-input operands :operand1 "First number:")
   (operand-input operands :operand2 "Second number:")])

(defn operand-input [operands kw display]
  [:div
   [:label {:for (name kw)} display]
   (text-input operands kw)])

(defn text-input [operands kw]
  [:textarea {:type "text"
              :id (name kw)
              :name (name kw)
              :value (kw @operands)
              :on-change #(swap! operands assoc kw (-> % .-target .-value))}])

(js/parseInt "42")