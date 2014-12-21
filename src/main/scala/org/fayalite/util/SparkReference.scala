package org.fayalite.util


import org.apache.spark.SparkContext

/**
 * Created by ryle on 12/2/2014.
 */
object SparkReference {

  var sc: SparkContext = _

  def getSC = {
    if (sc == null) {
      sc = new SparkContext("local[1]", "kernelSC")
    }
    sc
  }

}
