package org.fayalite.util

/**
 * Created by ryle on 1/25/15.
 */

import scala.sys.process._

object SparkRuntimeManager {

  val SPARK_PRINT_LAUNCH_COMMAND = "SPARK_PRINT_LAUNCH_COMMAND"
  val sbin = Common.SPARK_HOME + "sbin/"

  def startMaster() = {
    val launch = sbin + "start-master.sh"
  }

  def startSlave() = {
//       ../bin/spark-class org.apache.spark.deploy.worker.Worker spark://ubuntu:7077

  }

  def main(args: Array[String]) {

    startMaster()




  }


}
