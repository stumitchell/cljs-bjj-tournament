(defproject cljs-bjj-tournament "0.1.0-SNAPSHOT"
  :description "BJJ tournament planning as a single page app"
  :url "https://github.com/stumitchell/cljs-bjj-tournament"

  :dependencies [[org.clojure/clojure         "1.6.0"]
                 [org.clojure/clojurescript   "0.0-3169"]
                 [reagent                     "0.5.0"]
                 [re-com "0.5.2"]]

  :node-dependencies [[source-map-support "0.2.8"]]

  :plugins [[lein-cljsbuild "1.0.4"]
            [lein-npm "0.4.0"]]

  :source-paths ["src" "target/classes"]

  :clean-targets ["out" "out-adv"]

  :cljsbuild {
    :builds [{:id "dev"
              :source-paths ["src"]
              :compiler {
                :main cljs-bjj-tournament.core
                :output-to "out/cljs_bjj_tournament.js"
                :output-dir "out"
                :optimizations :none
                :cache-analysis true
                :source-map true}}
             {:id "release"
              :source-paths ["src"]
              :compiler {
                :main cljs-bjj-tournament.core
                :output-to "out-adv/cljs_bjj_tournament.min.js"
                :output-dir "out-adv"
                :optimizations :advanced
                :pretty-print false}}]})
