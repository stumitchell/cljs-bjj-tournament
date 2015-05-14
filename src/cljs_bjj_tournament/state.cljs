(ns cljs-bjj-tournament.state
  (:require-macros [reagent.ratom :refer [reaction]])  
  (:require [cljs-bjj-tournament.model :refer [make-competitor
                                               make-division
                                               make-match
                                               Club
                                               make-competitor-from-map
                                               link-competitors-with-clubs]]
            [re-frame.core :refer [register-sub
                                   register-handler
                                   path]]
            [re-frame.db :refer [app-db]]
            [alandipert.storage-atom :refer [local-storage]]
            [cljs-bjj-tournament.competitors-csv :refer [competitors]]))

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
               "Oliver MMA" oliver-mma}
        competitors (map make-competitor-from-map competitors)
        [competitors clubs] (link-competitors-with-clubs competitors clubs)
        competitors-map (into {}
                              (for [c competitors]
                                [(:guid c) c]))
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
     :page :matches
     :clubs clubs
     :competitors competitors-map
     :divisions divisions
     :matches [(make-match "ALL" (:guid (first (vals competitors-map))) 
                              (:guid (last (vals competitors-map))))]}))

(def default-state
  {:initialised true
   :page :intro
   :clubs {}
   :competitors {}
   :divisions {}
   :matches []})

(def persistent-db (local-storage 
                     (atom {})
                     ::persistent-db))

(defn initialise 
  [db]
  (let [db (-> db
               (merge test-state)
               (merge @persistent-db))]
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

(reg-sub-key :competitors)

(reg-sub-key :matches)

(reg-sub-key :clubs)

(reg-sub-key :edit-competitor)

(reg-sub-key :divisions)
