package org.fayalite.repl


import org.fayalite.repl.REPL._
import org.fayalite.util.{SparkReference, Common}

import org.apache.spark.{Logging, SparkConf, SparkContext}
import org.apache.spark.repl.{MagicSparkILoop, SparkCommandLine, SparkILoop}
import scala.reflect._
import java.io.File
import java.net.URL
import org.apache.spark.SparkConf
import org.apache.spark.executor.{ExecutorURLClassLoader, ChildExecutorURLClassLoader, MutableURLClassLoader}
import org.apache.spark.util.Utils


object SparkREPLManager {

   def main (args: Array[String]) {
    testEvaluation()
  }

  val setLocal = (property: String) => (value: String) => "$sc.setLocalProperty(" +
    "\"" + property + "\",\"" + value + "\")"

  def jarsToCL(jars: Array[String], classServerUri: String) = {
    val urls = jars.map{jar => new java.net.URL("file://" + jar)}
    val currentLoader = Thread.currentThread().getContextClassLoader
    val parent: MutableURLClassLoader = new ExecutorURLClassLoader(urls, currentLoader)
    val conf = new SparkConf(true)
    val userCP: java.lang.Boolean = false
    val klass = Class.forName("org.apache.spark.repl.ExecutorClassLoader").asInstanceOf[Class[_ <: ClassLoader]]
    val constructor = klass.getConstructor(classOf[SparkConf], classOf[String],
      classOf[ClassLoader], classOf[Boolean])
    val cl = constructor.newInstance(conf, classServerUri, parent, userCP)

   // val jarprop = setLocal("spark.dynamic.jarPath")(tcpu)
   // val uprop = setLocal("spark.dynamic.userReplPath")(classServerUri)
    cl
  }



  def testEvaluation() : Unit = {
    val sc = SparkReference.getSC

    class TestM(uid: Int) {

      val tcp =    s"/home/ryle/vx$uid.jar"
      val tcpu = "file://" + tcp

      val currentLoader = Thread.currentThread().getContextClassLoader
      val urls = Array(new java.net.URL("file://" + tcp))
      val parent: MutableURLClassLoader = new ExecutorURLClassLoader(urls, currentLoader)
      val conf = new SparkConf(true)
      val userCP: java.lang.Boolean = false
      val klass = Class.forName("org.apache.spark.repl.ExecutorClassLoader").asInstanceOf[Class[_ <: ClassLoader]]
      val constructor = klass.getConstructor(classOf[SparkConf], classOf[String],
        classOf[ClassLoader], classOf[Boolean])

      val srm = new SparkREPLManager(uid, classPath = tcp)

      val cl = constructor.newInstance(conf, srm.iloop.classServer.uri, parent, userCP)

      SparkContext.classLoaders(uid) = cl

      println(srm.run("val x = 1"))
      println(srm.run("val x = 2"))
      println(srm.run("$sc"))

      val setLocal = (property: String) => (value: String) => "$sc.setLocalProperty(" +
        "\"" + property + "\",\"" + value + "\")"

      val curi = srm.iloop.classServer.uri

      val prop = setLocal("userId")("1")
      val jarprop = setLocal("spark.dynamic.jarPath")(tcpu)
      val uprop = setLocal("spark.dynamic.userReplPath")(curi)
      val p = (code: String) => println(srm.run(code))

      println(prop)

      p(jarprop)
      p(uprop)

      p("org.fayalite.arcturus.Test.x")

      p("$sc.parallelize(1 to 10, 3).map{_ => org.fayalite.arcturus.Test.x}.collect().toList")

    }

 //   "/home/ryle/repo/arcturus/target/scala-2.10/arcturus.jar"
    val x2 = "/home/ryle/vx2.jar"

    new TestM(1)
    new TestM(2)

    Thread.sleep(Long.MaxValue)
  }
}

class SparkREPLManager(replId: Int, classPath: String = "") extends REPLManagerLike with Logging {

  val iloop = new MagicSparkILoop(Some(br), pw)

  var cp = s"::${Common.SPARK_HOME}conf:" +
    s"${Common.SPARK_HOME}lib/spark-assembly-1.2.1-SNAPSHOT-hadoop1.0.4.jar:" + classPath

  val args = Array("-nowarn", "false", "-encoding", "UTF-8", "-classpath", cp)

  val command = new SparkCommandLine(args.toList, msg => println(msg))
  val settings = command.settings

  logInfo("Manager starting REPL loop")
  val maybeFailed = scala.util.Try{

    iloop.process(settings)
  //  SparkReference.sc.setLocalProperty()
  }
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
