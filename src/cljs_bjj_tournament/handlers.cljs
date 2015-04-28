(ns cljs-bjj-tournament.handlers
  (:require [re-frame.core :refer [register-handler
                                   path]]))

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
  (path [:competitors])
  (fn [old_competitors [_ competitors]]
    (into {}
          (for [c competitors]
            [(:guid c) c]))))

(register-handler
  :delete-competitor
  (path [:competitors])
  (fn [competitors [_ id]]
       (dissoc competitors id)))


