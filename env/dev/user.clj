(ns user
  (:require [integrant.core :as ig]
            [integrant.repl :refer [clear go halt prep init reset reset-all]]
            [integrant.repl.state :refer [system config]]
            [puget.printer :as puget]
            [clojure.java.io :as io]
            [udeps.config :as uconf]
            [udeps.core :as udeps]))

;; https://github.com/weavejester/integrant-repl
;; Provides worflow function (prep) (init) (go) (reset) (halt)
(integrant.repl/set-prep! #(ig/prep (uconf/build)))
