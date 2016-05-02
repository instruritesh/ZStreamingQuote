# ZStreamingQuote
ZStreamingQuote is an open source framework for handling, processing and storing of Streaming Quotes for Zerodha Kite Connect Web Socket based Streaming Quotes. It is a Java based system.
It has been created for algorithmic trading for developers (even for those who has very little knowledge on software development) using Zerodha Kite Connect system.

# Current Features
* **Open-Source** - ZStreamingQuote has been released under an extremely permissive open-source MIT License, which allows full usage in both research and commercial applications, without restriction, but with no warranty of any kind whatsoever.
* **Free** - ZStreamingQuote is completely free and costs nothing to download or use.
* **Collaboration** - As ZStreamingQuote is open-source, developers can collaborate to improve the software. New features will be added frequently. Any bugs can be reported, will be fixed.
* **Stand Alone System** - ZStreamingQuote framework APIs can be integrated to any Java Application to run as a standalone system. Example source code is provided.
* **Web Service System** - Complete ZStreamingQuote framework can be run as a web service system also, REST based URL handlers are integrated. Code need not be modified to run the system. Its useful for non developers as well as non-Java guys.
* **Database Storage** - MySQL based database storage feature is incorporated.
* **OHLCV Data** - Feature to query Open/High/Low/Close/Volume data between a time range is incorporated. This is based on processing of stored streamed quote data between two requested times. This is JSON formatted data and useful for feeding the data to decision making intraday algorithms e.g. 5minute OHLC, 1minute OHLC etc etc.
* **Streamed Quote** - Other than OHLCV, the complete Streamed Quote between two time frame can be queried for application use. this is the JSON formatted raw websocket data.
* **Multithreaded system** - ZStreamingQuote system is multithreaded to remove latencies at various stages e.g. between receiving, processing and storing.
* **Auto Reconnect** - Auto Reconnect feature for the web socket has been incorporated at various states of Web Socket functionality, e.g., at heart bit failure, data starvation, abrupt closure, web socket connect/open failure etc etc

Web Service URLs:
```
http://localhost:8080/zstreamingquote/start/?apikey=zac2flvsbza9cge4&userid=DR2062&publictoken=bb20b54f2d9a0b6efa8d879e80a024dd
http://localhost:8080/zstreamingquote/stop/
http://localhost:8080/zstreamingquote/timerangeohlc/?format=json&instrument=121345&from=10:00:00&to=20:00:00
http://localhost:8080/zstreamingquote/timerangestreamingquote/?format=json&instrument=121345&from=10:00:00&to=20:00:00
```
