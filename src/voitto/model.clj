(ns voitto.model
  (use [clj-time.core :only [local-date]]))

(def example-transactions
  [{:date (local-date 2013 8 26)
    :comment "Opening the books"
    :entries [{:account :openings
               :cents -100000}
              {:account :savings
               :cents 100000}]}
   {:date (local-date 2013 8 26)
    :comment "Pocket money"
    :entries [{:account :savings
               :cents -25000}
              {:account :cash
               :cents 25000}]}
   {:date (local-date 2013 8 26)
    :comment "Buy ice cream"
    :other-party "Tampereen jäätelötehdas"
    :entries [{:account :cash
               :cents -300}
              {:account :ice-cream
               :cents 300}]}
   {:date (local-date 2013 8 27)
    :comment "Example unbalanced transaction"
    :entries [{:account :cash
               :cents -20}]}])

(defn process-entry
  [bals {account :entry/account
         cents   :entry/cents}]
  (merge-with + bals (assoc {} account cents)))

(defn process-transaction [bals {entries :transaction/entry}]
  (reduce process-entry bals entries))

(defn balances [transactions]
  (reduce process-transaction {} transactions))

(defn balanced-transaction? [transaction]
  (->> (:transaction/entry transaction)
       (map :entry/cents)
       (reduce + 0)
       (= 0)))

(defn simple-transaction? [transaction]
  (and
    (balanced-transaction? transaction)
    (= 2 (count (:transaction/entry transaction)))))

(defn split-transaction? [transaction]
  (and
    (balanced-transaction? transaction)
    (> 2 (count (:transaction/entry transaction)))))

(defn transaction-total [transaction]
  (->> (:transaction/entry transaction)
			 (map :entry/cents)
			 (partition-by pos?)
			 (map (partial reduce + 0))
			 (map #(Math/abs %))
			 (apply max)))