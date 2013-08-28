# Voitto - a simple yet efficient double-entry bookkeeping system

Copyright (C) 2010-2013 Santtu Pajukanta <santtu@pajukanta.fi>

Licensed under the [MIT license](http://opensource.org/licenses/MIT).

Work in progress. This is the Clojure branch of development. If you're looking for the Python utilities for the Tappio format, [see here](https://github.com/japsu/voitto).

## Getting started

Assuming you have [Leiningen](https://github.com/technomancy/leiningen) installed:

    lein run
    iexplore http://localhost:3000

## Notes

* Voitto is single-user for now. There is no authentication or authorization.

## Technology choices

* Backend
** [Clojure](https://github.com/clojure/clojure)
** [HTTP-Kit](https://github.com/http-kit/http-kit)
** [Compojure](https://github.com/weavejester/compojure)
** [Hiccup](https://github.com/weavejester/hiccup)
** [Datomic Free](https://www.datomic.com) database (PROPRIETARY!)
* Frontend
** Progressive enhancement (*not* a single page application)
** [jQuery](https://github.com/jquery/jquery) 1.10.x
** [Twitter Bootstrap](https://github.com/twitter/bootstrap)
** [Typeahead.js](https://github.com/twitter/typeahead.js)