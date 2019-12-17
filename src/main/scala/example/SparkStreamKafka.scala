package example

import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.rdd.RDD
import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent
import org.apache.spark.streaming.kafka010._

object SparkStreamKafka extends App with LocalSparkContext {
  val scc = createLocalSparkStreamingContext()

  val kafkaParams = Map[String, Object](
    "bootstrap.servers" -> "localhost:9092",
    "key.deserializer" -> classOf[StringDeserializer],
    "value.deserializer" -> classOf[StringDeserializer],
    "group.id" -> "spark-kafka-streaming-test",
    "auto.offset.reset" -> "latest",
    "enable.auto.commit" -> (false: java.lang.Boolean)
  )

  val topics = Array("foo")
  val stream = KafkaUtils.createDirectStream[String, String](
    scc,
    PreferConsistent,
    Subscribe[String, String](topics, kafkaParams)
  )
  stream.foreachRDD(routine(_))

  scc.start()
  scc.awaitTermination()


  def routine(rdd: RDD[ConsumerRecord[String,String]]) = {
    rdd.foreach(a => println(s"timestamp: ${a.timestamp} - value: ${a.value}"))
  }
}
