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
                                 gap
                                 hyperlink-href
                                 hyperlink
                                 radio-button]]))
(defn match-panel
  []
  (let [matches (subscribe [:matches])
        competitors (subscribe [:competitors])
        divisions (subscribe [:divisions])
        choice1 (atom #{})
        choice2 (atom #{})
        division (atom #{(first @divisions)})
        sort-button (atom :weight)
        match-link (fn
                     [m]
                     (let [p1 (@competitors (:p1 m))
                           p2 (@competitors (:p2 m))]
                       [h-box
                        :children
                        [[hyperlink-href
                          :label (str "Start Match -- "
                                      (.full-name-club p1) " Vs "
                                      (.full-name-club p2))
                          :href (str "scorejudo.html?"
                                     (.url-string p1 "p1")
                                     (.url-string p2 "p2"))
                          ; :href (str "scoreMaster/index.html?"
                          ;            (.url-string p1 "p1")
                          ;            (.url-string p2 "p2"))
                          :target "_blank"]
                         [gap :size "5em"]
                         [hyperlink
                          :label "[x]"
                          :on-click #(dispatch [:delete-match (:guid m)])]]]))
        empty-selection? #(nil? (first %))]

    (fn []
      [v-box
       :children
       [[h-box
         :justify :between
         :children [[title
                     :label "Matches"
                     :level :level2]
                    [button
                     :label "Show all"
                     :on-click #(reset! division #{})]]]
        (for [[id m first? last?] (enumerate (filter (fn [m]
                                                       (let [div (first @division)]
                                                         (if div
                                                           (= (:division m)
                                                              (:name div))
                                                           true)))
                                                     @matches))]
          ^{:key id} [match-link m id])
        [line]
        [title
         :label "Choose a division"
         :level :level2]
        [selection-list
         :model @division
         :on-change #(reset! division %)
         :id-fn identity
         :choices  @divisions
         :label-fn :name
         :multi-select? false]
        [h-box
         :justify :between
         :children [[title
                     :label "Create a match"
                     :level :level2]
                    [button
                     :label "Auto create matches"
                     :on-click #(dispatch [:auto-create-matches (first @division)])]]]
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
         (let [div       (first @division)
               filter-fn (if (nil? div)
                           (constantly true)
                           #(.in-division? div %))
               sort-fn (case @sort-button
                         :name #(.full-name %)
                         #(- (float (@sort-button %))))]
           [[selection-list
             :model @choice1
             :on-change #(reset! choice1 %)
             :choices  (->> @competitors
                            vals
                            (filter filter-fn)
                            (remove @choice2)
                            (sort-by sort-fn))
             :label-fn #(.full-name-club %)
             :id-fn identity
             :multi-select? false]
            [selection-list
             :model @choice2
             :on-change #(reset! choice2 %)
             :choices (->> @competitors
                            vals
                            (filter filter-fn)
                            (remove @choice1)
                            (sort-by sort-fn))
             :label-fn #(.full-name-club %)
             :id-fn identity
             :multi-select? false]
            [button
             :label "Add match"
             :disabled? (or (empty-selection? @choice1)
                            (empty-selection? @choice2))
             :on-click #(dispatch [:add-match
                                   (:name div)
                                   (:guid (first @choice1))
                                   (:guid (first @choice2))])]])]]])))