package example

import org.apache.spark.ml.Pipeline
import org.apache.spark.ml.classification.{DecisionTreeClassificationModel, DecisionTreeClassifier}
import org.apache.spark.ml.evaluation.MulticlassClassificationEvaluator
import org.apache.spark.ml.feature.{IndexToString, StringIndexer, VectorAssembler}
import org.apache.spark.ml.linalg.Vectors


object MLlibTest extends App with LocalSparkContext {
  val spark = createLocalSparkSession()
  val training = spark.createDataFrame(Seq(
    (1.0, Vectors.dense(0.0, 1.1, 0.1)),
    (0.0, Vectors.dense(2.0, 1.0, -1.0)),
    (0.0, Vectors.dense(2.0, 1.3, 1.0)),
    (1.0, Vectors.dense(0.0, 1.2, -0.5))
  )).toDF("label", "features")

  val data =
    spark
      .read
      .format("csv")
      .option("header", "false")
      .option("inferSchema", "true")
      .load("file://///Users/cardu/Dev/spark/iris.csv")
        .toDF("sepal_length", "sepal_width", "petal_length", "petal_width", "species")

  val split = data.randomSplit(Array(0.7, 0.3), 100)
  val (trainingData, scoringData) = (split(0), split(1))

  val assembler = new VectorAssembler()
    .setInputCols(Array("sepal_length", "sepal_width", "petal_length", "petal_width"))
    .setOutputCol("features")

  val labelIndexer = new StringIndexer()
    .setInputCol("species")
    .setOutputCol("indexedSpecies")
    .fit(data)

  //forzare albero ad essere semplice -> maxdepth basso, instances per node alto
  //forzare albero ad essere complesso -> maxdepth alto, instances per node basso
  val dt = new DecisionTreeClassifier()
    .setLabelCol("indexedSpecies")
    .setFeaturesCol("features")
    .setMaxDepth(5)
    .setMaxBins(64)
    .setMinInstancesPerNode(10)

  val labelConverter = new IndexToString()
    .setInputCol("indexedSpecies")
    .setOutputCol("predictedSpecies")
    .setLabels(labelIndexer.labels)

  val pipeline = new Pipeline().setStages(Array(assembler, labelIndexer, dt, labelConverter))

  val trainedModel = pipeline.fit(trainingData)

  val predictions = trainedModel.transform(scoringData)

  val evaluator = new MulticlassClassificationEvaluator()
    .setLabelCol("indexedSpecies")
    .setPredictionCol("prediction")
    .setMetricName("accuracy")
  val accuracy = evaluator.evaluate(predictions)
  println(s"ACCURACY: $accuracy")

  val rfModel = trainedModel.stages(2).asInstanceOf[DecisionTreeClassificationModel]
  println(s"Learned classification tree model:\n ${rfModel.toDebugString}")

  spark.close()
}
