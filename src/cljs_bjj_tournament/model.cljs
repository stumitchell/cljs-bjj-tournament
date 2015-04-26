(ns cljs-bjj-tournament.model)

(defrecord Club
           [name full-name image-url])

(defrecord Competitor
           [fname lname gender yob belt club]
           Object
           (full-name [_] (str fname " " lname))
           (full-name-club [this] (str (.full-name this) 
                                    " (" (:name club) ")"))
           (url-string [this p] (str p "Name=" (.full-name this) "&" 
                                     p "Team=" (:name club) "&")))