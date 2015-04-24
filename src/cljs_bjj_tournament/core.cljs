(ns cljs-bjj-tournament.core
  (:require [reagent.core :as reagent]
            [reagent.ratom :refer [atom]]
            [re-com.util :refer [get-element-by-id]]
            [re-com.core :refer [title 
                                 v-box h-box
                                 line
                                 selection-list
                                 hyperlink-href
                                 button]]
            [re-frame.core :refer [subscribe
                                   dispatch]]
            [cljs-bjj-tournament.state :refer [initialise]]
            [cljs-bjj-tournament.handlers]))

;; (repl/connect "http://localhost:9000/repl")

(enable-console-print!)

(defn sidebar
    []
    [title 
     :label "sidebar"
     :level :level2])
 
(defn create-match
    []
    (let [matches (subscribe [:matches])
          competitors (subscribe [:competitors])
          choice1 (atom #{})
          choice2 (atom #{})
          full-name #(str (:fname %) " " (:lname %))
          match-link (fn
                       [m]
                       (let [p1Name (full-name (first m))
                             p2Name (full-name (last m))]
                         [hyperlink-href
                          :label (str "Start Match -- " 
                                      p1Name " Vs " p2Name)
                          :href (str "/scoreMaster/?p1Name=" 
                                     p1Name "&p2Name=" p2Name)
                          :target "_blank"]))
          empty-selection? #(nil? (first %))]
     (fn []
      [v-box
        :children 
        [[title 
          :label "Matches"
          :level :level3]
         (for [m @matches] ^{:key m} (match-link m))
         [line]
         [title 
          :label "Create a match"
          :level :level3]
         [h-box
          :gap "10px"
          :children 
          [[selection-list
                  :model @choice1
                  :on-change #(reset! choice1 %)
                  :choices @competitors
                  :label-fn full-name
                  :multi-select? false]
            [selection-list
                  :model @choice2
                  :on-change #(reset! choice2 %)
                  :choices @competitors
                  :label-fn full-name
                  :multi-select? false]              
            [button
             :label "Add match"
             :disabled? (or (empty-selection? @choice1) 
                            (empty-selection? @choice2))
             :on-click #(dispatch [:add-match 
                                   (first @choice1) 
                                   (first @choice2)])]]]]])))
 
(defn main
    []
    (let [initialised (subscribe [:initialised])]
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
              [create-match]]]]]))))

(defn ^:export mount-app
  []
  (dispatch [:initialise])
  (reagent/render [main] (get-element-by-id "app")))
