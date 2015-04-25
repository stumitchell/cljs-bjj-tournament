(ns cljs-bjj-tournament.state
    (:require-macros [reagent.ratom :refer [reaction]])  
    (:require [cljs-bjj-tournament.model :refer [Competitor
                                                 Club]]
              [re-frame.core :refer [register-sub
                                     register-handler
                                     path]]
              [re-frame.db :refer [app-db]]
              [alandipert.storage-atom :refer [local-storage]]))

(def default-state
    (let [abjj (Club. "ABJJ" "AucklandBjj.com" 
                        "http://1.bp.blogspot.com/-F0suapvfqS0/VGE1FCmNEiI/AAAAAAAADFY/2s584eV3Zyw/s1600/Auckland_BJJ_logo.png")]
        {:initialised true
         :page :intro
         :clubs [:abjj]
         :competitors
         [(Competitor. "Stuart" "Mitchell" "Male" 1976 "Black" abjj)
          (Competitor. "Serge" "Morel" "Male" 1974 "Black" abjj)
          (Competitor. "Leon" "Lockheart" "Male" 1978 "White" abjj )]
         :matches []}))

(def persistent-db (local-storage 
                     (atom {})
                     ::persistent-db))

(defn initialise 
  [db]
  (let [db (-> db
               (merge default-state)
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
