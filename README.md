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
* **Completely Configurable** - ZStreamingQuote is developed in highly configured manner. The system configurations can be configured through the configuration file.

# On Going Feature Development
* **CSV Formatted Data** - Currently, ZStreamingQuote provides response data as JSON formatted when run in web service mode. To provide the data in CSV format is an ongoing development going on.

# Dependencies
1) Java 8 to be installed. Check the version of Java:
```java -version```, It should be 1.8.0.xxx
2) Maven building infrastructure is required to build the system. Check out the link: http://www.mkyong.com/maven/how-to-install-maven-in-windows/
2) MySQL database to be installed in the system/server for storing quotes, otherwise quote query will fail. For guide to install MySQL, check out the link: http://www.tutorialspoint.com/mysql/mysql-installation.htm
3) Default MySQL database name 'ZStreamingQuotesDB' should be present. The database name can be configured.

# Installation
Developers can clone the git repository, otherwise the source code can also be downloaded as zip from this page.
1) Go to the project directory:
```cd D:\ZStreamingQuote-master```, check if pom.xml exists in the path
2) Build maven project in root folder (where pom.xml exists):
```mvn clean package```
check if ```ZStreamingQuote-1.0-SNAPSHOT-jar-with-dependencies.jar``` is created in \target folder.
3) This jar can be deployed in linux machine or windows machine and can be run as platform independent way. No further jar dependencies are required, its been incorporated inside the jar package created.
4) Running the system:
Linux, assumed that the jar is placed in /home/ directory:
```nohup java -jar /home/ZStreamingQuote-master/target/ZStreamingQuote-1.0-SNAPSHOT-jar-with-dependencies.jar > /home/ZStreamingQuote-master/target/ZStreamingQuote.txt &```
This should run the system as a background process and would not be killed even if working console is closed (If not required e.g. testing purpose, remove ```nohup``` and ```&``` from the command).
Windows, assumed that it will run from the \target folder itself:
```java -jar .\target\ZStreamingQuote-1.0-SNAPSHOT-jar-with-dependencies.jar > .\target\ZStreamingQuoteLogs.txt```

# Usage - Stand Alone System



Web Service URLs:
```
http://localhost:8080/zstreamingquote/start/?apikey=zac2flvsbza9cge4&userid=DR2062&publictoken=bb20b54f2d9a0b6efa8d879e80a024dd
http://localhost:8080/zstreamingquote/stop/
http://localhost:8080/zstreamingquote/timerangeohlc/?format=json&instrument=121345&from=10:00:00&to=20:00:00
http://localhost:8080/zstreamingquote/timerangestreamingquote/?format=json&instrument=121345&from=10:00:00&to=20:00:00
```
