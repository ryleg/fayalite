package org.fayalite.util


import org.apache.spark.{SparkConf, SparkContext}

/**
 * Created by ryle on 12/2/2014.
 */
object SparkReference {

  var sc: SparkContext = _
  implicit var master = "local[*]" //"spark://ubuntu:7077" //

  def getSC = {
    if (sc == null) {
      val sparkConf = new SparkConf()
      sparkConf.set("spark.scheduler.mode", "FAIR")
      sparkConf.set("spark.speculation", "true")
      sparkConf.setMaster(master)
      sparkConf.setAppName("SuperMaster")
      sparkConf.set("spark.executor.memory", "512M")
      sparkConf.set("spark.driver.memory", "1G")
      sc = new SparkContext(sparkConf)
    }
    sc
  }

  def main(args: Array[String]) {
    getSC

    sc.makeRDD(1 to 10).count
  }

}
