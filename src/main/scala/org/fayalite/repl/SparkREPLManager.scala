package org.fayalite.repl


import org.fayalite.repl.REPL._
import org.fayalite.util.Common

import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.repl.{HackSparkILoop, SparkCommandLine, SparkILoop}
import scala.reflect._


class SparkREPLManager(userId: Int) extends REPLManagerLike(userId) {

  val iloop = new HackSparkILoop(Some(br), pw)

  var cp = s"::${Common.SPARK_HOME}conf:" +
    s"${Common.SPARK_HOME}lib/spark-assembly-1.2.1-SNAPSHOT-hadoop1.0.4.jar:"

  val args = Array("-nowarn", "false", "-encoding", "UTF-8", "-classpath", cp)

  val command = new SparkCommandLine(args.toList, msg => println(msg))
  val settings = command.settings
  val maybeFailed = iloop.process(settings)

  def run(code: String, doRead: Boolean = true) = {
    val res = iloop.intp.interpret(code)
    if (doRead) (res, read())
    else (res, "")
  }

  import scala.reflect.api.{Mirror, TypeCreator, Universe => ApiUniverse}
  val u: scala.reflect.runtime.universe.type = scala.reflect.runtime.universe
  val m = u.runtimeMirror(Thread.currentThread().getContextClassLoader)

  private def tagOfStaticClass[T: ClassTag]: u.TypeTag[T] =
    u.TypeTag[T](
      m,
      new TypeCreator {
        def apply[U <: ApiUniverse with Singleton](m: Mirror[U]): U # Type =
          m.staticClass(classTag[T].runtimeClass.getName).toTypeConstructor.asInstanceOf[U # Type]
      })

  def bind[T](name: String, reference: T)(implicit evidence: ClassTag[T]) = {
    import _root_.scala.tools.nsc.Settings
    lazy val tagOfReference = tagOfStaticClass[T]
    import _root_.scala.reflect._
    import scala.tools.nsc.interpreter.NamedParam
    iloop.intp.quietBind(scala.tools.nsc.interpreter.NamedParam[T](name,
      reference)(tagOfReference, classTag[T]))
  }

  def rebindSC(scb: SparkContext) = {
    bind("$sc", scb)
  }

}
