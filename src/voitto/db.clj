(ns voitto.db
  (:use [datomic.api :only [q db tempid] :as d]
        [clj-time.coerce :only [to-date]]
        [voitto.model :only [example-events]]))

(def db-uri "datomic:mem://play")
(d/create-database db-uri)
(def conn (delay (d/connect db-uri)))

(def account-schema
  [{:db/id #db/id[:db.part/db]
    :db/ident :account/ident
    :db/valueType :db.type/keyword
    :db/cardinality :db.cardinality/one
    :db/index true
    :db/unique :db.unique/identity
    :db/doc "The identity of the account, used in URIs"
    :db.install/_attribute :db.part/db}
   
   {:db/id #db/id[:db.part/db]
    :db/ident :account/name
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one
    :db/fulltext true
    :db/index true
    :db/unique :db.unique/value
    :db/doc "Human-readable name of the account"
    :db.install/_attribute :db.part/db}
   
   {:db/id #db/id[:db.part/db]
    :db/ident :account/type
    :db/valueType :db.type/ref
    :db/cardinality :db.cardinality/one
    :db/doc "Account type (asset, liability, income, expense etc.)"
    :db.install/_attribute :db.part/db}

   ; account/type enum values
   {:db/id #db/id[:db.part/db]
    :db/ident :account.type/asset}
   {:db/id #db/id[:db.part/db]
    :db/ident :account.type/liability}
   {:db/id #db/id[:db.part/db]
    :db/ident :account.type/income}
   {:db/id #db/id[:db.part/db]
    :db/ident :account.type/expense}])

(def event-schema
  [{:db/id #db/id[:db.part/db]
    :db/ident :event/date
    :db/valueType :db.type/instant
    :db/cardinality :db.cardinality/one
    :db/index true
    :db/doc "Date of the event. Time part is insignificant."
    :db.install/_attribute :db.part/db}
   
   {:db/id #db/id[:db.part/db]
    :db/ident :event/comment
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one
    :db/fulltext true
    :db/doc "A descriptive comment about the event."
    :db.install/_attribute :db.part/db}
   
   {:db/id #db/id[:db.part/db]
    :db/ident :event/otherParty
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one
    :db/fulltext true
    :db/doc "The other party of the transaction: who did the money come from or go to."
    :db.install/_attribute :db.part/db}
  
   {:db/id #db/id[:db.part/db]
    :db/ident :event/entry
    :db/valueType :db.type/ref
    :db/isComponent true
    :db/cardinality :db.cardinality/many
    :db/doc "The event consists of multiple entries."
    :db.install/_attribute :db.part/db}])

(def entry-schema
  [{:db/id #db/id[:db.part/db]
    :db/ident :entry/account
    :db/valueType :db.type/ref
    :db/cardinality :db.cardinality/one
    :db/index true
    :db/doc "The account this entry is crediting or debiting."
    :db.install/_attribute :db.part/db}
   
   {:db/id #db/id[:db.part/db]
    :db/ident :entry/cents
    :db/valueType :db.type/long
    :db/cardinality :db.cardinality/one
    :db/doc "The amount of the entry in 1/100ths of the currency ('cents'). Positive amount is debit, negative amount is credit."
    :db.install/_attribute :db.part/db}])

(def schema (concat account-schema event-schema entry-schema))

@(d/transact @conn schema)

(def example-accounts
  [{:db/id #db/id[:db.part/user]
    :account/ident :savings
    :account/name "My Savings Account"
    :account/type :account.type/asset}
   {:db/id #db/id[:db.part/user]
    :account/ident :checking
    :account/name "My Checking Account"
    :account/type :account.type/asset}
   {:db/id #db/id[:db.part/user]
    :account/ident :credit-card
    :account/name "Credit Card"
    :account/type :account.type/liability}
   {:db/id #db/id[:db.part/user]
    :account/ident :ice-cream
    :account/name "Ice Cream"
    :account/type :account.type/expense}
   {:db/id #db/id[:db.part/user]
    :account/ident :openings
    :account/name "Openings"
    :account/type :account.type/liability}
   {:db/id #db/id[:db.part/user]
    :account/ident :cash
    :account/name "Cash"
    :account/type :account.type/asset}])

@(d/transact @conn example-accounts)

(defn get-account-id-by-ident [ident]
  (q [:find '?e :where ['?e :account/ident ident]] (db @conn)))

(defn entry-to-entity [{:keys [account cents]}]
  {:db/id (tempid :db.part/user)
   :entry/account (get-account-id-by-ident account)
   :entry/cents cents})

(defn event-to-entity [{:keys [date comment other-party entries]}]
  {:db/id (tempid :db.part/user)
   :event/date (to-date date)
   :event/comment comment
   :event/otherParty (or other-party "")
   :event/entry (map entry-to-entity entries)})

(defn insert-events [events]
  (->> events
       (map event-to-entity)
       (d/transact @conn)))

@(insert-events example-events)

(defn get-events []
  (->> (db @conn)
       (q '[:find ?evn
            :where
            [?evn :event/date _]])
       (map first)
       (map (partial d/entity (db @conn)))))

(->> (db @conn)
     (q '[:find ?evn
          :where
          [?acc :account/type :account.type/expense]
          [?ent :entry/account ?acc]
          [?evn :event/entry ?ent]
          [?evn :event/comment ?comment]])
     (first) (first)
     (d/entity (db @conn))
     (into {}))