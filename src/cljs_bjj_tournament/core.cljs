(ns cljs-bjj-tournament.core
  (:require [reagent.core :as reagent]
            [re-com.util :refer [get-element-by-id]]
            [re-com.core :refer [title 
                                 v-box h-box
                                 line]]))

;; (repl/connect "http://localhost:9000/repl")

(enable-console-print!)

(defn sidebar
    []
    [title 
     :label "sidebar"
     :level :level2])
 
(defn main
    []
    [h-box 
     :gap "10px"
     :children
     [[sidebar]
      [v-box 
      :children 
      [[title 
        :label "Aucklandbjj.com tournament planner"
        :level :level1]
       [line]
       [v-box
        :children 
        [[title 
          :label "Competitors"
          :level :level3]
         [:ul
          [:li "me"]]]]]]]])

(defn ^:export mount-app
  []
  (reagent/render [main] (get-element-by-id "app")))
