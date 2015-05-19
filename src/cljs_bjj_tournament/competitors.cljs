(ns cljs-bjj-tournament.competitors
  (:require [re-com.core :refer [v-box
                                 title
                                 line
                                 label
                                 h-box
                                 checkbox
                                 row-button
                                 input-text
                                 info-button
                                 gap
                                 button
                                 hyperlink-href]
             :refer-macros [handler-fn]]
            [re-com.util :refer [get-element-by-id 
                                 enumerate]]
            [re-frame.core :refer [subscribe
                                   dispatch]]
            [reagent.core :as reagent]
            [cljs-bjj-tournament.model :refer [make-competitor
                                               read-csv]]))

(defn data-row
  [id row first? last? col-widths mouse-over click-msg]
  (let [mouse-over-row? (identical? @mouse-over row)]
    [h-box
     :class    "rc-div-table-row"
     :attr     {:on-mouse-over (handler-fn (reset! mouse-over row))
                :on-mouse-out  (handler-fn (reset! mouse-over nil))}
     :children [[label :label (.full-name row) :width (:name col-widths)]
                [label :label (:gender row) :width (:gender col-widths)]
                [label :label (:club-name row) :width (:club-name col-widths)]
                [label :label (:yob   row) :width (:yob   col-widths)]
                [label :label (:belt   row) :width (:belt   col-widths)]
                [h-box
                 :gap      "2px"
                 :width    (:actions col-widths)
                 :align    :center
                 :children [[row-button
                             :md-icon-name    "md-mode-edit"
                             :mouse-over-row? mouse-over-row?
                             :tooltip         "Edit this line"
                             :on-click        #(dispatch [:edit-competitor id])]
                            [row-button
                             :md-icon-name    "md-delete"
                             :mouse-over-row? mouse-over-row?
                             :tooltip         "Delete this line"
                             :on-click        #(dispatch [:delete-competitor id])]]]]]))

(defn data-table
  []
  (let [mouse-over (reagent/atom nil)
        click-msg  (reagent/atom "")
        sort-key   (reagent/atom :fname)
        label-fn   (fn [label-str label-key col-widths]
                     [h-box 
                      :gap "2px"
                      :width (label-key col-widths)
                      :children 
                      [[label 
                        :label label-str]
                       [row-button
                        :md-icon-name "md-sort"
                        :tooltip (str "sort on " label-str)
                        :on-click #(reset! sort-key label-key)]]])
        sort-fn    (fn [key]
                     (case key
                       :name #(.full-name %)
                       :belt #(case (:belt %)
                                "White"  1
                                "Blue"   2
                                "Purple" 3
                                "Brown"  4
                                "Black"  5
                                6)
                       key))]
    (fn [rows col-widths]
      [v-box
       :align    :start
       :gap      "10px"
       :children [[v-box
                   :class    "rc-div-table"
                   :children [^{:key "0"}
                              [h-box
                               :class    "rc-div-table-header"
                               :children [[label-fn "Name" :name col-widths]
                                          [label-fn "Gender" :gender col-widths]
                                          [label-fn "Club" :club-name col-widths]
                                          [label-fn "YOB" :yob col-widths]
                                          [label-fn "Belt" :belt col-widths]
                                          [label-fn "Actions" :actions col-widths]]]
                              (for [[id row first? last?] (enumerate 
                                                            (sort-by 
                                                              (sort-fn @sort-key) 
                                                              rows))]
                                ^{:key id} [data-row (:guid row) row first? last? col-widths mouse-over click-msg])]]]])))

(defn field-label
  ;takes the field label and a text or hiccup help text
  ([text]
   (field-label text nil))
  ([text info]
   [h-box 
    :children (concat 
                [[label 
                  :label text
                  :style {:font-variant "small-caps"}]]
                (when info
                  [[gap :size "5px"] 
                   [info-button
                    :info (if string? info 
                            [:div info]
                            info)]]))]))

(defn competitor-field
  [name field competitor]
  [h-box
   :gap "10px"
   :children
   [[field-label name]
    [input-text 
     :model (field @competitor)
     :on-change #(swap! competitor assoc field %)]]])

(defn add-competitor
  []
  (let [edit-competitor (subscribe [:edit-competitor])
        competitors (subscribe [:competitors])
        new-competitor (reagent/atom 
                         (make-competitor "fname" "lname" 
                                          "gender" "yob" "belt" 
                                          "club-name" 0))]
    (fn []
      (let [competitor (if-not (nil? @edit-competitor)
                         (reagent/atom (@competitors @edit-competitor))
                         new-competitor)]
        [v-box
         :gap "5px"
         :children
         [[title
           :label (if @edit-competitor 
                    "Edit Competitor" 
                    "Add competitor ")]
          [competitor-field "First Name" :fname competitor]
          [competitor-field "Last Name" :lname competitor]
          [competitor-field "Gender" :gender competitor]
          [competitor-field "YOB" :yob competitor]
          [competitor-field "Belt" :belt competitor]
          [competitor-field "Club" :club-name competitor]
          [button
           :label "Save"
           :on-click (fn []
                       (dispatch [:add-competitor @competitor])
                       (dispatch [:edit-competitor nil])
                       (reset! new-competitor 
                               (make-competitor "fname" "lname" 
                                                "gender" "yob" "belt" 
                                                "club-name" 0)))]]]))))

(defn- load-file-handler
  []
  (let [file (aget (.-files (get-element-by-id "csv-file-open")) 0)
        reader (js/FileReader.)]
    (set!
      (.-onload reader)
      (fn [_] (dispatch [:add-competitors (read-csv (.-result reader))])))
    (.readAsText reader file "UTF8")))

(defn load-csv-file
  []
  [:input {:id        "csv-file-open"
           :type      "file"
           :accept    ".csv"
           :on-change #(load-file-handler)}])

(defn competitor-panel
  []
  (let [competitors (subscribe [:competitors])
        col-widths {:name "15em" :gender "6em" :club-name "15em" :yob "4em" :belt "4.5em" :actions "4.5em"}]
    (fn []
      [v-box 
       :gap "10px"
       :children 
       [[:div "This panel allows you to add, delete and edit competitor
              information in the tool"]
        [:div "To begin it is advised to load competitor information in 
              from a csv file similar to the one found "
              [hyperlink-href 
               :label "here"
               :target "_blank"
               :href "../resources/sample-competitors.csv"]]
        [line]
        [load-csv-file]
        [line]
        [title 
         :label "Current competitors"]
        [data-table (vals @competitors) col-widths]
        
        [line]
        [add-competitor]]])))