# ZStreamingQuote
ZStreamingQuote is an open source framework for handling, processing and storing of Streaming Quotes for Zerodha Kite Connect Web Socket based Streaming Quotes. It is a Java based system.

It has been created for algorithmic trading for developers (even for those who has very little knowledge on software development) using Zerodha Kite Connect system.

For details about Kite Connect Streaming Quote, check out link: https://kite.trade/docs/connect/v1/#streaming-websocket.

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
* **Completely Configurable** - ZStreamingQuote is developed in highly configured manner. The system configurations can be configured through the configuration file. The complete details about all configurations are mentioned at later section of this page.

# On Going Feature Development
* **CSV Formatted Data** - Currently, ZStreamingQuote provides response data as JSON formatted when run in web service mode. To provide the data in CSV format is an ongoing development going on.

# Dependencies
1) Java 8 to be installed. Check the version of Java:
```java -version```, It should be 1.8.0.xxx

2) Maven building infrastructure is required to build the system. Check out the link: http://www.mkyong.com/maven/how-to-install-maven-in-windows/

2) MySQL database to be installed in the system/server for storing quotes, otherwise quote query will fail. For guide to install MySQL, check out the link: http://www.tutorialspoint.com/mysql/mysql-installation.htm

3) Default MySQL database name ```ZStreamingQuotesDB``` should be present. The database name can be configured.

4) User requires to be logged in Zerodha Kite system, since apikey, userid and public token is required to run the system.


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
In this mode, Java APIs of the framework can be integrated in your own Java application to run the system in stand alone fashion. Its just like running any other jar in the system (basically desktop version).

Example code is given in ```TestApp.java```, Path: ```<root>/src/main/java/com/ritesh/zstreamingquote/app/TestApp.java```

Step 1: Modify ```apiKey```, ```userId```, ```publicToken```.

Step 2: Call ```ZStreamingQuoteControl.getInstance().start()``` with these three parameters.

Step 3: Modify ```fromTime``` and ```toTime``` according to the time range for which data is required.

Step 4: Modify ```instrumentToken``` for which data is required.

Step 5: Call ```ZStreamingQuoteControl.getInstance().getOHLCDataByTimeRange()``` with these three parameters to get **Open/High/Low/Close/Volume** data between these times for the given instrument.

Step 6: Call ```ZStreamingQuoteControl.getInstance().getQuoteListByTimeRange()``` with these three parameters to get the ```complete streamed data``` between these times for the given instrument.

Step 7: Call ```ZStreamingQuoteControl.getInstance().stop()``` to stop the system.

NOTE: If you plan to use maven build for stand alone system, remember to replace the Jetty server class main method assigned as ```mainClass``` for maven build to your main mthod class with fully qualified path name.

# Usage - Web Service System
In this mode, Java code need not be modified. Just run the system (see above point on running system). Access the REST API URLs for controlling the streaming quote process and querying data from any application/webpage/command line. The URLs are described below (assuming the Host IP of the web server running is ```localhost```, modify the host ip with it if you intend to run the server at remote IP)
1) **Start The System**: 
Assuming your user id=DR1234, api key=abc2flfdgh9cge4, public token=bb20b54f2d9a0b6efa8d879e80a024dd.
This API call will start the Complete System.
GET URL: ```http://localhost:8080/zstreamingquote/start/?apikey=abc2flfdgh9cge4&userid=DR1234&publictoken=bb20b54f2d9a0b6efa8d879e80a024dd```.
Modify the URL filter parameter accordingly
  a) ```apikey``` - your Zerodha Connect API key, 
  b) ```userid``` - your Zerodha ID.
  c) ```publictoken``` - your public token obtained after loging in to Zerodha Connect API. For login details check link: ```https://kite.trade/docs/connect/v1/#login-flow```.

2) **Stop The System**: 
This API call will stop the Complete System.
GET URL: ```http://localhost:8080/zstreamingquote/stop/```

3) **Query OHLCV between a time range**: 
Assuming that you want the JSON formatted Open/High/Low/Close/Volume data within time range 09:20:00 a.m to 09:30:00 a.m. for the instrument token 121345.
This API call will start the Complete System.
GET URL: ```http://localhost:8080/zstreamingquote/timerangeohlc/?format=json&instrument=121345&from=09:20:00&to=09:30:00```.
Modify the URL filter parameter accordingly
  a) ```format``` - in which format data will be fetched, currently only ```json``` is supported, 
  b) ```instrument``` - instrument token for which data is required, this is obtained from Zerodha connect API ```https://api.kite.trade/instruments?api_key=xxx```. NOTE: instrument tokens required for your subscription must be assigned in config paramters explained below.
  c) ```from``` - beginning time of the time range from which OHLCV data is required,
  d) ```to``` - end time of the time range till which OHLCV data is required.

