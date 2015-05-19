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
            [cljs-bjj-tournament.competitors :refer [competitor-panel]]
            [cljs-bjj-tournament.matches :refer [match-panel]]))

;; (repl/connect "http://localhost:9000/repl")

(enable-console-print!)

(def tabs-definition
  [{:id :intro           :level :major :label "Introduction"}
   ; {:id :tournament      :level :major :label "Create Tournament"}
   {:id :competitors     :level :major :label "Load Competititors"}
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
  [v-box
   :children [
              [:div "Welcome to the Aucklandbjj.com tournament planner."]
              [:div "This tool will allow you to plan and score your 
                    Brazilian Jiu-Jitsu tournament"]
              [:div "Currently this tool only saves to your local computer so 
                    please load the files on the computer you are going to 
                    use on the day"]
              [:div "First load in the competitiors for the competition"]]])

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
              :matches [match-panel]
              [intro])]]]]))))

(defn ^:export mount-app
  []
  (dispatch [:initialise])
  (reagent/render [main] (get-element-by-id "app")))
