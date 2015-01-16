
import org.apache.spark.SparkContext
import org.fayalite.repl.NotebookREPL


val sc = new SparkContext("local[4]", "some name0")

sc.setLocalProperty("spark.dynamic.userReplPath", $intp.classServer.uri)
class HackSparkILoop(in: Option[BufferedReader], out:JPrintWriter) extends SparkILoop(in, out, None)

scala -classpath

import java.io.{PipedOutputStream, PipedInputStream, BufferedReader, InputStreamReader}
import scala.tools.nsc.interpreter.{JPrintWriter, AbstractFileClassLoader}
import org.apache.spark.repl.{SparkILoop, SparkIMain}
import org.apache.spark.repl.MagicSparkILoop



val replInputSource = new PipedInputStream()
val replInputSink = new PipedOutputStream(replInputSource)
val br = new BufferedReader(new InputStreamReader(replInputSource, "UTF-8"))

val replOutputSink = new PipedOutputStream()
val replOutputSource = new PipedInputStream(replOutputSink)

val pw = new JPrintWriter(replOutputSink)
val nrepl = new SparkILoop(Some(br), pw, None)


val replOutputSink = new PipedOutputStream()
val replOutputSource = new PipedInputStream(replOutputSink)

val pw = new JPrintWriter(replOutputSink)

val il = new HackSparkILoop(Some(br), pw)

val nrepl = new NotebookREPL(pw, replOutputSink)

import java.io._

val replInputSource = new PipedInputStream()
val replInputSink = new PipedOutputStream(replInputSource)
replInputSink.write("val x = 1\n".toCharArray.map{_.toByte})

val br = new BufferedReader(new InputStreamReader(replInputSource, "UTF-8"))
val _interp = new SparkILoop(None, new JPrintWriter(scala.Console.out, true), None)
val args = Array("")
_interp.process(args)


import scala.tools.nsc.interpreter.AbstractFileClassLoader
import scala.tools.nsc.util.ScalaClassLoader
import scala.tools.nsc.util.ScalaClassLoader.URLClassLoader

def makeClassLoader(): AbstractFileClassLoader =
  new TranslatingClassLoader(parentClassLoader match {
    case null   => ScalaClassLoader fromURLs compilerClasspath
    case p      =>
      _runtimeClassLoader = new URLClassLoader(compilerClasspath, p) with ExposeAddUrl
      _runtimeClassLoader
  })


sc.parallelize(1 to 20, 20).map{ x =>
  x
}.count


def tryParseSerializedThreadLocalProperties(taskName: String) = {
  import scala.util.{Try, Success, Failure}

  Try {

    val splitTaskName = taskName.split("stringSerializedthreadLocalProperties:")(1)
    val secondSplit = splitTaskName.split("parentPoolName:")
    val threadLocalProperties = secondSplit(0)
    val poolName = secondSplit(1)

    val dbll = threadLocalProperties.replaceAll("\\{", "").replaceAll("\\}",
      "").split(",").toList.map {
      _.split("=").toList
    }
    val fakeProperties = dbll.map {
      case List(x, y) => x.trim.stripMargin -> y.trim.stripMargin
    }.toMap
    (fakeProperties, poolName)
  }.toOption
}

val (threadLocalProperties, poolName) = tryParseSerializedThreadLocalProperties(testTaskName).get

val jarPath = threadLocalProperties.get("spark.dynamic.jarPath")
val replPath = threadLocalProperties.get("spark.dynamic.userReplPath")