4) **Query Raw Streamed Quote data between a time range**: 
Assuming that you want the JSON formatted Open/High/Low/Close/Volume data within time range 09:20:00 a.m to 09:30:00 a.m. for the instrument token 121345.
This API call will start the Complete System.
GET URL: ```http://localhost:8080/zstreamingquote/timerangestreamingquote/?format=json&instrument=121345&from=09:20:00&to=09:30:00```.
Modify the URL filter parameter accordingly
  a) ```format``` - in which format data will be fetched, currently only ```json``` is supported, 
  b) ```instrument``` - instrument token for which data is required, this is obtained from Zerodha connect API ```https://api.kite.trade/instruments?api_key=xxx```. NOTE: instrument tokens required for your subscription must be assigned in config paramters explained below.
  c) ```from``` - beginning time of the time range from which raw streamed quote data is required,
  d) ```to``` - end time of the time range till which raw streamed quote data is required.
  
  NOTE: The JSON data for raw streamed quote API will provide response according to the mode subscribed e.g. ```ltp```/```quote```/```full```, the JSON structure will change accordingly. The mode can be changed from configuration mentioned below.

# Configurations
The Configuration file is ```ZStreamingConfig.java```, Path in the project tree is ```/src/main/java/com/ritesh/zstreamingquote/config/ZStreamingConfig.java```. Modify the following parameters according to your need. Default values in built are already in place.

**Common Configurations**:
1) ```QUOTE_STREAMING_START_TIME```: If you want the system to start at later point of time than immediate start, modify the start time here in ```HH:MM:SS``` format. Type: ```String```. Default value: ```"09:15:01"```.
2) ```QUOTE_STREAMING_WS_HEARTBIT_CHECK_TIME```: Time gap after which system will reconnect once Heart Bit is missed, Value is in milliseconds. Type: ```Integer```. Default value: ```3000```.
3) ```QUOTE_STREAMING_WS_DATA_CHECK_TIME_ON_SUBSCRIBE```: Time gap after which system will reconnect once it detects it starves for data even though subscribed for instruments, Value is in milliseconds. Type: ```Integer```. Default value: ```5000```.
4) ```QUOTE_STREAMING_WS_SUBSCRIBE_DELAY_ON_INITIATE```: Time gap after which system will reconnect once it detects Web Socket did not get opened even though it has been initiated, Value is in milliseconds. Type: ```Integer```. Default value: ```500```.
5) ```QUOTE_STREAMING_REINITIATE_RETRY_LIMIT```: Number of connection retry, if it detects Web Socket did not get opened even though it has been initiated, Value is in milliseconds. Type: ```Integer```. Default value: ```5```.
6) ```QUOTE_STREAMING_START_AT_BOOTUP```: Whether to start the websocket streaming immediately once application starts or to be delayed. NOTE: If delayed configure ```QUOTE_STREAMING_START_TIME``` above. Type: ```Boolean```. Default value: ```false```.

**Database Configurations**:
7) ```QUOTE_STREAMING_DB_URL```: MySQL Database name and JDBC URL path. Type: ```String```. Default value: ```"jdbc:mysql://localhost/ZStreamingQuotesDB"```.
8) ```QUOTE_STREAMING_DB_USER```: user name of the database. Type: ```String```. Default value: ```"root"```.
9) ```QUOTE_STREAMING_DB_PWD```: user password of the database. Type: ```String```. Default value: ```""```.
10) ```QUOTE_STREAMING_DB_TABLE_NAME_PRE_APPENDER```: Table Name pre appender which will be used to create the table name initial words once websocket starts ticking data. Type: ```String```. Default value: ```"StreamingQuoteMode_Mode"```.
11) ```QUOTE_STREAMING_DB_TABLE_NAME_POST_APPENDER```: Table Name post appender which will be used to create the table name later words once websocket starts ticking data. Type: ```String```. Default value: ```"_Date"```.
12) ```QUOTE_STREAMING_DB_STORE_REQD```: Flag whether to store the streaming data in DB or not. Type: ```Boolean```. Default value: ```true```.

**Debug print Configurations**:
13) ```QUOTE_STREAMING_HEART_BIT_MSG_PRINT```: Flag whether to print Heart Bit Messages or not. Type: ```Boolean```. Default value: ```true```.
14) ```QUOTE_STREAMING_QUOTE_FLOW_MSG_PRINT```: Flag whether to print quote messages or not. Type: ```Boolean```. Default value: ```true```.
15) ```QUOTE_STREAMING_WEB_SERVICE_MSG_PRINT```: Flag whether to print Web Service Messages or not. Type: ```Boolean```. Default value: ```true```.

