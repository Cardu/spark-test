package example

import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession

case class MyData(key: String, value: Int)

object SparkStreamToDataFrameTest extends App with LocalSparkContext {
  val scc = createLocalSparkStreamingContext()
  val lines = scc.socketTextStream("localhost", 9999)

  def routine(rdd: RDD[String]) = {
    val spark = SparkSession.builder().config(rdd.sparkContext.getConf).getOrCreate()
    import spark.implicits._

    val words = rdd.flatMap(_.split(" "))
    val pairs = words.map(x => (x,1))
    val output = pairs.reduceByKey(_ + _)
    output.map((MyData.apply _).tupled).toDF().show()
    //a questo punto potrei effettuare cquery sql sullo stesso
  }

  lines.foreachRDD(rdd => routine(rdd))

  scc.start()
  scc.awaitTermination()
}
