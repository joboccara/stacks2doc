(ns stacks2doc.app
  (:require
   [reagent.core :as r]
   [stacks2doc.mermaid :refer [to-flowchart]]
   [stacks2doc.stack :refer [classes-graph-from-sources]]))

(declare mermaid-output remove-nth stack-input)

(def use-detailed-graph (r/atom true))
(def use-label (r/atom true))
(def base-url (r/atom ""))
(def file-extension (r/atom ""))

(defn tee [value]
  (js/console.log value) value)

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
         "Toggle Labels"]]
       [:div {:class "space-y-2"}
        [:label {:class "font-bold text-gray-700"} "Base URL"]
        [:input {:class "p-2 border rounded w-full focus:outline-none focus:ring-2 focus:ring-blue-500"
                 :type "text"
                 :value (or @base-url "")
                 :on-change #(reset! base-url (-> % .-target .-value))}]
        [:label {:class "font-bold text-gray-700"} "File Extension"]
        [:input {:class "p-2 border rounded w-full focus:outline-none focus:ring-2 focus:ring-blue-500"
                 :type "text"
                 :value (or @file-extension ".java")
                 :on-change #(reset! file-extension (-> % .-target .-value))}]]
       [:div {:class "space-y-4"}
        [:p {:class "text-gray-600"} (str "Base URL: " @base-url)]
        [:p {:class "text-gray-600"} (str "File Extension: " @file-extension)]]
       [:div {:class "grid grid-cols-3 gap-4"}
        (map #(stack-input stack-sources %) (vec (range (count @stack-sources))))] 
       (try
         (mermaid-output (to-flowchart
                          (classes-graph-from-sources @stack-sources
                                                           (tee @base-url)
                                                           @file-extension)
                          :detailed @use-detailed-graph
                          :label @use-label) "graph")
         (catch :default _
           [:div {:class "text-red-500 font-bold"}
            "Error: Invalid stack trace format."]))])))

(defn stack-input [stack-sources position]
  [:div {:class "flex flex-col space-y-2 p-4 border rounded-lg shadow-md bg-white"}
   [:label {:class "font-bold text-gray-700"} "Stack"]
   [:textarea {:class "p-4 font-mono text-sm border rounded resize-y whitespace-nowrap overflow-x-auto h-32 focus:outline-none focus:ring-2 focus:ring-blue-500"
               :wrap "off"
               :type "text"
               :id (str "diagram-input-" position)
               :name  (str "diagram-input-" position)
               :value (nth @stack-sources position)
               :on-change #(swap! stack-sources assoc position (-> % .-target .-value))}]
   [:div {:class "flex space-x-2"}
    [:button {:class "bg-green-500 text-white px-4 py-2 rounded hover:bg-green-600"
              :on-click #(swap! stack-sources conj "")} "+"]
    [:button {:class "bg-gray-200 text-white px-4 py-2 rounded hover:bg-red-600"
              :on-click #(swap! stack-sources remove-nth position)} "❌"]]])

(defn remove-nth [arr n]
  (vec (concat (subvec arr 0 n) (subvec arr (inc n)))))

(defn mermaid-output [diagram id]
  (let [promise (.render js/window.mermaid "mermaid-css-id", diagram)]
    (set! *warn-on-infer* false)
    (.then promise (fn [result] (set! (.-innerHTML (js/document.getElementById id)) (.-svg result))))
    (set! *warn-on-infer* true)
    [:div {:id id
           :class "bg-gray-100 p-4 border border-gray-300 rounded-md shadow-md overflow-auto"}]))
