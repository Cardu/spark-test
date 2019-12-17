package example

object SparkTest1 extends App with LocalSparkContext {
  val sc = createSparkContext()
  val myColl = 1 to 100
  val myRdd = sc.parallelize(myColl)
  println(s"NUMERIII: ${myRdd.map(_*2).collect().mkString(",")}")
  sc.stop()
}