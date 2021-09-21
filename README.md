# Fountain


This simple module has just one very simple purpose.
**Provide streaming data to Spark Streaming without using any program outside**.
It's like a playground for Spark Streaming.
It is NOT for unit testing.

Maybe the simplest way to just check Spark Streaming is to use `netcat`.
But it's inconvenient.
This tool is like a substitute for `netcat`, but much simpler and easier to use.
And you can write it in your code.

It's especially convenient for Spark Streaming beginners.
When you learn Spark Streaming, you don't need to learn another thing like `netcat` or Kafka to see if it's working.
Learning other things to learn the thing you really want to learn is really a mental burden.
This simple tool is to take all that burden away.

Basically this program just sends text file data to a specific socket.

In the future version, I will make it generate random streaming data.


---


## Requirements

Java8/11 and Scala2

```
Scala >= 2.12.x
```

---

## How to Install

Add the line below in `build.sbt`.

```scala
libraryDependencies += "io.github.yrichika" %% "fountain" % "0.1.0"
```


---

## How to Use

Simply use `EachLineFromFile.run()` method to start a streaming server.
This will wait for Spark Streaming socket to connect.
And it will send data from the file when connected.



```scala
import fountain.servers

// This will start a server at [localhost:9999], and send each line in the [data/sample.json]
// at [300]-millisecond intervals
EachLineFromFile.run("localhost", 9999, "data/sample.json", 300)

// And just read streaming from socket
val lines = spark.readStream
  .format("socket")
  .option("dateFormat", "YYYY-MM-dd")
  .option("host", "localhost")
  .option("port", 9999)
  .load()

// ...
```

