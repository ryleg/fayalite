package org.fayalite.util


import org.apache.spark.{SparkConf, SparkContext}

/**
 * Created by ryle on 12/2/2014.
 */
object SparkReference {

  var sc: SparkContext = _

  def getSC = {
    if (sc == null) {
      val sparkConf = new SparkConf()
      sparkConf.set("spark.scheduler.mode", "FAIR")
      sparkConf.setMaster("local[*]")
      sparkConf.setAppName("SuperMaster")
      sc = new SparkContext(sparkConf)
    }
    sc
  }

}
