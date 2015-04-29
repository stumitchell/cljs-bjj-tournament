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
                                 button]
             :refer-macros [handler-fn]]
            [re-com.util :refer [get-element-by-id 
                                 enumerate]]
            [re-frame.core :refer [subscribe
                                   dispatch]]
            [reagent.core :as reagent]
            [cljs-bjj-tournament.model :refer [make-competitor]]))

(defn data-row
  [id row first? last? col-widths mouse-over click-msg]
  (let [mouse-over-row? (identical? @mouse-over row)]
    [h-box
     :class    "rc-div-table-row"
     :attr     {:on-mouse-over (handler-fn (reset! mouse-over row))
                :on-mouse-out  (handler-fn (reset! mouse-over nil))}
     :children [[label :label (.full-name row) :width (:name col-widths)]
                [label :label (:gender row) :width (:gender col-widths)]
                [label :label (:name (:club row)) :width (:club col-widths)]
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
                               :children [[label :label "Name" :width (:name col-widths)]
                                          [label :label "Gender" :width (:gender col-widths)]
                                          [label :label "Club" :width (:club col-widths)]
                                          [label :label "YOB" :width (:yob col-widths)]
                                          [label :label "Belt" :width (:belt col-widths)]
                                          [label :label "Actions" :width (:actions col-widths)]]]
                              (for [[id row first? last?] (enumerate rows)]
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
                                          "club"))]
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
          [button
           :label "Save"
           :on-click (fn []
                       (dispatch [:add-competitor @competitor])
                       (dispatch [:edit-competitor nil])
                       (reset! new-competitor 
                               (make-competitor "fname" "lname" 
                                                "gender" "yob" "belt" 
                                                "club")))]]]))))

(defn read-csv 
  [input]
  (let [data (map 
               #(clojure.string/split % #",") 
               (clojure.string/split-lines input))
        headers (first data)
        data (rest data)]
    (into [] 
          (for [line data]
              (into {} 
                    (for [[k v] 
                          (map list headers line)]
                      [k (clojure.string/trim v)]))))))

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
        col-widths {:name "15em" :gender "4em" :club "15em" :yob "4em" :belt "4.5em" :actions "4.5em"}]
    (fn []
      [v-box 
       :gap "10px"
       :children 
       [[title 
         :label "Current competitors"]
        [data-table (vals @competitors) col-widths]
        
        [line]
        [add-competitor]
        [load-csv-file]]])))