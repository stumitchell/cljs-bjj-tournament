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
            [reagent.ratom :as ratom :refer-macros [reaction]]
            [cljs-bjj-tournament.model :refer [make-competitor
                                               read-csv]]))
(defn input-field
  [row col-widths field-key editing? temp-row]
  (if editing?
    [input-text
     :model (field-key @temp-row)
     :width (field-key col-widths)
     :on-change #(swap! temp-row assoc field-key %)]
    [label :label (field-key row) :width (field-key col-widths)]))

(defn editing-buttons
  [id row editing-row mouse-over col-widths temp-row]
  (let [editing? (= @editing-row id)
        mouse-over-row? (= @mouse-over row)]
    [h-box
     :gap      "2px"
     :width    (:actions col-widths)
     :align    :center
     :children [[row-button
                 :md-icon-name    "zmdi-edit"
                 :mouse-over-row? mouse-over-row?
                 :tooltip         "Edit this line"
                 :on-click        (if editing?
                                    (handler-fn
                                      (dispatch [:add-competitor @temp-row])
                                      (reset! editing-row nil))
                                    #(reset! editing-row id))]
                [row-button
                 :md-icon-name    "zmdi-delete"
                 :mouse-over-row? mouse-over-row?
                 :tooltip         "Delete this line"
                 :on-click        #(dispatch [:delete-competitor id])]]]))

(defn data-row
  [& {:keys [id row first?
                   last? col-widths mouse-over
                   click-msg editing-row]}]
      (let [editing? (= @editing-row id)
            temp-row (reagent/atom row)]
        [h-box
         :class    "rc-div-table-row"
         :attr     {:on-mouse-over (handler-fn (reset! mouse-over row))
                    :on-mouse-out  (handler-fn (reset! mouse-over nil))}
         :children [[input-field row col-widths :fname editing? temp-row]
                    [input-field row col-widths :lname editing? temp-row]
                    [input-field row col-widths :gender editing? temp-row]
                    [input-field row col-widths :club-name editing? temp-row]
                    [input-field row col-widths :yob editing? temp-row]
                    [input-field row col-widths :weight editing? temp-row]
                    [input-field row col-widths :belt editing? temp-row]
                    [editing-buttons id row editing-row mouse-over col-widths
                     temp-row]]]))

(defn data-table
  []
  (let [mouse-over (reagent/atom nil)
        click-msg  (reagent/atom "")
        sort-key   (reagent/atom :fname)
        editing-row (reagent/atom nil)
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
       :children
       [[v-box
         :class    "rc-div-table"
         :children
         [^{:key "0"}
          [h-box
           :class    "rc-div-table-header"
           :children
           [[label-fn "First Name" :fname col-widths]
            [label-fn "Last Name" :lname col-widths]
            [label-fn "Gender" :gender col-widths]
            [label-fn "Club" :club-name col-widths]
            [label-fn "YOB" :yob col-widths]
            [label-fn "Weight" :weight col-widths]
            [label-fn "Belt" :belt col-widths]
            [label-fn "Actions" :actions col-widths]]]
          (for [[id row first? last?] (enumerate
                                        (sort-by
                                          (sort-fn @sort-key)
                                          rows))]
            ^{:key id} [data-row
                        :id (:guid row)
                        :row row
                        :first?  first?
                        :last? last?
                        :col-widths col-widths
                        :mouse-over mouse-over
                        :click-msg click-msg
                        :editing-row editing-row])]]]])))

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
                    :info (if (string? info)
                            [:div info]
                            info)]]))]))

(defn competitor-field
  [name field competitor]
  [h-box
   :gap "10px"
   :children
   [[field-label name]
    [input-text
     :model (str (field @competitor))
     :on-change #(swap! competitor assoc field %)]]])

(defn add-competitor
  []
  (let [edit-competitor (subscribe [:edit-competitor])
        competitors (subscribe [:competitors])
        new-competitor (reagent/atom
                         (make-competitor "fname" "lname"
                                          "gender" "yob" "belt"
                                          "club-name" "0"))]
    (fn []
      (let [competitor (if-not (nil? @edit-competitor)
                         (reagent/atom (@competitors @edit-competitor))
                         new-competitor)]
        [v-box
         :gap "5px"
         :children
         [
          ; [title
          ;  :label (if @edit-competitor
          ;           "Edit Competitor"
          ;           "Add competitor ")]
          ; [competitor-field "First Name" :fname competitor]
          ; [competitor-field "Last Name" :lname competitor]
          ; [competitor-field "Gender" :gender competitor]
          ; [competitor-field "YOB" :yob competitor]
          ; [competitor-field "Weight" :weight competitor]
          ; [competitor-field "Belt" :belt competitor]
          ; [competitor-field "Club" :club-name competitor]
          [button
           :label "Add Competitor"
           :on-click (fn []
                       (dispatch [:add-competitor @competitor])
                       (dispatch [:edit-competitor nil])
                       (reset! new-competitor
                               (make-competitor "fname" "lname"
                                                "gender" "yob" "belt"
                                                "club-name" "0")))]]]))))

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
        col-widths {:fname "12em" :lname "12em" :gender "6em" :club-name "10em" :yob "6em"
                    :weight "6em" :belt "6em" :actions "6em"}]
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