(ns cljs-bjj-tournament.handlers
  (:require [re-frame.core :refer [register-handler
                                   path]]
            [re-frame.db :refer [app-db]]
            [cljs-bjj-tournament.model :refer [make-competitor
                                               make-club]]))

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

(defn make-competitor-from-map
  [attrs]
  (let [clubs (:clubs @app-db)
        fname (first (clojure.string/split (attrs "Name") #" "))
        lname (last (clojure.string/split (attrs "Name") #" "))
        gender "Male"
        yob (attrs "YOB")
        belt (attrs "Belt")
        club-name (attrs "Club")
        club (if (contains? clubs club-name)
               (clubs club-name)
               (let 
                 [club (make-club club-name)]
                 (swap! app-db assoc club club-name)
                 club))]
    (make-competitor fname lname gender yob belt club)))

(register-handler
  :add-competitors
  (path [:competitors])
  (fn [old_competitors [_ competitors]]
    (let [competitors (map make-competitor-from-map competitors)]
      (into {}
            (for [c competitors]
              [(:guid c) c])))))

(register-handler
  :delete-competitor
  (path [:competitors])
  (fn [competitors [_ id]]
    (dissoc competitors id)))


