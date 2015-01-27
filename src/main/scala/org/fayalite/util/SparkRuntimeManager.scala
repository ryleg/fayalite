package org.fayalite.util

/**
 * Created by ryle on 1/25/15.
 */

import scala.sys.process._

object SparkRuntimeManager {

  val shome = Common.SPARK_HOME
  val SPARK_PRINT_LAUNCH_COMMAND = "SPARK_PRINT_LAUNCH_COMMAND"
  val sbin = Common.SPARK_HOME + "sbin/"
  val bin = shome + "bin/"
  val masterHost = "ubuntu"

  def startMaster() = (sbin + "start-master.sh").!!

  def startSlave() = (bin + s"spark-class org.apache.spark.deploy.worker.Worker spark://$masterHost:7077 -m 512M -c 1").!!
  // ./spark-class org.apache.spark.deploy.worker.Worker spark://ubuntu:7077 -m 512M -c 1
  def main(args: Array[String]) {

    //startMaster()

    (1 to 3).par.foreach { _ => startSlave()}

  }


}
