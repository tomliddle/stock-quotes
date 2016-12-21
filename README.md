# stock-quotes
The stock-quotes app uses Play Framework 2.5 with [slick 3](https://github.com/slick/slick) library for database access.

It supports the following features:

* Generic Interface for CRUD operations allowing some code reuse.

* Models as case classes and slick models, independent from database driver and profile

#Running

The database pre-configured is an h2, so you just have to:


        $ sbt run

#Testing

To run all tests (routes and persistence tests):


        $ sbt test

#Using

	curl --request POST localhost:9000/stock -H "Content-type: application/json" --data "{\"id\" : \"APPL\",\"name\" : \"Apple\",\"desc\" : \"Apple plc\"}"

	curl localhost:9000/stock/APPL

#TODO

Update stock quotes periodically using scheduled actor


