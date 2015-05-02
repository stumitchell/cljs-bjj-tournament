(ns cljs-bjj-tournament.handlers
  (:require [re-frame.core :refer [register-handler
                                   path]]
            [re-frame.db :refer [app-db]]
            [cljs-bjj-tournament.model :refer [make-competitor
                                               make-club
                                               make-competitor-from-map
                                               link-competitors-with-clubs]]))

(register-handler
  :add-match
  ;adds a match to the db
  (path [:matches])
  (fn [matches [_ c1 c2]]
    (conj matches [c1 c2])))

(register-handler 
  :move-competitor-up 
  (path :competitors)
  (fn [competitors [_ i]]
    (let [j (dec i)
          org (competitors i)
          new (competitors j)]
      (-> competitors
          (assoc i new)
          (assoc j org)))))

(register-handler 
  :move-competitor-down 
  (path :competitors)
  (fn [competitors [_ i]]
    (let [j (inc i)
          org (competitors i)
          new (competitors j)]
      (-> competitors
          (assoc i new)
          (assoc j org)))))

(register-handler
  :add-competitor
  (path [:competitors])
  (fn [competitors [_ competitor]]
    (assoc competitors (:guid competitor) competitor)))

(register-handler
  :add-competitors
  (fn [db [_ competitors]]
    (let [competitors (map make-competitor-from-map competitors)
          [competitors clubs] (link-competitors-with-clubs 
                                    competitors (:clubs db))
          competitors-map (into {}
                                (for [c competitors]
                                  [(:guid c) c]))]
      ;add the competitors and delete the matches
      (-> db
          (assoc :competitors competitors-map)
          (assoc :clubs clubs)
          (assoc :matches [])))))

(register-handler
  :delete-competitor
  (path [:competitors])
  (fn [competitors [_ id]]
    (dissoc competitors id)))


