(ns udeps.config
  (:require [integrant.core :as ig]
            [clojure.java.io :as io]
            [clojure.edn :as edn]
            [org.httpkit.client :as http]))

(derive :cfg/verbose :cfg/param)

(defn- read-conf [path]
  (if-let [conf (io/resource path)]
    (-> conf
        slurp
        ig/read-string)
    {}))

(defn build []
  (let [default-config (read-conf "udeps-default.edn")
        user-config    (read-conf "udeps.edn")]
    (merge default-config user-config)))

(defmethod ig/init-key :cfg/param [_ data] data)

(defmethod ig/init-key :src/folder [_ path]
  (fn [dep]
    (let [fname     (name dep)
          file-path (str path fname ".edn")]
      (try
        (if-let [fdata (-> file-path slurp edn/read-string)]
          fdata)
        (catch java.io.FileNotFoundException e
          (throw (ex-info (str "File not found at " file-path) {:dep dep})))))))

(defmethod ig/init-key :src/http [_ query-params]
  (let [{:keys [url params]} query-params]
    (fn [dep]
      (let [fn-url   (str url (name dep))
            response (http/get fn-url params)]

        (-> @response :body edn/read-string)))))
