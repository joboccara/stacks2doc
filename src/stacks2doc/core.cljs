(ns stacks2doc.core
  (:require [stacks2doc.app]
            [reagent.dom.client :as rdom-client]))

(defn app [] stacks2doc.app/app)

(defn init []
  (rdom-client/render (rdom-client/create-root (.getElementById js/document "app")) [app]))
