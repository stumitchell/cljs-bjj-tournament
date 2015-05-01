(ns cljs-bjj-tournament.model
  (:require [cljs-uuid-utils.core :as uuid]
            [re-frame.db :refer [app-db]]))

(defrecord Club
  [name full-name image-url])

(defn make-club
  ([club-name]
   (make-club club-name club-name nil))
  ([club-name full-name]
   (make-club club-name full-name nil))
  ([club-name full-name image-url]
   (Club. club-name full-name image-url)))

(defrecord Competitor
  [guid fname lname gender yob belt club]
  Object
  (full-name [_] (str fname " " lname))
  (full-name-club [this] (str (.full-name this) 
                              " (" (:name club) ")"))
  (url-string [this p] (str p "Name=" (.full-name this) "&" 
                            p "Team=" (:full-name club) "&"
                            p "DefaultLogoPath=" (:image-url club) "&"
                            p "DefaultLogoPath=" (:image-url club) "&")))

(defn make-competitor
  [fname lname gender yob belt club]
  (Competitor. (uuid/make-random-uuid) fname lname gender yob belt club))