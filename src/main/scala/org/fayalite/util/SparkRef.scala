package org.fayalite.util


import org.apache.spark.sql.SQLContext
import org.apache.spark.{SparkConf, SparkContext}

import rx._
import rx.ops._

/**
 * Static quarantine box to make Spark happy.
 */
object SparkRef {

  var sc: SparkContext = _
  var sqlContext: SQLContext = _
  var sparkConf: SparkConf = _

  var rsqc: Var[SQLContext] = _
  var rsconf: Var[SparkConf] = _
  var rsc: Var[SparkContext] = _

  var run : (String, Boolean) => String = _


  implicit val master = Var("local[*]") //"spark://ubuntu:7077" //

  /**
   * Return a SparkContext that has hard-coded parameters
   * for testing, ideally these should be supplied by some sort
   * of config supplied by users through the browser instead
   * of being reliant on parsing some SPARK_HOME/conf dir.
   * @return SparkContext duh.
   */
  def getSC: SparkContext = {
    if (sc == null) {
      sparkConf = new SparkConf()
      sparkConf.set("spark.scheduler.mode", "FAIR")
      sparkConf.set("spark.speculation", "true")
      sparkConf.setMaster(master())
      sparkConf.setAppName("SuperMaster")
      sparkConf.set("spark.executor.memory", "512M")
      sparkConf.set("spark.driver.memory", "1G")
      println("starting sparkcontext")
      sc = new SparkContext(sparkConf)
      println("started sparkcontext")
      sqlContext = new SQLContext(sc)
//      rsc() = sc
   //   rsqc() = sqlContext
    } // bring up screen repl server single cmd run main inspect actors.
    sc
  }

  /**
   * Demonstrates working.
   * @param args
   */
  def main(args: Array[String]) {
    getSC
    sc.makeRDD(1 to 10).count
  }

}
