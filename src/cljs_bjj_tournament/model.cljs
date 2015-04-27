(ns cljs-bjj-tournament.model
  (:require [cljs-uuid-utils.core :as uuid]))

(defrecord Club
           [name full-name image-url])

(defrecord Competitor
           [guid fname lname gender yob belt club]
           Object
           (full-name [_] (str fname " " lname))
           (full-name-club [this] (str (.full-name this) 
                                    " (" (:name club) ")"))
           (url-string [this p] (str p "Name=" (.full-name this) "&" 
                                     p "Team=" (:name club) "&")))

(defn make-competitor
  [fname lname gender yob belt club]
  (Competitor. (uuid/make-random-uuid) fname lname gender yob belt club))