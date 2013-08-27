(ns voitto.model
  (use [clj-time.core :only [local-date]]))

(defonce id-counter (atom 0))
(defn id! [] (swap! id-counter inc))

(def example-events
  [{:id (id!)
    :date (local-date 2013 8 26)
    :comment "Opening the books"
    :entries [{:account :openings
               :cents -100000}
              {:account :savings
               :cents 100000}]}
   {:id (id!)
    :date (local-date 2013 8 26)
    :comment "Pocket money"
    :entries [{:account :savings
               :cents -25000}
              {:account :cash
               :cents 25000}]}
   {:id (id!)
    :date (local-date 2013 8 26)
    :comment "Buy ice cream"
    :other-party "Tampereen jäätelötehdas"
    :entries [{:account :cash
               :cents -300}
              {:account :ice-cream
               :cents 300}]}
   {:id (id!)
    :date (local-date 2013 8 27)
    :comment "Example unbalanced event"
    :entries [{:account :cash
               :cents -20}]}])

(defn process-entry [bals {:keys [account cents]}]
  (merge-with + bals (assoc {} account cents)))

(defn process-event [bals {:keys [entries]}]
  (reduce process-entry bals entries))

(defn balances [events]
  (reduce process-event {} events))

(defn balanced-event? [event]
  (= 0 (reduce + 0 (map :cents (:entries event)))))

(defn simple-event? [event]
  (and
    (balanced-event? event)
    (= 2 (count (:entries event)))))

(defn split-event? [event]
  (and
    (balanced-event? event)
    (> 2 (count (:entries event)))))

(defn event-total [event]
  (->> (:entries event)
			 (map :cents)
			 (partition-by pos?)
			 (map (partial reduce + 0))
			 (map #(Math/abs %))
			 (apply max)))