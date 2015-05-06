(ns cljs-bjj-tournament.matches
  (:require [reagent.ratom :refer [atom]]
            [re-frame.core :refer [subscribe
                                   dispatch]]
            [re-com.util :refer [enumerate]]
            [re-com.core :refer [button
                                 selection-list
                                 h-box
                                 title
                                 line
                                 v-box
                                 hyperlink-href]]))
(defn match-panel
  []
  (let [matches (subscribe [:matches])
        competitors (subscribe [:competitors])
        divisions (subscribe [:divisions])
        choice1 (atom #{})
        choice2 (atom #{})
        division (atom #{(first (vals @divisions))})
        match-link (fn
                     [m]
                     (let [p1 (@competitors (first m))
                           p2 (@competitors (last m))]
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
        (for [[id m first? last?] (enumerate @matches)] 
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
         :gap "10px"
         :children 
         (let [filter-fn (if (nil? (first @division))
                           (constantly true)
                           (:filter-fn (first @division)))]
           [[selection-list
             :model @choice1
             :on-change #(reset! choice1 %)
             :choices  (sort-by #(.full-name %) 
                                (filter filter-fn 
                                        (vals @competitors)))
             :label-fn #(.full-name-club %)
             :multi-select? false]
            [selection-list
             :model @choice2
             :on-change #(reset! choice2 %)
             :choices (sort-by #(.full-name %) 
                               (filter filter-fn 
                                       (vals @competitors)))
             :label-fn #(.full-name-club %)
             :multi-select? false]              
            [button
             :label "Add match"
             :disabled? (or (empty-selection? @choice1) 
                            (empty-selection? @choice2))
             :on-click #(dispatch [:add-match 
                                   (:guid (first @choice1)) 
                                   (:guid (first @choice2))])]])]]])))