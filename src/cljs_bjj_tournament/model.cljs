(ns cljs-bjj-tournament.model
  (:require [cljs-uuid-utils.core :as uuid]
            [re-frame.db :refer [app-db]]))

(defrecord Division
  [guid name filter-fn])

(defn make-division 
  [name filter-fn]
  (Division. (uuid/make-random-uuid) name filter-fn))

(defrecord Club
  [name full-name image-url])

(defn make-club
  ([club-name]
   (make-club club-name club-name nil))
  ([club-name full-name]
   (make-club club-name full-name nil))
  ([club-name full-name image-url]
   (Club. club-name full-name image-url)))

(defn age-division
  [c]
  (let [age (- 2014 (:yob c))]
    (cond 
      (< age 35) "M1"
      (< age 40) "M2"
      (< age 45) "M3"
      (< age 50) "M4"
      :else "M5")))

(defrecord Competitor
  [guid fname lname gender yob belt club weight]
  Object
  (full-name [_] (str fname " " lname))
  (full-name-club [this] (str (.full-name this) 
                              " (" (:name club) ")"
                              " " yob
                              " -- " weight "kg"))
  (url-string [this p] (str p "Name=" (.full-name this) "&" 
                            p "Team=" (:full-name club) "&"
                            p "DefaultLogoPath=" (:image-url club) "&"
                            p "DefaultLogoPath=" (:image-url club) "&"))
  (age-div [this] (age-division this)))

(defn make-competitor
  [fname lname gender yob belt club weight]
  (Competitor. (uuid/make-random-uuid) fname lname gender (float yob) belt club (float weight)))

(defn make-competitor-from-map
  [attrs]
  (let [clubs (:clubs @app-db)
        fname (first (clojure.string/split (attrs "Name") #" "))
        lname (last (clojure.string/split (attrs "Name") #" "))
        gender "Male"
        yob (attrs "YOB")
        belt (attrs "Belt")
        club (attrs "Club")
        weight (attrs "Weight")]
    (make-competitor fname lname gender yob belt club weight)))

(defn link-competitors-with-clubs 
  [competitors clubs]
  (let [;find all clubs that are mentioned
        ;find the new ones
        new-clubs (filter (complement #(contains? clubs %)) 
                          (map :club competitors))
        clubs (into clubs (for [c (map make-club new-clubs)]
                            [(:name c) c]))
        competitors (map #(assoc % :club (clubs (:club %))) competitors)]
    [competitors clubs]))

(defn read-csv 
  [input]
  (let [data (map 
               #(clojure.string/split % #",") 
               (clojure.string/split-lines input))
        headers (first data)
        data (rest data)]
    (into [] 
          (for [line data]
            (into {} 
                  (for [[k v] 
                        (map list headers line)]
                    [k (clojure.string/trim v)]))))))
