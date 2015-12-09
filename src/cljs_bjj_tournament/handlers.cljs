(ns cljs-bjj-tournament.handlers
  (:require [re-frame.core :refer [register-handler
                                   path
                                   dispatch]]
            [re-frame.db :refer [app-db]]
            [cljs-bjj-tournament.model :refer [make-competitor
                                               map->Competitor
                                               make-club
                                               make-match
                                               make-competitor-from-map
                                               link-competitors-with-clubs]]
            [cljs-bjj-tournament.state :refer [persistent-path]]
            [lonocloud.synthread :as ->]))

(register-handler
  :add-match
  ;adds a match to the db
  (persistent-path [:matches])
  (fn [matches [_ division p1 p2]]
    (conj matches (make-match division p1 p2))))

(register-handler
  :auto-create-matches
  ;auto creates matches in the db
  (persistent-path [:matches])
  (fn [matches [_ division sort-button]]
    (-> matches
        (->/let [sort-fn (case sort-button
                           :name #(.full-name %)
                           #(- (float (sort-button %))))
                 players (->> @app-db
                              :competitors
                              vals
                              (sort-by sort-fn)
                              (filter #(.in-division? division %)))
                 num-players (count players)
                 upper-division-size (-> num-players
                                         (->> (.log js/Math))
                                         (/ (.log js/Math 2))
                                         int
                                         inc
                                         (->> (.pow js/Math 2)))
                 remainder (-> upper-division-size
                               (- num-players)
                               (->> (- num-players)))
                 partitioned-players (->> players
                                          (take remainder)
                                          (partition 2 2 nil))
                 byes (->> players
                          (drop remainder)
                          (map (fn [p] [p nil])))
                 matches (->> (concat partitioned-players byes)
                              (map (fn [[p1 p2]]
                                     (make-match (:name division)
                                                 (:guid p1)
                                                 (:guid p2))))
                              (remove nil?))]
                (concat matches)))))

(register-handler
  :move-competitor-up
  (persistent-path :competitors)
  (fn [competitors [_ i]]
    (let [j (dec i)
          org (competitors i)
          new (competitors j)]
      (-> competitors
          (assoc i new)
          (assoc j org)))))

(register-handler
  :move-competitor-down
  (persistent-path :competitors)
  (fn [competitors [_ i]]
    (let [j (inc i)
          org (competitors i)
          new (competitors j)]
      (-> competitors
          (assoc i new)
          (assoc j org)))))

(register-handler
  :add-competitor
  (persistent-path [:competitors])
  (fn [competitors [_ competitor]]
    (assoc competitors (:guid competitor) competitor)))

(register-handler
  :add-competitors
  (persistent-path [:competitors])
  (fn [competitors [_ competitors]]
    (let [competitors (map make-competitor-from-map competitors)
          [competitors clubs] (link-competitors-with-clubs
                                competitors (:clubs @app-db))
          competitors-map (into {}
                                (for [c competitors]
                                  [(:guid c) c]))]
      ;add the competitors and delete the matches
      (dispatch [:clubs clubs])
      (dispatch [:matches []])
      competitors-map)))

(register-handler
  :delete-competitor
  (persistent-path [:competitors])
  (fn [competitors [_ id]]
    (dissoc competitors id)))

(register-handler
  :delete-match
  (persistent-path [:matches])
  (fn [matches [_ id]]
    (->> matches
         (remove #(= id (:guid %)))
         vec)))



