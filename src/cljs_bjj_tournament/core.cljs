(ns cljs-bjj-tournament.core
  (:require [reagent.core :as reagent]
            [reagent.ratom :refer [atom]]
            [re-com.util :refer [get-element-by-id
                                 enumerate]]
            [re-com.core :refer [title 
                                 v-box h-box
                                 line
                                 selection-list
                                 hyperlink-href
                                 button
                                 gap]
             :refer-macros [handler-fn]]
            [re-frame.core :refer [subscribe
                                   dispatch]]
            [cljs-bjj-tournament.state :refer [initialise]]
            [cljs-bjj-tournament.handlers]
            [cljs-bjj-tournament.competitors :refer [competitor-panel]]))

;; (repl/connect "http://localhost:9000/repl")

(enable-console-print!)

(def tabs-definition
  [{:id :intro           :level :major :label "Introduction"}
   {:id :tournament      :level :major :label "Create Tournament"}
   {:id :competitors     :level :major :label "Create Competititors"}
   {:id :divisions       :level :major :label "Create Divisions"}
   {:id :matches         :level :major :label "Create Matches"}
   {:id :results         :level :major :label "Show Results"}
   ])


(defn nav-item
  []
  (let [mouse-over? (reagent/atom false)]
    (fn [tab selected-tab-id]
      (let [selected?   (= @selected-tab-id (:id tab))
            is-major?  (= (:level tab) :major)]
      [:div
       {:style {:width            "150px"
                :line-height      "1.3em"
                :padding-left     (if is-major? "24px" "32px")
                :padding-top      (when is-major? "6px")
                :font-size        (when is-major? "15px")
                :font-weight      (when is-major? "bold")
                :color            (when selected? "#111")
                :border-right     (when selected? "4px #d0d0d0 solid")
                :background-color (if (or
                                        (= @selected-tab-id (:id tab))
                                        @mouse-over?) "#eaeaea")}

        :on-mouse-over (handler-fn (reset! mouse-over? true))
        :on-mouse-out  (handler-fn (reset! mouse-over? false))
        :on-click      (handler-fn (dispatch [:page (:id tab)]))}
       [:span
        {:style {:cursor "default"}}    ;; removes the I-beam over the label
        (:label tab)]]))))


(defn left-side-nav-bar
  [selected-tab-id]
    [v-box
     :class    "noselect"
     :style    {:background-color "#fcfcfc"}
     :size    "1 0 auto"
     :children (for [tab tabs-definition]
                 [nav-item tab selected-tab-id])])
(defn sidebar
    []
    (let [selected-tab-id (subscribe [:page])]
      (fn []
    [v-box 
     :children
     [[gap :size "70px"]
      [left-side-nav-bar selected-tab-id]]])))
 
(defn intro 
  []
  [:div "Hi this is the intro page"])

(defn matches
    []
    (let [matches (subscribe [:matches])
          competitors (subscribe [:competitors])
          choice1 (atom #{})
          choice2 (atom #{})
          match-link (fn
                       [m]
                       (let [p1 (first m)
                             p2 (last m)]
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
          :label "Create a match"
          :level :level2]
         [h-box
          :gap "10px"
          :children 
          [[selection-list
                  :model @choice1
                  :on-change #(reset! choice1 %)
                  :choices  @competitors
                  :label-fn #(.full-name-club %)
                  :multi-select? false]
            [selection-list
                  :model @choice2
                  :on-change #(reset! choice2 %)
                  :choices @competitors
                  :label-fn #(.full-name-club %)
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
    (let [initialised (subscribe [:initialised])
          page (subscribe [:page])]
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
              (case @page 
                :intro [intro]
                :competitors [competitor-panel]
                :matches [matches]
                [intro])]]]]))))

(defn ^:export mount-app
  []
  (dispatch [:initialise])
  (dispatch [:page :competitors])
  (reagent/render [main] (get-element-by-id "app")))
