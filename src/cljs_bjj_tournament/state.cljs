(ns cljs-bjj-tournament.state
  (:require-macros [reagent.ratom :refer [reaction]])  
  (:require [cljs-bjj-tournament.model :refer [make-competitor
                                               make-division
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
        stu (make-competitor "Stuart" "Mitchell" "Male" "1976" "Black" abjj)
        competitors (map make-competitor-from-map competitors)
        [competitors clubs] (link-competitors-with-clubs competitors clubs)
        competitors-map (into {}
                              (for [c competitors]
                                [(:guid c) c]))
        divisions {"ALL" (make-division "ALL" (constantly true))
                   "WhiteM1" (make-division "White Belt M1" 
                                            #(and (= (:belt %) "White")
                                                  (= (.age-div %) "M1")))
                   "WhiteM2" (make-division "White Belt M2" 
                                            #(and (= (:belt %) "White")
                                                  (= (.age-div %) "M2")))
                   "WhiteM3M4" (make-division "White Belt M3 M4" 
                                              #(and (= (:belt %) "White")
                                                    (or 
                                                      (= (.age-div %) "M3")
                                                      (= (.age-div %) "M4"))))
                   "BlueM1M2" (make-division "Blue Belt M1 M2" 
                                              #(and (= (:belt %) "Blue")
                                                    (or 
                                                      (= (.age-div %) "M1")
                                                      (= (.age-div %) "M2"))))
                   "BlueM3M4" (make-division "Blue Belt M3 M4" 
                                              #(and (= (:belt %) "Blue")
                                                    (or 
                                                      (= (.age-div %) "M3")
                                                      (= (.age-div %) "M4"))))
                   "BlueM5" (make-division "Blue Belt M5" 
                                         #(and (= (:belt %) "Blue")
                                               (= (.age-div %) "M5")))}]
    {:initialised true
     :page :matches
     :clubs clubs
     :competitors competitors-map
     :divisions divisions
     :matches []}))

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
