package example

import cats.effect.{ExitCode, IO, IOApp}

object StructuredStreamingKafka extends IOApp with LocalSparkContext {
  val userDefinedFunction: String => String = (input: String) => input.replace("Magalli", "Meghelli").trim()

  override def run(args: List[String]): IO[ExitCode] = sparkContextResource().use{ spark => {
    import spark.implicits._

    spark.udf.register("CleanString", userDefinedFunction)

    val app = spark
      .readStream
      .format("kafka")
      .option("kafka.bootstrap.servers", "localhost:9092")
      .option("subscribe", "foo")
      .load()
      .selectExpr("CleanString(CAST(value AS STRING))")
      .as[String]
      .map(a => a.toUpperCase()) //elaborazione
      .writeStream
      .outputMode("append")
      .option("truncate", "false")
      .format("console")
    IO {
      app.start().awaitTermination()
      ExitCode.Success
    }
  }
  }
}
