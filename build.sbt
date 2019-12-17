import Dependencies._

ThisBuild / scalaVersion     := "2.11.8"
ThisBuild / version          := "0.1.0-SNAPSHOT"
ThisBuild / organization     := "com.example"
ThisBuild / organizationName := "example"

val sparkVersion = "2.4.4"
val sparkCore = "org.apache.spark" %% "spark-core" % sparkVersion
val sparkSql = "org.apache.spark" %% "spark-sql" % sparkVersion
val sparkStreaming = "org.apache.spark" %% "spark-streaming" % sparkVersion
val sparkMLlib = "org.apache.spark" %% "spark-mllib" % sparkVersion
val sparkStreamingKafka = "org.apache.spark" %% "spark-streaming-kafka-0-10" % sparkVersion
val sparkSqlKafka = "org.apache.spark" %% "spark-sql-kafka-0-10" % sparkVersion

val catsCore = "org.typelevel" %% "cats-core" % "2.0.0"
val catsEffect = "org.typelevel" %% "cats-effect" % "2.0.0"

lazy val root = (project in file("."))
  .settings(
    name := "spark-test",
    libraryDependencies += scalaTest % Test,
    libraryDependencies ++= Seq(sparkCore, sparkSql, sparkStreaming, sparkMLlib, sparkStreamingKafka, sparkSqlKafka, catsCore, catsEffect)
  )
