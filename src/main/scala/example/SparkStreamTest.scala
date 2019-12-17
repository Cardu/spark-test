package example

import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Seconds, StreamingContext}

object SparkStreamTest extends App with LocalSparkContext {
  val scc = createLocalSparkStreamingContext()

  //non fa da solo bind su porta, si aggancia a stream già aperto, quindi dovrei averlo aperto
  //si può usare netcat, nc -lk 9999
  val lines = scc.socketTextStream("localhost", 9999)

  lines.foreachRDD(rdd => {
    val words = rdd.flatMap(_.split(" "))
    val pairs = words.map(x => (x,1))
    val output = pairs.reduceByKey(_ + _)
    output.collect().foreach(println)
  })

  //alternative, one-liner
  //lines.map(_.split(" ")).map(word => (word,1)).reduceByKey(_ + _).print()

  scc.start()
  scc.awaitTermination()
}
