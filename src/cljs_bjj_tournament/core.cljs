(ns cljs-bjj-tournament.core
  (:require [reagent.core :as reagent]
            [re-com.util :refer [get-element-by-id]]
            [re-com.core :refer [title 
                                 v-box h-box
                                 line
                                 selection-list]]
            [re-frame.core :refer [subscribe
                                   dispatch]]
            [cljs-bjj-tournament.state :refer [initialise]]))

;; (repl/connect "http://localhost:9000/repl")

(enable-console-print!)

(defn sidebar
    []
    [title 
     :label "sidebar"
     :level :level2])
 
(defn main
    []
    (let [initialised (subscribe [:initialised])
          competitors (subscribe [:competitors])
          choice1 (atom #{})
          choice2 (atom #{})]
      (fn 
        []
        (when @initialised 
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
                  :label "Create a match"
                  :level :level3]
                 [h-box
                  :children 
                  [(map (fn [c]
                        [selection-list
                          :model c
                          :on-change #(reset! c %)
                          :choices @competitors
                          :label-fn #(str (:fname %) " " (:lname %))])
                      [choice1 choice2])
                      ]]]]]]]]))))

(defn ^:export mount-app
  []
  (dispatch [:initialise])
  (reagent/render [main] (get-element-by-id "app")))
