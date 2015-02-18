package org.fayalite.repl


import org.apache.spark.rdd.RDD
import org.fayalite.repl.REPL._
import org.fayalite.util.{SparkReference, Common}

import org.apache.spark.{Logging, SparkConf, SparkContext}
import org.apache.spark.repl.{MagicSparkILoop, SparkCommandLine, SparkILoop}
import scala.concurrent.Future
import scala.reflect._
import java.io.File
import java.net.URL
import org.apache.spark.SparkConf
import org.apache.spark.executor.{ExecutorURLClassLoader, ChildExecutorURLClassLoader, MutableURLClassLoader}
import org.apache.spark.util.Utils


object SparkREPLManager {

  def apply(userId: Int, singleJarSimplePath: String) = {
    new BootstrapREPLManager(userId, "file://" + singleJarSimplePath)
  }

  // TODO: Change spark distribution to accept multiple user jars and fix arg
  class BootstrapREPLManager(userId: Int, val singleJarPath: String) extends Serializable

  with Logging {

    val srm = new SparkREPLManager(userId, classPath = Some(singleJarPath))

 //   val jarPaths = classPath.split(":")
    val curi = srm.iloop.classServer.uri

    val cl = constructClassLoaderFromJars(Array(singleJarPath), curi)
    SparkContext.classLoaders(userId) = cl

    val setLocal = (property: String) => (value: String) => "$sc.setLocalProperty(" +
      "\"" + property + "\",\"" + value + "\")"

    val prop = setLocal("userId")(s"$userId")
    val jarprop = setLocal("spark.dynamic.jarPath")(singleJarPath)
    val uprop = setLocal("spark.dynamic.userReplPath")(curi)
    val p = (code: String) => logInfo(srm.run(code).toString())

    Seq(prop, jarprop, uprop).map{p}

  }

  val setLocal = (property: String) => (value: String) => "$sc.setLocalProperty(" +
    "\"" + property + "\",\"" + value + "\")"

  def constructClassLoaderFromJars(
                                    jarPaths: Array[String],
                                    classServerUri: String) = {
    val currentLoader = Thread.currentThread().getContextClassLoader
    val urls = jarPaths.map{jp => new java.net.URL(jp)}
    val parent: MutableURLClassLoader = new ExecutorURLClassLoader(urls, currentLoader)
    val conf = new SparkConf(true)
    val userCP: java.lang.Boolean = false
    val klass = Class.forName("org.apache.spark.repl.ExecutorClassLoader").asInstanceOf[Class[_ <: ClassLoader]]
    val constructor = klass.getConstructor(classOf[SparkConf], classOf[String],
      classOf[ClassLoader], classOf[Boolean])
    val cl = constructor.newInstance(conf, classServerUri, parent, userCP)
    cl
  }


  def testEvaluation() : Unit = {
    val sc = SparkReference.getSC

    class TestM(uid: Int) {

      val tcp = s"/home/ryle/vx$uid.jar"
      val tcpu = "file://" + tcp

      val currentLoader = Thread.currentThread().getContextClassLoader
      val urls = Array(new java.net.URL("file://" + tcp))
      val parent: MutableURLClassLoader = new ExecutorURLClassLoader(urls, currentLoader)
      val conf = new SparkConf(true)
      val userCP: java.lang.Boolean = false
      val klass = Class.forName("org.apache.spark.repl.ExecutorClassLoader").asInstanceOf[Class[_ <: ClassLoader]]
      val constructor = klass.getConstructor(classOf[SparkConf], classOf[String],
        classOf[ClassLoader], classOf[Boolean])

      val srm = new SparkREPLManager(uid, classPath = Some(tcp))

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
  //    p(setLocal("spark.scheduler.pool")(s"$uid"))

      p("org.fayalite.arcturus.Test.x")
    //  while (true) {
        p("$sc.parallelize(1 to 10, 3).map{_ => org.fayalite.arcturus.Test.x}.collect().toList")

   //     Thread.sleep(15000)

    //  }
    }

 //   "/home/ryle/repo/arcturus/target/scala-2.10/arcturus.jar"
    val x2 = "/home/ryle/vx2.jar"
    new TestM(1)
    //Future { }
    new TestM(2)

    Thread.sleep(Long.MaxValue)
  }
}

class SparkREPLManager(replId: Int, classPath: Option[String] = None) extends REPLManagerLike with Logging {

  val iloop = new MagicSparkILoop(Some(br), pw)

  var cp = s"::${Common.SPARK_HOME}conf:" +
    s"${Common.SPARK_HOME}lib/spark-assembly-1.2.1-SNAPSHOT-hadoop1.0.4.jar:" +
    classPath.getOrElse("") // fixme to map

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
