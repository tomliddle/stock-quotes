curl --request PUT localhost:9000/stock -H "Content-type: application/json" --data "{\"id\" : \"NASDAQ:AAPL\",\"name\" : \"ApAPLle Inc\", \"desc\" : \"Apple stock\"}"
curl --request PUT localhost:9000/stock -H "Content-type: application/json" --data "{\"id\" : \"NASDAQ:MSFT\",\"name\" : \"Microsoft\", \"desc\" : \"Microsoft stock\"}"
curl --request PUT localhost:9000/stock -H "Content-type: application/json" --data "{\"id\" : \"NASDAQ:AMZN\",\"name\" : \"Glaxo\", \"desc\" : \"Glaxo stock\"}"

