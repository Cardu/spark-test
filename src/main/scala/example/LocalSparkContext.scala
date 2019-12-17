package example

import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession
import org.apache.spark.streaming.{Seconds, StreamingContext}
import cats.effect.{IO, Resource}
import cats.implicits._

trait LocalSparkContext {
  //con local lo faccio girare senza yarn
  def createSparkContext() = {
    val spark = SparkSession.builder().appName("test").getOrCreate()
    spark.sparkContext
  }

  def createLocalSparkSession() = SparkSession.builder().master("local[*]").appName("test").getOrCreate()
  def createSparkSession() = SparkSession.builder().appName("test").getOrCreate()

  def createLocalSparkContext() = {
    val spark = createLocalSparkSession()
    spark.sparkContext
  }

  def createLocalSparkStreamingContext(seconds: Int = 10) = {
    val conf = new SparkConf().setMaster("local[*]").setAppName("Spark Streaming context")
    new StreamingContext(conf, Seconds(seconds))
  }

  def sparkContextResource(local: Boolean = true) = {
    val acquire = IO.pure(if(local) createLocalSparkSession() else createSparkSession())
    def release(session: SparkSession) = IO.delay(session.close())
    Resource.make(acquire)(release)
  }
}
