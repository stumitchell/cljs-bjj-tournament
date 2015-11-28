(ns cljs-bjj-tournament.state
  (:require-macros [reagent.ratom :refer [reaction]])
  (:require [cljs-bjj-tournament.model :refer [make-division
                                               Club
                                               map->Competitor
                                               map->Club
                                               map->Match]]
            [re-frame.core :refer [register-sub
                                   register-handler
                                   path
                                   dispatch]]
            [re-frame.db :refer [app-db]]
            [alandipert.storage-atom :refer [local-storage]]
            [matchbox.core :as matchbox]
            [cljs-bjj-tournament.firebase :refer [root-db]]))

(enable-console-print!)

(def test-state
  (let [abjj (Club. "ABJJ" "AucklandBjj.com"
                    "resources/club_logos/auckland-bjj.png")
        tukaha (Club. "Tukaha" "Tukaha Brazilian Jiu Jitsu"
                      "resources/club_logos/tukaha-bjj.png")
        oliver-mma (Club. "Oliver MMA" "Oliver MMA"
                          "resources/club_logos/oliver-mma.png")
        clubs {"ABJJ" abjj
               "Tukaha" tukaha
               "Oliver MMA" oliver-mma
               "DS Team" (Club. "DS Team" "DS Team"
                               "resources/club_logos/DS-team.png")
               "UJC" (Club. "UJC" "University Judo Club"
                            "resources/club_logos/UJC.png")
               "City BJJ" (Club. "City BJJ" "City BJJ"
                                 "resources/club_logos/city-bjj.png")
               "Zero Gravity" (Club. "Zero Gravity" "Zero Gravity BJJ"
                                     "resources/club_logos/zero-gravity.png")
               "Clinch" (Club. "Clinch" "Clinch BJJ"
                               "resources/club_logos/clinch.png")}
        divisions [(make-division "ALL" (constantly true))
                   (make-division "White Belt M1 M2 - Light"
                                  #(and (= (:belt %) "White")
                                        (#{"M1" "M2"} (.age-div %))
                                        (> 74 (:weight %))))
                   (make-division "White Belt M2 M2 - Medium"
                                  #(and (= (:belt %) "White")
                                        (#{"M1" "M2"} (.age-div %))
                                        (> 85 (:weight %))
                                        (< 74 (:weight %))))
                   (make-division "White Belt M2 M2 - Heavy"
                                  #(and (= (:belt %) "White")
                                        (#{"M1" "M2"} (.age-div %))
                                        (< 85 (:weight %))))
                   (make-division "White Belt M3 M4 - Light"
                                  #(and (= (:belt %) "White")
                                        (or
                                          (= (.age-div %) "M3")
                                          (= (.age-div %) "M4"))))
                   (make-division "Blue Belt M1 M2 - Light"
                                  #(and (= (:belt %) "Blue")
                                        (#{"M1" "M2" "M3" "M4"} (.age-div %))
                                        (> 95 (:weight %))))
                   (make-division "Blue Belt M1 M2 - Heavy"
                                  #(and (= (:belt %) "Blue")
                                        (#{"M1" "M2"} (.age-div %))
                                        (< 95 (:weight %))))
                   (make-division "Blue Belt M3 M4 - Light"
                                  #(and (= (:belt %) "Blue")
                                        (or
                                          (= (.age-div %) "M3")
                                          (= (.age-div %) "M4"))
                                        (> 90 (:weight %))))
                   (make-division "Blue Belt M3 M4 - Heavy"
                                  #(and (= (:belt %) "Blue")
                                        (or
                                          (= (.age-div %) "M3")
                                          (= (.age-div %) "M4"))
                                        (< 90 (:weight %))))
                   (make-division "Blue Belt M5"
                                  #(and (= (:belt %) "Blue")
                                        (= (.age-div %) "M5")))]
        divisions (into (sorted-map) (for [d divisions]
                                       [(:name d) d]))]
    {:initialised true
     :page :intro
     :clubs clubs
     ; :competitors competitors-map
     :divisions divisions
     ; :matches [(make-match "ALL" (:guid (first (vals competitors-map)))
     ;                          (:guid (last (vals competitors-map))))]
     }))

(def default-state
  {:initialised true
   :page :intro
   :clubs {}
   :competitors {}
   :divisions {}
   :matches []})

(def persistent-db (atom {}) #_(local-storage
                     (atom {})
                     ::persistent-db))

(defn initialise
  [db [_ new-db]]
  (let [db (if (nil? new-db)
             (do
               (matchbox/deref-in root-db "test-comp"
                                  #(dispatch [:initialise %]))
               (-> db
                   (merge test-state)
                   (merge @persistent-db)))
             (merge db new-db))]
    ;space for other initialisation
    db))

(register-handler
  :initialise
  initialise)

(defn reg-sub-key
  "given a key register and subscribe to it with
  simple getters and setters"
  [key & [default]]
  (register-sub
    key
    (fn
      [db]
      (reaction (get @db key))))
  (register-handler
    key
    (path [key])
    (fn
      [_ [_ value]]
      value))
  (when (some? default)
    (swap! app-db assoc key default)))

(reg-sub-key :initialised)

(reg-sub-key :page)

(reg-sub-key :edit-competitor)

(reg-sub-key :divisions)

(defn persistent-path
  "This middleware will persist the changes in the handler into
  local-storage"
  [p]
  (fn middleware
    [handler]
    ((path p)
      (fn new-handler
        [db v]
        (let [result (handler db v)]
          #_(swap! persistent-db assoc-in p result)
          (matchbox/reset-in! root-db (concat [:test-comp] p) result)
          result)))))

(defn register-persistent-sub-key
  [key translate-fn]
  (register-sub
    key
    (fn [db [_]]
      (reaction (->> @db
                     key
                     (map (fn [[k v]] [(name k) (translate-fn v)]))
                     (into {})))))
  (register-handler
    key
    (persistent-path [key])
    (fn [_ [_ value]]
      value)))

(register-persistent-sub-key :matches map->Match)
(register-sub
  :matches
  (fn [db [_]]
    (reaction (->> @db
                   :matches
                   (map map->Match)))))

(register-persistent-sub-key :competitors map->Competitor)

(register-persistent-sub-key :clubs map->Club)
