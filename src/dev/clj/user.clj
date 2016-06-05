(ns user
  (require [ring.adapter.jetty :as jetty]
           [pi.server :as server]
           [clojure.tools.namespace.repl :refer [refresh]]))

(comment
  (def s (jetty/run-jetty server/app {:port 3000 :join? false}))

  (.stop s)
  (.start s)
  )

(comment
  (refresh)
  )