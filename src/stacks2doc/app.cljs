(ns stacks2doc.app
  (:require
   [reagent.core :as r]
   [stacks2doc.mermaid :refer [to-flowchart]]
   [stacks2doc.stack :refer [classes-graph-from-sources packages-graph]]))

(declare mermaid-output raw-output remove-nth stack-input)

(def use-detailed-graph (r/atom true))
(def use-label (r/atom true))
(def use-debugging (r/atom false))
(def base-url (r/atom ""))
(def file-extension (r/atom ""))

(defn app []
  (let [stack-sources (r/atom [""])]
    (fn []
      [:div {:class "p-4 space-y-4"}
       [:div {:class "space-x-4"}
        [:button {:class "bg-blue-500 text-white px-4 py-2 rounded hover:bg-blue-600"
                  :on-click #(swap! use-detailed-graph not)}
         "Toggle Graph Type"]
        [:button {:class "bg-purple-500 text-white px-4 py-2 rounded hover:bg-purple-600"
                  :on-click #(swap! use-label not)}
         "Toggle Labels"]
        [:button {:class "bg-orange-500 text-white px-4 py-2 rounded hover:bg-orange-600"
                  :on-click #(swap! use-debugging not)}
         "Toggle Debug"]]
       [:div {:class "space-y-2"}
        [:div {:class "flex items-center space-x-2"}
         [:label {:class "font-bold text-gray-700 w-32"} "Base URL"]
         [:input {:class "p-2 border rounded focus:outline-none focus:ring-2 focus:ring-blue-500 w-64"
                  :type "text"
                  :value (or @base-url "")
                  :on-change #(reset! base-url (-> % .-target .-value))}]]
        [:div {:class "flex items-center space-x-2"}
         [:label {:class "font-bold text-gray-700 w-32"} "File Extension"]
         [:input {:class "p-2 border rounded focus:outline-none focus:ring-2 focus:ring-blue-500 w-64"
                  :type "text"
                  :value (or @file-extension ".java")
                  :on-change #(reset! file-extension (-> % .-target .-value))}]]]
       [:div {:class "grid grid-cols-3 gap-4"}
        (let [stack-sources-value @stack-sources]
          (map #(stack-input stack-sources stack-sources-value %) (vec (range (count @stack-sources)))))]
       (try
         ((if @use-debugging raw-output mermaid-output) (to-flowchart
                                                         (if @use-detailed-graph
                                                           (classes-graph-from-sources @stack-sources
                                                                                       @base-url
                                                                                       @file-extension)
                                                           (packages-graph (first @stack-sources)))
                                                         :detailed @use-detailed-graph
                                                         :label @use-label) "graph")
         (catch :default _
           [:div {:class "text-red-500 font-bold"}
            "Error: Invalid stack trace format."]))])))


(defn stack-input [stack-sources-ref stack-sources position]
  [:div {:class "flex flex-col space-y-2 p-4 border rounded-lg shadow-md bg-white"
         :key (str "diagram-input-" position)}
   [:label {:class "font-bold text-gray-700"} "Stack"]
   [:textarea {:class "p-4 font-mono text-sm border rounded resize-y whitespace-nowrap overflow-x-auto h-64 focus:outline-none focus:ring-2 focus:ring-blue-500"
               :wrap "off"
               :type "text"
               :name  (str "diagram-input-" position)
               :value (nth stack-sources position)
               :on-change #(swap! stack-sources-ref assoc position (-> % .-target .-value))}]
   [:div {:class "flex space-x-2"}
    [:button {:class "bg-green-500 text-white px-4 py-2 rounded hover:bg-green-600"
              :on-click #(swap! stack-sources-ref conj "")} "+"]
    (when (> position 0)
      [:button {:class "bg-gray-200 text-white px-4 py-2 rounded hover:bg-red-600"
                :on-click #(swap! stack-sources-ref remove-nth position)} "❌"])]])

(defn remove-nth [arr n]
  (vec (concat (subvec arr 0 n) (subvec arr (inc n)))))

(defn raw-output [diagram id]
  [:textarea {:class "p-4 font-mono text-sm border rounded"
              :style {:width "100%"}
              :type "text"
              :id id
              :name "mermaid-debug"
              :value diagram}])

(defn mermaid-output [diagram id]
  (let [promise (.render js/window.mermaid "mermaid-css-id", diagram)]
    (set! *warn-on-infer* false)
    (.then promise (fn [result] (set! (.-innerHTML (js/document.getElementById id)) (.-svg result))))
    (set! *warn-on-infer* true)
    [:div {:id id
           :class "bg-gray-100 p-4 border border-gray-300 rounded-md shadow-md overflow-auto"}]))
