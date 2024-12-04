(ns stacks2doc.core
  (:require
   [stacks2doc.app :refer [app]]
   [stacks2doc.demo :refer [demo-app]]
   [reagent.dom.client :as rdom-client]))

(defn init []
  (let [path (.-pathname js/window.location)]
    (if (= path "/demo")
      (rdom-client/render (rdom-client/create-root (.getElementById js/document "app")) [demo-app])
      (rdom-client/render (rdom-client/create-root (.getElementById js/document "app")) [app]))))
