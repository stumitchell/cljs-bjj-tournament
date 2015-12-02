(ns cljs-bjj-tournament.firebase
  (:require [matchbox.core :as m]))

(def root-db (m/connect "https://blinding-torch-9119.firebaseio.com"))

(def comp-name "2015-judo-comp")
(m/auth-anon root-db)

(def comp-db (m/get-in root-db comp-name))