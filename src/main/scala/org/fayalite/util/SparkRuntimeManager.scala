package org.fayalite.util

import scala.sys.process._
import fa.SPARK_HOME
// TODO: Switch to ammonite-ops. Merge with remote server management code ala EMR/EC2

/**
 * In order to avoid patching spark, we must hijack the bootstrap launcher
 * for driver/master/executor and deploy instead our overriden versions
 * of these respective classes with patches inside. The only alternative
 * is a live patch of executor using scalive but that's pretty hard.
 * NOTE ** Currently using patched version of spark until replacement complete.
 */
object SparkRuntimeManager {

  // TODO: Pickup from configs.
  val shome = SPARK_HOME
  val SPARK_PRINT_LAUNCH_COMMAND = "SPARK_PRINT_LAUNCH_COMMAND"
  val sbin = SPARK_HOME + "sbin/"
  val bin = shome + "bin/"
  val masterHost = "ubuntu"

  def startMaster() = (sbin + "start-master.sh").!!

  def startSlave() = (bin + s"spark-class org.apache.spark.deploy.worker.Worker spark://$masterHost:7077 -m 512M -c 1").!!
  // ./spark-class org.apache.spark.deploy.worker.Worker spark://ubuntu:7077 -m 512M -c 1
  def main(args: Array[String]) {

    println(startMaster())

    Thread.sleep(10000)

    (1 to 3).par.foreach { _ => startSlave()}

  }


}
