(ns cljs-bjj-tournament.state
  (:require-macros [reagent.ratom :refer [reaction]])
  (:require [cljs-bjj-tournament.model :refer [make-division
                                               Club
                                               map->Division
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
            [cljs-bjj-tournament.firebase :refer [comp-db]]))

(enable-console-print!)

(def bjj-clubs
  {"ABJJ" (Club. "ABJJ" "AucklandBjj.com"
                    "resources/club_logos/auckland-bjj.png")
   "Tukaha" (Club. "Tukaha" "Tukaha Brazilian Jiu Jitsu"
                      "resources/club_logos/tukaha-bjj.png")
   "Oliver MMA" (Club. "Oliver MMA" "Oliver MMA"
                          "resources/club_logos/oliver-mma.png")
   "DS Team" (Club. "DS Team" "DS Team"
                    "resources/club_logos/DS-team.png")
   "UJC" (Club. "UJC" "University Judo Club"
                "resources/club_logos/UJC.png")
   "City BJJ" (Club. "City BJJ" "City BJJ"
                     "resources/club_logos/city-bjj.png")
   "Zero Gravity" (Club. "Zero Gravity" "Zero Gravity BJJ"
                         "resources/club_logos/zero-gravity.png")
   "Clinch" (Club. "Clinch" "Clinch BJJ"
                   "resources/club_logos/clinch.png")})

(def judo-clubs
  {"ABJJ" (Club. "ABJJ" "AucklandBjj.com"
                    "resources/club_logos/auckland-bjj.png")
   "UJC" (Club. "UJC" "University Judo Club" nil)
   "WJC" (Club. "WJC" "Western Judo Club" nil)
   "NJC" (Club. "NJC" "Nippon Judo Club" nil)
   "HJC" (Club. "HJC" "Howick Judo Club" nil)})


(def bjj-masters-divisions
  (let [divisions  [(make-division "ALL" (constantly true))
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
                                        (#{"M3" "M4"} (.age-div %))
                                        (> 90 (:weight %))))
                   (make-division "Blue Belt M3 M4 - Heavy"
                                  #(and (= (:belt %) "Blue")
                                        (#{"M3" "M4"} (.age-div %))
                                        (< 90 (:weight %))))
                   (make-division "Blue Belt M5"
                                  #(and (= (:belt %) "Blue")
                                        (= (.age-div %) "M5")))]]
      (into (sorted-map) (for [d divisions]
                                       [(:name d) d]))))

(def judo-masters-divisions
  (let [divisions [(make-division "ALL" (constantly true))
                   (make-division "U19" #(#{"U19"} (.age-div %)))
                   (make-division "U29 - Light" #(and
                                                   (#{"U29"} (.age-div %))
                                                   (> 81 (:weight %))))
                   (make-division "U29 - Heavy" #(and
                                                   (#{"U29"} (.age-div %))
                                                   (< 81 (:weight %))))
                   (make-division "U39 - Light" #(and
                                                   (#{"M1" "M2"} (.age-div %))
                                                   (> 81 (:weight %))))
                   (make-division "U39 - Heavy" #(and
                                                   (#{"M1" "M2"} (.age-div %))
                                                   (< 81 (:weight %))))
                   (make-division "U49 - Light" #(and
                                                   (#{"M3" "M4"} (.age-div %))
                                                   (> 81 (:weight %))))
                   (make-division "U49 - Heavy" #(and
                                                   (#{"M3" "M4"} (.age-div %))
                                                   (< 81 (:weight %))))
                   (make-division "U59" #(#{"M5" "M6"} (.age-div %)))
                   (make-division "U60+" #(#{"M7"} (.age-div %)))]]
      (into (sorted-map) (for [d divisions]
                                       [(:name d) d]))))

(def test-state
  {:initialised true
   :page :intro
   :clubs judo-clubs
   ; :competitors competitors-map
   :divisions judo-masters-divisions
   ; :matches [(make-match "ALL" (:guid (first (vals competitors-map)))
   ;                          (:guid (last (vals competitors-map))))]
   })

(def default-persistent-state
  {:clubs {}
   :competitors {}
   :matches []})

(def default-state
  (merge {:initialised true
          :page :intro
          :divisons {}}
         default-persistent-state))

(def persistent-db (atom {}) #_(local-storage
                     (atom {})
                     ::persistent-db))

(defn initialise
  [db [_ new-db]]
  (matchbox/listen-to comp-db :value
                      (fn [[k v]]
                        (dispatch [:sync-db v])))
  (dispatch [:clubs (:clubs test-state)])
  #_(dispatch [:divisions (:divisions test-state)])
  (let [db (-> db
               (merge test-state)
               (merge @persistent-db))]
    ;space for other initialisation
    db))

(defn sync-db
  [db [_ new-db]]
  (merge db default-persistent-state new-db))

(register-handler
  :sync-db
  sync-db)

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
          (matchbox/reset-in! comp-db p result)
          result)))))

(defn register-persistent-sub-key
  [key translate-fn]
  (register-sub
    key
    (fn [db [_]]
      (reaction (->> @db
                     key
                     (map (fn [[k v]] [k (translate-fn v)]))
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

(register-persistent-sub-key :clubs map->Club)

(register-persistent-sub-key :competitors map->Competitor)

(register-persistent-sub-key :clubs map->Club)
