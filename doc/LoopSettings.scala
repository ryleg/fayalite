package org.fayalite.repl

import org.apache.spark.repl.SparkILoop

import scala.tools.nsc.Settings
import scala.tools.nsc.interpreter.ILoop


trait PlexLike {
  def run() : Unit = ???
}

class LoopSettings(sil: ILoop) extends PlexLike {

  val settings = new Settings
  val compilerPath = java.lang.Class.forName("scala.tools.nsc.Interpreter").getProtectionDomain.getCodeSource.getLocation
  val libPath = java.lang.Class.forName("scala.Some").getProtectionDomain.getCodeSource.getLocation

  println("compilerPath=" + compilerPath)
  println("settings.bootclasspath.value=" + settings.bootclasspath.value)

  settings.bootclasspath.value = List(settings.bootclasspath.value, compilerPath, libPath) mkString java.io.File.pathSeparator
  settings.usejavacp.value = true

  override def run() = {
    sil.process(settings)
  }
}

object Debug{
  def main(args: Array[String]) {

    val settings = new Settings
    val compilerPath = java.lang.Class.forName("scala.tools.nsc.Interpreter").getProtectionDomain.getCodeSource.getLocation
    val libPath = java.lang.Class.forName("scala.Some").getProtectionDomain.getCodeSource.getLocation

    println("compilerPath=" + compilerPath)
    println("settings.bootclasspath.value=" + settings.bootclasspath.value)

    settings.bootclasspath.value = List(settings.bootclasspath.value, compilerPath, libPath) mkString java.io.File.pathSeparator
    settings.usejavacp.value = true

    println()
  }
}

/*

 -cp ::/Users/ryle/110spark/conf:/Users/ryle/110spark/lib/spark-assembly-1.1.0-hadoop1.0.4.jar:/Users/ryle/110spark/lib/datanucleus-api-jdo-3.2.1.jar:/Users/ryle/110spark/lib/datanucleus-core-3.2.2.jar:/Users/ryle/110spark/lib/datanucleus-rdbms-3.2.1.jar -XX:MaxPermSize=128m -Xms512m -Xmx512m org.apache.spark.deploy.SparkSubmit --class org.apache.spark.repl.Main spark-shell

 */