(ns cljs-bjj-tournament.handlers
    (:require [re-frame.core :refer [register-handler
                                     path]]))

(register-handler
    :add-match
    ;adds a match to the db
    (path [:matches])
    (fn [matches [_ c1 c2]]
        (conj matches [c1 c2])))
        
    