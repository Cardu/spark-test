package example

import org.apache.spark.sql.streaming.StreamingQuery

object StructuredStreamingTest extends App with LocalSparkContext {
  val spark = createLocalSparkSession()
  val lines = spark
    .readStream
    .format("socket")
    .option("host", "localhost")
    .option("port", 9999)
    .load()
  import spark.implicits._

  val counts = lines.as[String].flatMap(_.split(" ")).groupBy("value").count()

  //StreamingQuery fornisce una serie di informazioni utili
  val query: StreamingQuery =
    counts
    .writeStream
      .outputMode("update")
      //update: significa pubblico solo righe modificate
      //complete: stampa ad ogni intervallo tutti i dati
      //append: solo nuove righe
      .format("console") //scrivo su console, qui potrebbe esserci kafka, per esempio
      .start()

  query.awaitTermination()
  spark.close()
}
