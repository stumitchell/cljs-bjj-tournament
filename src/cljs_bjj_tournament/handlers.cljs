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
    (conj matches (make-match division p1 p2 nil))))

(register-handler
  :clear-matches
  ;deletes all the matches in a division
  (persistent-path [:matches])
  (fn [matches [_ division]]
    (let [div-name (:name division)]
      (if division
        (remove #(= div-name (:division %)) matches)
        []))))

(defn create-round
  [round-num divison match-offset previous-round]
  (let [previous-matches (->> previous-round
                              (partition 2))
        matches (map-indexed
                  (fn [i [m1 m2]]
                    (let [p1 (if (:p2 m1)
                               (str "Winner " (:match-num m1))
                               (:p1 m1))
                          p2 (if (:p2 m2)
                               (str "Winner " (:match-num m2))
                               (:p1 m2))
                          leaf1 (:guid m1)
                          leaf2 (:guid m2)]
                      (make-match (:name divison)
                                  p1
                                  p2
                                  (+ 1 match-offset i)
                                  leaf1
                                  leaf2)))
                  previous-matches)]
    matches))

(defn create-other-rounds
  [num-rounds division match-offset previous-round]
   (let [next-round (create-round (dec num-rounds)
                                  division
                                  match-offset
                                  previous-round)]
     (if (= num-rounds 1)
       next-round
       (concat
         next-round
         (create-other-rounds (dec num-rounds)
                              division
                              (+ match-offset (count next-round))
                              next-round)))))

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
                 num-rounds (-> num-players
                                (->> (.log js/Math))
                                (/ (.log js/Math 2))
                                (->> (.ceil js/Math)))
                 upper-division-size (.pow js/Math 2 num-rounds)
                 remainder (-> upper-division-size
                               (- num-players)
                               (->> (- num-players)))
                 partitioned-players (->> players
                                          (take remainder)
                                          (partition 2 2 nil))
                 byes (->> players
                          (drop remainder)
                          (map (fn [p] [p nil])))
                 first-round-matches (->> (concat partitioned-players byes)
                              (map-indexed (fn [i [p1 p2]]
                                     (make-match (:name division)
                                                 (:guid p1)
                                                 (:guid p2)
                                                 (inc i))))
                              (remove nil?))
                 other-rounds (create-other-rounds
                                num-rounds division
                                (count first-round-matches)
                                first-round-matches)
                 all-rounds (concat first-round-matches other-rounds)]
                (concat all-rounds)
                ))))

(register-handler
  :match-result
  (persistent-path [:matches])
  (fn [matches [_ match-guid winner-guid]]
    (-> matches
      (->> (map (fn [m]
                  (if (= (:leaf1 m)
                         match-guid)
                    (assoc m :p1 winner-guid)
                    m)))
           (map (fn [m]
                  (if (= (:leaf2 m)
                         match-guid)
                    (assoc m :p2 winner-guid)
                    m)))))))

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
    (print competitor)
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