**Mandatory Configurations**:
16) ```QUOTE_STREAMING_INSTRUMENTS_ARR```: Instrument Tokens for which Data is to be subscribed in Web Socket. Type: ```String Array```. Default value: ```{ "121345", "1793" }```. ***NOTE: This is a mandatory field to be modified according to your requirement.***
17) ```QUOTE_STREAMING_TRADING_HOLIDAYS```: Trading Holiday dates on which Web Socket will not be activated in ```DD-MM-YYYY``` format. This field is useful when your system runs in cronjob automatically by default everyday and to prevent unneccessary data population. Type: ```String Array```. Default value: ```{ "26-01-2016", "07-03-2016", "24-03-2016", "25-03-2016", "14-04-2016", "15-04-2016", "19-04-2016", "06-07-2016", "15-08-2016", "05-09-2016", "13-09-2016", "11-10-2016", "12-10-2016", "31-10-2016", "14-11-2016" }```. ***NOTE: This is a mandatory field to be modified according to your requirement.***
18) ```QUOTE_STREAMING_DEFAULT_MODE```: Mode in which quote data will be received from web socket, accordingly system will switch the mode after subscribing. ```QUOTE_STREAMING_MODE_LTP = "ltp"```, ```QUOTE_STREAMING_MODE_QUOTE = "quote"```, ```QUOTE_STREAMING_MODE_FULL = "full"```. Type: ```String```. Default value: ```QUOTE_STREAMING_MODE_QUOTE```.

**Web Server Configurations**:
19) ```JETTY_SERVER_PORT_NUM```: Port Number in which Web Server will run in you system. Type: ```Integer```. Default value: ```8080```.
20) ```JETTY_SERVER_PROCESS_START_URL```: URL for starting the streaming process, this is mapped to point 1) **Start The System** in **Usage - Web Service System** mentioned above. Type: ```String```. Default value: ```"/zstreamingquote/start"```.
21) ```JETTY_SERVER_PROCESS_STOP_URL```: URL for stopping the streaming process, this is mapped to point 2) **Stop The System** in **Usage - Web Service System** mentioned above. Type: ```String```. Default value: ```"/zstreamingquote/stop"```.
22) ```JETTY_SERVER_TIMERANGE_OHLC_URL```: URL for querying the OHLCV data from system, this is mapped to point 3) **Query OHLCV between a time range** in **Usage - Web Service System** mentioned above. Type: ```String```. Default value: ```"/zstreamingquote/timerangeohlc"```.
23) ```JETTY_SERVER_TIMERANGE_STREAMING_QUOTE_URL```: URL for querying the raw streamed quote data from system, this is mapped to point 4) **Query Raw Streamed Quote data between a time range** in **Usage - Web Service System** mentioned above. Type: ```String```. Default value: ```"/zstreamingquote/timerangestreamingquote"```.
24) ```JETTY_SERVER_PROCESS_START_APIKEY_REQ_PARAM```: API Key filter request parameter in process start URL, this is mapped to point 1) **Start The System**  in **Usage - Web Service System** mentioned above. Type: ```String```. Default value: ```"apikey"```.
25) ```JETTY_SERVER_PROCESS_START_USERID_REQ_PARAM```: User Id filter request parameter in process start URL, this is mapped to point 1) **Start The System**  in **Usage - Web Service System** mentioned above. Type: ```String```. Default value: ```"userid"```.
26) ```JETTY_SERVER_PROCESS_START_PUBTOKEN_REQ_PARAM```: Public Token filter request parameter in process start URL, this is mapped to point 1) **Start The System**  in **Usage - Web Service System** mentioned above. Type: ```String```. Default value: ```"publictoken"```.
27) ```JETTY_SERVER_TIMERANGE_FORMAT_REQ_PARAM```: Data format filter request parameter in Data Query (OHLCV/Streamed Data) URL, this is mapped to both point 3) **Query OHLCV between a time range** and 4) **Query Raw Streamed Quote data between a time range** in **Usage - Web Service System** mentioned above. Type: ```String```. Default value: ```"format"```.
28) ```JETTY_SERVER_TIMERANGE_FROM_TIME_REQ_PARAM```: From Time (beginning time) of the time range filter request parameter in Data Query (OHLCV/Streamed Data) URL, this is mapped to both point 3) **Query OHLCV between a time range** and 4) **Query Raw Streamed Quote data between a time range** in **Usage - Web Service System** mentioned above. Type: ```String```. Default value: ```"from"```.
29) ```JETTY_SERVER_TIMERANGE_TO_TIME_REQ_PARAM```: To Time (end time) of the time range filter request parameter in Data Query (OHLCV/Streamed Data) URL, this is mapped to both point 3) **Query OHLCV between a time range** and 4) **Query Raw Streamed Quote data between a time range** in **Usage - Web Service System** mentioned above. Type: ```String```. Default value: ```"to"```.
30) ```JETTY_SERVER_TIMERANGE_INSTRUMENT_REQ_PARAM```: Instrument Token filter request parameter in Data Query (OHLCV/Streamed Data) URL, this is mapped to both point 3) **Query OHLCV between a time range** and 4) **Query Raw Streamed Quote data between a time range** in **Usage - Web Service System** mentioned above. Type: ```String```. Default value: ```"instrument"```. **NOTE: Values passed here must be used for Subscribing data from Web Socket also else No Data will be provided.**
