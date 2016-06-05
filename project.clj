(defproject pi "0.1.0-SNAPSHOT"
  :description "Solutions to Pi tests"
  :url "https://github.com/analyticbastard/pi"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :dependencies [[org.clojure/clojure "1.7.0"]
                 [compojure "1.4.0"]
                 [ring/ring-jetty-adapter "1.4.0"]
                 [ring/ring-json "0.4.0"]
                 ]

  :plugins [[lein-ring "0.9.7"]
            ]

  :ring {:handler pi.server/app}

  :source-paths ["src/main"]

  :test-paths ["src/test"]

  :profiles {:dev {:dependencies [[org.clojure/tools.namespace "0.2.11"]
                                  ]
                   :source-paths ["src/dev/clj"]
                   }}
  )
