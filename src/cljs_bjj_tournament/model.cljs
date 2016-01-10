(ns cljs-bjj-tournament.model
  (:require [cljs.reader]
            [cljs-uuid-utils.core :as uuid]
            [re-frame.db :refer [app-db]]))

(defrecord Match
  [guid division p1 p2 winner])


(defn make-match
  [division p1 p2]
  (Match. (-> (uuid/make-random-uuid)
              str
              keyword)
          division p1 p2 nil))

(defrecord Division
  [guid name age-divs belts min-weight max-weight]
  Object
  (in-division? [_ c]
                (and (if age-divs
                       ((set age-divs) (.age-div c))
                       true)
                     (if belts
                       ((set belts) (:belt c))
                       true)
                     (< min-weight (:weight c))
                     (>= max-weight (:weight c)))))

(defn make-division
  [& {:keys [name age-divs belts min-weight max-weight]
      :or {min-weight 0
           max-weight 999}}]
  (Division. (-> (uuid/make-random-uuid)
                 str
                 keyword)
             name age-divs belts min-weight max-weight))

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
  (let [age (- 2015 (:yob c))]
    (cond
      (< age 20) "U19"
      (< age 30) "U29"
      (< age 35) "M1"
      (< age 40) "M2"
      (< age 45) "M3"
      (< age 50) "M4"
      (< age 55) "M5"
      (< age 60) "M6"
      :else "M7")))

(defrecord Competitor
  [guid fname lname gender yob belt club-name weight]
  Object
  (get-club [_] (get-in @app-db [:clubs (keyword club-name)]))
  (full-name [_] (str fname " " lname))
  (full-name-club [this] (str (.full-name this)
                              " (" club-name ")"
                              " " yob
                              " -- " weight "kg"))
  (url-string [this p] (str p "Name=" (.full-name this) "&"
                            p "Team=" (:full-name (.get-club this)) "&"
                            p "DefaultLogoPath=" (:image-url (.get-club this)) "&"
                            p "DefaultLogoPath=" (:image-url (.get-club this)) "&"))
  (age-div [this] (age-division this)))

(defn make-competitor
  [fname lname gender yob belt club-name weight]
  (Competitor. (-> (uuid/make-random-uuid)
                   str
                   keyword)
               fname lname gender (float yob) belt club-name (float weight)))

(defn make-competitor-from-map
  [attrs]
  (let [fname (first (clojure.string/split (attrs "Name") #" "))
        lname (last (clojure.string/split (attrs "Name") #" "))
        gender (attrs "Gender")
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
                          (map :club-name competitors))
        clubs (into clubs (for [c (map make-club new-clubs)]
                            [(:name c) c]))
        ]
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

(cljs.reader/register-tag-parser!
  "cljs-bjj-tournament.model.Match" map->Match)
(cljs.reader/register-tag-parser!
  "cljs-bjj-tournament.model.Competitor" map->Competitor)
(cljs.reader/register-tag-parser!
  "cljs-bjj-tournament.model.Club" map->Club)
