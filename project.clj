(defproject cljs-bjj-tournament "0.1.0-SNAPSHOT"
  :description "BJJ tournament planning as a single page app"
  :url "https://github.com/stumitchell/cljs-bjj-tournament"

  :dependencies [[org.clojure/clojure         "1.7.0"]
                 [org.clojure/clojurescript   "1.7.170"]
                 [reagent                     "0.5.1"]
                 [re-com "0.7.0-alpha2"]
                 [re-frame "0.5.0"]
                 [alandipert/storage-atom "1.2.4"]
                 [com.lucasbradstreet/cljs-uuid-utils "1.0.1"]]

  :plugins [[lein-cljsbuild "1.1.1"]
            [lein-figwheel    "0.5.0-1"]
            [lein-ancient     "0.6.7"]]

  :source-paths ["src"]

  :clean-targets ^{:protect false} ["resources/public/js/compiled" "target"]

  :figwheel {:css-dirs ["resources/public/css"] ;; watch and update CSS
             }

  :cljsbuild {:builds [{:id "dev"
                        :source-paths ["src"]
                        :figwheel  {:on-jsload "cljs-bjj-tournament.core/mount-gui"}
                        :compiler {
                                   :main cljs-bjj-tournament.core
                                   :asset-path "js/compiled/out"
                                   :output-to "resources/public/js/compiled/cljs_bjj_tournament.js"
                                   :output-dir "resources/public/js/compiled/out"
                                   :optimizations :none
                                   :cache-analysis true
                                   :source-map true}}
                       {:id "release"
                        :source-paths ["src"]
                        :compiler {
                                   :main cljs-bjj-tournament.core
                                   :asset-path "js/compiled/out"
                                   :output-to "resources/public/js/compiled/cljs_bjj_tournament.js"
                                   :output-dir "resources/public/js/compiled/out"
                                   :optimizations :advanced
                                   :pretty-print false}}]}

  :aliases          {;; *** DEMO ***
                     "run" ["cljsbuild" "once" "dev"]

                     "debug" ["figwheel" "dev"]}

)
