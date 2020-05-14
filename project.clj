(defproject lambda "0.1.0-SNAPSHOT"
  :license "MIT"
  :min-lein-version "2.9.1"
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [org.clojure/clojurescript "1.10.597"]
                 [org.clojure/core.async  "0.4.500"]
                 [reagent "0.9.1"]
                 [instaparse "1.4.10"]
                 [cljsjs/semantic-ui-react "0.88.1-0"]]
  :plugins [[lein-figwheel "0.5.19"]
            [lein-cljsbuild "1.1.7" :exclusions [[org.clojure/clojure]]]]
  :source-paths ["src"]
  :cljsbuild
  {:builds
   [{:id           "dev"
     :source-paths ["src"]
     :figwheel {:on-jsload "lambda.core/on-js-reload"
                :open-urls ["http://localhost:3449/index.html"]}
     :compiler {:main                 lambda.core
                :asset-path           "js/compiled/out"
                :output-to            "resources/public/js/compiled/lambda.js"
                :output-dir           "resources/public/js/compiled/out"
                :source-map-timestamp true
                :preloads             [devtools.preload]}}
    {:id           "min"
     :source-paths ["src"]
     :compiler     {:output-to     "resources/public/js/compiled/lambda.js"
                    :main          lambda.core
                    :optimizations :advanced
                    :pretty-print  false}}]}
  :figwheel
  {:css-dirs ["resources/public/css"]
   :nrepl-port 7888}

  :profiles
  {:dev {:dependencies  [[binaryage/devtools "0.9.10"]
                         [figwheel-sidecar "0.5.19"]
                         [cider/piggieback "0.4.1"]
                         [nrepl "0.7.0-alpha3"]]
         :plugins [[com.jakemccrary/lein-test-refresh "0.24.1"]
                   ;; [venantius/ultra "0.6.0"]
                   ]
         :repl-options {:nrepl-middleware [cider.piggieback/wrap-cljs-repl]}
         :source-paths  ["src" "dev" "test"]
         :clean-targets ^{:protect false}
         ["resources/public/js/compiled" :target-path]}

   :kaocha {:dependencies [[lambdaisland/kaocha "1.0-612"]
                           [lambdaisland/kaocha-cljs "0.0-71"]]}}

  :aliases {"kaocha" ["with-profile" "+kaocha" "run"
                      "-m" "kaocha.runner" "unit-cljs"]})
