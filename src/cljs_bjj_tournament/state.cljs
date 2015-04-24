(ns cljs-bjj-tournament.state
    (:require-macros [reagent.ratom :refer [reaction]])  
    (:require [cljs-bjj-tournament.model :refer [Competitor]]
              [re-frame.core :refer [register-sub
                                     register-handler
                                     path]]
              [re-frame.db :refer [app-db]]
              [alandipert.storage-atom :refer [local-storage]]))

(def default-state
    {:initialised true
     :competitors
     [(Competitor. "Stuart" "Mitchell" "Male" 1976 "Black")
      (Competitor. "Serge" "Morel" "Male" 1974 "Black")
      (Competitor. "Leon" "Lockheart" "Male" 1978 "White")]
  :matches
  []})

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

(reg-sub-key
    :competitors)

(reg-sub-key
    :matches)
