package org.fayalite.repl


import org.fayalite.repl.REPL._
import org.fayalite.util.{SparkReference, Common}

import org.apache.spark.{Logging, SparkConf, SparkContext}
import org.apache.spark.repl.{MagicSparkILoop, SparkCommandLine, SparkILoop}
import scala.reflect._

object SparkREPLManager {

   def main (args: Array[String]) {
    testEvaluation()
  }

  def testEvaluation() : Unit = {
    SparkReference.getSC
    val srm = new SparkREPLManager(0)
    println(srm.run("val x = 1"))
  }
}

class SparkREPLManager(userId: Int) extends REPLManagerLike with Logging {

  val iloop = new MagicSparkILoop(Some(br), pw)

  var cp = s"::${Common.SPARK_HOME}conf:" +
    s"${Common.SPARK_HOME}lib/spark-assembly-1.2.1-SNAPSHOT-hadoop1.0.4.jar:"

  val args = Array("-nowarn", "false", "-encoding", "UTF-8", "-classpath", cp)

  val command = new SparkCommandLine(args.toList, msg => println(msg))
  val settings = command.settings

  logInfo("Manager starting REPL loop")
  val maybeFailed = iloop.process(settings)
  logInfo("Manager finished attempting start REPL loop Success? " + maybeFailed)

  rebindSC(SparkReference.sc)

  def run(code: String, doRead: Boolean = true) = {
    logInfo("Manager code to run " + code)
    val res = iloop.intp.interpret(code)
    val response = (res, read()) //else (res, "")
    logInfo("Manager response " + response)
    response
  }

  def bind[T](name: String, reference: T)(implicit evidence: ClassTag[T]) = {
    import _root_.scala.tools.nsc.Settings
    lazy val tagOfReference = iloop.tagOfStaticClass[T]
    import _root_.scala.reflect._
    import scala.tools.nsc.interpreter.NamedParam
    iloop.intp.quietBind(scala.tools.nsc.interpreter.NamedParam[T](name,
      reference)(tagOfReference, classTag[T]))
  }

  def rebindSC(scb: SparkContext) = {
    bind("$sc", scb)
  }

}
