(ns cljs-bjj-tournament.competitors
  (:require [re-com.core :refer [v-box
                                  title
                                  line
                                  label
                                  h-box
                                  checkbox
                                  row-button]
             			  :refer-macros [handler-fn]]
            [re-com.util :refer [enumerate]]
            [re-frame.core :refer [subscribe
                                   dispatch]]
            [reagent.core :as reagent]))

(defn data-row
  [id row first? last? col-widths mouse-over click-msg]
  (let [mouse-over-row? (identical? @mouse-over row)]
    [h-box
     :class    "rc-div-table-row"
     :attr     {:on-mouse-over (handler-fn (reset! mouse-over row))
                :on-mouse-out  (handler-fn (reset! mouse-over nil))}
     :children [[h-box
                 :width (:sort col-widths)
                 :gap "2px"
                 :align :center
                 :children [[row-button
                             :md-icon-name    "md-arrow-back md-rotate-90" ;; "md-arrow-back md-rotate-90", "md-play-arrow md-rotate-270", "md-expand-less"
                             :mouse-over-row? mouse-over-row?
                             :tooltip         "Move this line up"
                             :disabled?       (and first? mouse-over-row?)
                             :on-click        #(dispatch [:move-competitor-up id])]
                            [row-button
                             :md-icon-name    "md-arrow-forward md-rotate-90" ;; "md-arrow-forward md-rotate-90", "md-play-arrow md-rotate-90", "md-expand-more"
                             :mouse-over-row? mouse-over-row?
                             :tooltip         "Move this line down"
                             :disabled?       (and last? mouse-over-row?)
                             :on-click        #(dispatch [:move-competitor-down id])]]]
                [label :label (.full-name row) :width (:name col-widths)]
                [label :label (:name (:club row)) :width (:club col-widths)]
                [label :label (:yob   row) :width (:yob   col-widths)]
                [label :label (:belt   row) :width (:belt   col-widths)]
                [h-box
                 :gap      "2px"
                 :width    (:actions col-widths)
                 :align    :center
                 :children [[row-button
                             :md-icon-name    "md-content-copy"
                             :mouse-over-row? mouse-over-row?
                             :tooltip         "Copy this line"
                             :on-click        #(reset! click-msg (str "copy row " (:id row)))]
                            [row-button
                             :md-icon-name    "md-mode-edit"
                             :mouse-over-row? mouse-over-row?
                             :tooltip         "Edit this line"
                             :on-click        #(reset! click-msg (str "edit row " (:id row)))]
                            [row-button
                             :md-icon-name    "md-delete"
                             :mouse-over-row? mouse-over-row?
                             :tooltip         "Delete this line"
                             :on-click        #(reset! click-msg (str "delete row " (:id row)))]]]]]))


(defn data-table
  []
  (let [mouse-over (reagent/atom nil)
        click-msg  (reagent/atom "")]
    (fn [rows col-widths]
      [v-box
       :align    :start
       :gap      "10px"
       :children [[v-box
                   :class    "rc-div-table"
                   :children [^{:key "0"}
                              [h-box
                               :class    "rc-div-table-header"
                               :children [[label :label "Sort" :width (:sort col-widths)]
                                          [label :label "Name" :width (:name col-widths)]
                                          [label :label "Club" :width (:club col-widths)]
                                          [label :label "YOB" :width (:yob col-widths)]
                                          [label :label "Belt" :width (:belt col-widths)]
                                          [label :label "Actions" :width (:actions col-widths)]]]
                              (for [[id row first? last?] (enumerate rows)]
                                ^{:key id} [data-row id row first? last? col-widths mouse-over click-msg])]]]])))

(defn competitor-panel
  []
  (let [competitors (subscribe [:competitors])
        col-widths {:sort "2.6em" :name "7.5em" :club "4em" :yob "4em" :belt "4.5em" :actions "4.5em"}]
    (fn []
      [v-box 
       :gap "10px"
       :children 
       [[title 
         :label "Current competitors"]
        [data-table @competitors col-widths]
        
        [line]
        [title
         :label "Add competitors"]]])))