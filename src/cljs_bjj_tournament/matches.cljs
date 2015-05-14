(ns cljs-bjj-tournament.matches
  (:require [reagent.ratom :refer [atom]]
            [re-frame.core :refer [subscribe
                                   dispatch]]
            [re-com.util :refer [enumerate]]
            [re-com.core :refer [button
                                 selection-list
                                 h-box
                                 title
                                 label
                                 line
                                 v-box
                                 hyperlink-href
                                 radio-button]]))
(defn match-panel
  []
  (let [matches (subscribe [:matches])
        competitors (subscribe [:competitors])
        divisions (subscribe [:divisions])
        choice1 (atom #{})
        choice2 (atom #{})
        division (atom #{(first (vals @divisions))})
        sort-button (atom :weight)
        match-link (fn
                     [m]
                     (let [p1 (@competitors (:p1 m))
                           p2 (@competitors (:p2 m))]
                       [hyperlink-href
                        :label (str "Start Match -- " 
                                    (.full-name-club p1) " Vs " 
                                    (.full-name-club p2))
                        :href (str "scoreMaster/index.html?" 
                                   (.url-string p1 "p1") 
                                   (.url-string p2 "p2"))
                        :target "_blank"]))
        empty-selection? #(nil? (first %))]
    (fn []
      [v-box
       :children 
       [[title 
         :label "Matches"
         :level :level2]
        (for [[id m first? last?] (enumerate (filter #(= (:division %)
                                                         (:name (first @division))) 
                                                     @matches))] 
          ^{:key id} [match-link m])
        [line]
        [title 
         :label "Choose a division"
         :level :level2]
        [selection-list
         :model @division
         :on-change #(reset! division %)
         :choices (vals @divisions)
         :label-fn :name
         :multi-select? false]
        [title 
         :label "Create a match"
         :level :level2]
        [h-box
         :gap "5px"
         :children
         [[title
           :label "Sort by:"
           :level :level3]
          [radio-button
           :model @sort-button
           :value :name
           :on-change #(reset! sort-button :name)]
          [label 
           :label "Name"]
          [radio-button
           :model @sort-button
           :value :yob
           :on-change #(reset! sort-button :yob)]
          [label 
           :label "Age"]
          [radio-button
           :model @sort-button
           :value :weight
           :on-change #(reset! sort-button :weight)]
          [label 
           :label "Weight"]]]
        [h-box
         :gap "10px"
         :children 
         (let [filter-fn (if (nil? (first @division))
                           (constantly true)
                           (:filter-fn (first @division)))
               sort-fn (case @sort-button
                         :name #(.full-name %)
                         #(- (float (@sort-button %))))]
           [[selection-list
             :model @choice1
             :on-change #(reset! choice1 %)
             :choices  (sort-by sort-fn 
                                (filter filter-fn 
                                        (vals @competitors)))
             :label-fn #(.full-name-club %)
             :multi-select? false]
            [selection-list
             :model @choice2
             :on-change #(reset! choice2 %)
             :choices (sort-by sort-fn 
                               (filter filter-fn 
                                       (vals @competitors)))
             :label-fn #(.full-name-club %)
             :multi-select? false]              
            [button
             :label "Add match"
             :disabled? (or (empty-selection? @choice1) 
                            (empty-selection? @choice2))
             :on-click #(dispatch [:add-match 
                                   (:name (first @division))
                                   (:guid (first @choice1)) 
                                   (:guid (first @choice2))])]])]]])))