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
  (fn [matches [_ division]]
    (-> matches
        (->/let [players (->> @app-db
                              :competitors
                              vals
                              (map map->Competitor)
                              (filter #(.in-division? division %))
                              (partition 2)
                              )
                 matches (->> players
                              (map (fn [[p1 p2]]
                                     (when (some? p2)
                                       (make-match (:name division)
                                                   (:guid p1)
                                                   (:guid p2)))))
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



