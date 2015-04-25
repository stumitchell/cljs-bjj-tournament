(ns cljs-bjj-tournament.model)

(defrecord Club
           [name full-name image-url])

(defrecord Competitor
           [fname lname gender yob belt club]
           Object
           (full-name [_] (str fname " " lname ".." ))
           (full-name-club [_] (str fname " " lname 
                                    " (" ")")))