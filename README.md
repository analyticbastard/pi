# pi

Solutions to Pi exercises

## Requisites

You need the standard Clojure toolbelt, including [Leiningen](http://leiningen.org/).


## Usage

To run the tests for the core logic components:

```lein test```

To run the web server:

```lein ring server```

To interact with the server API, you can use ```curl```:

````curl -X GET '{ "strings" : ["aaabbc" , "bbccd"] }'````

You will get a JSON map with one entry called ```result``` containing
the desired answer.

## License

Copyright © 2016 Javier Arriero

Distributed under the Eclipse Public License either version 1.0 or any later version.
