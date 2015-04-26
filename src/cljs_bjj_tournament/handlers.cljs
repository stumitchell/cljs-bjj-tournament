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

