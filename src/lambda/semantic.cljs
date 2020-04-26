(ns lambda.semantic
  (:require
   [cljsjs.semantic-ui-react]
   [goog.object :refer [getValueByKeys get]]))

(def semantic-ui js/semanticUIReact)

(defn component
  "Get a component from semantic-ui-react"
  [k & ks]
  (if (seq ks)
    (apply getValueByKeys semantic-ui k ks)
    (get semantic-ui k)))

(def container        (component "Container"))
(def button           (component "Button"))
(def button-group     (component "Button" "Group"))
(def input            (component "Input"))
(def segment          (component "Segment"))
(def segment-group    (component "Segment" "Group"))
(def sidebar          (component "Sidebar"))
(def sidebar-pushable (component "Sidebar" "Pushable"))
(def sidebar-pusher   (component "Sidebar" "Pusher"))
(def icon             (component "Icon"))
(def checkbox         (component "Checkbox"))
(def message          (component "Message"))
(def message-header   (component "Message" "Header"))
(def message-content  (component "Message" "Content"))
(def popup            (component "Popup"))
(def placeholder      (component "Placeholder"))
(def placeholder-line (component "Placeholder" "Line"))
(def label            (component "Label"))
(def form             (component "Form"))
