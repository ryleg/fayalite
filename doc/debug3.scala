


import java.net.URLClassLoader



import scala.concurrent.Future
import scala.xml.NodeSeq

implicit val ec = scala.concurrent.ExecutionContext.Implicits.global
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.repl.{SparkCommandLine, SparkILoop}
import java.io.{InputStreamReader, BufferedReader, PipedOutputStream, PipedInputStream}
import scala.tools.nsc.interpreter.{JPrintWriter}
import scala.tools.nsc.interpreter.NamedParam
import _root_.scala.tools.nsc.Settings
import scala.reflect.ClassTag
import org.apache.spark.rdd.RDD


val home = System.getProperty("user.home")

val SPARK_HOME = s"$home/repo/spark-dynamic/dist/"

val FAYALITE_JAR = "/home/ubuntu/repo/fayalite/target/scala-2.10/fayalite.jar"

//val ucl = new URLClassLoader()

class DebugREPL(userId: Int) {
  val replInputSource = new PipedInputStream()
  val replInputSink = new PipedOutputStream(replInputSource)
  val br = new BufferedReader(new InputStreamReader(replInputSource, "UTF-8"))
  val replOutputSink = new PipedOutputStream()
  val replOutputSource = new PipedInputStream(replOutputSink)
  val pw = new JPrintWriter(replOutputSink)
  val iloop = new SparkILoop(Some(br), pw, None)
  val wrongSettings = iloop.multiplexProcess(Array(""))

  var cp = s"::${SPARK_HOME}conf:" +
    s"${SPARK_HOME}lib/spark-assembly-1.2.1-SNAPSHOT-hadoop1.0.4.jar:" //+
    //s"$FAYALITE_JAR:" +
//    s"/home/ubuntu/p$userId.jar"

  val args = Array("-nowarn", "false", "-encoding", "UTF-8", "-classpath", cp)

  val command = new SparkCommandLine(args.toList, msg => println(msg))
  val settings = command.settings
  val maybeFailed = iloop.multiplexProcess(settings)

  def run(code: String, doRead: Boolean = true) = {
    val res = iloop.intp.interpret(code)
    if (doRead) (res, read())
    else (res, "")
  }

  def bind[T](name: String, reference: T)(implicit evidence: ClassTag[T]) = {
    import _root_.scala.tools.nsc.Settings
    lazy val tagOfSparkContext = iloop.tagOfStaticClass[T]
    import _root_.scala.reflect._
    import scala.tools.nsc.interpreter.NamedParam
    iloop.intp.quietBind(scala.tools.nsc.interpreter.NamedParam[T](name,
      reference)(tagOfSparkContext, classTag[T]))
  }
  def rebindSC(scb: SparkContext) = {
    bind("$sc", scb)
  }
  var allHistory : String = ""
  def read() : String = {
    var output = ""
    var bytesRead = 0
    do {
      bytesRead = replOutputSource.available()
      val buffer = new Array[Byte](bytesRead)
      replOutputSource.read(buffer)
      output += buffer.map {
        _.toChar
      }.mkString("")
    } while (bytesRead > 0)
    allHistory += output
    output
  }
}


import org.apache.spark.{SparkConf, SparkContext}

val sparkConf = new SparkConf()
sparkConf.set("spark.scheduler.mode", "FAIR")
sparkConf.setMaster("local[*]")
sparkConf.setAppName("SuperMaster")
val scc = new SparkContext(sparkConf)

val dr = new DebugREPL(1)

dr.rebindSC(scc)

dr.run("$sc")


import akka.util.Timeout
import akka.actor.{ActorRef, Actor}
import akka.pattern.ask
implicit val timeout = Timeout(10)

import akka.actor.{ActorSystem, Props}
import com.typesafe.config.ConfigFactory

val serverActorSystemName = "FayaliteServer"
val clientActorSystemName = "FayaliteClient"

val serverActorName = "FayaliteMultiplex"
val clientActor = "FayaliteREPLClient"

val defaultHost = "127.0.0.1"
val defaultPort = 16180

def createActorSystem(name: String = serverActorSystemName,
                      host: String = defaultHost,
                      port: Int = defaultPort
                       ) = {
  val akkaThreads = 4
  val akkaBatchSize = 15
  val akkaTimeout = 100
  val akkaFrameSize = 10 * 1024 * 1024
  val akkaLogLifecycleEvents = false
  val lifecycleEvents = "on"
  val logAkkaConfig = "on"
  val akkaHeartBeatPauses = 600
  val akkaFailureDetector = 300.0
  val akkaHeartBeatInterval = 1000
  val requireCookie = false
  val secureCookie = ""
  val akkaConf =
    ConfigFactory.parseString(
      s"""
      |akka.daemonic = on
      |akka.loggers = [""akka.event.slf4j.Slf4jLogger""]
      |akka.stdout-loglevel = "ERROR"
      |akka.jvm-exit-on-fatal-error = off
      |akka.remote.require-cookie = "$requireCookie"
      |akka.remote.secure-cookie = "$secureCookie"
      |akka.remote.transport-failure-detector.heartbeat-interval = $akkaHeartBeatInterval s
      |akka.remote.transport-failure-detector.acceptable-heartbeat-pause = $akkaHeartBeatPauses s
      |akka.remote.transport-failure-detector.threshold = $akkaFailureDetector
      |akka.actor.provider = "akka.remote.RemoteActorRefProvider"
      |akka.remote.netty.tcp.transport-class = "akka.remote.transport.netty.NettyTransport"
      |akka.remote.netty.tcp.hostname = "$host"
      |akka.remote.netty.tcp.port = $port
      |akka.remote.netty.tcp.tcp-nodelay = on
      |akka.remote.netty.tcp.connection-timeout = $akkaTimeout s
      |akka.remote.netty.tcp.maximum-frame-size = ${akkaFrameSize}B
      |akka.remote.netty.tcp.execution-pool-size = $akkaThreads
      |akka.actor.default-dispatcher.throughput = $akkaBatchSize
      |akka.log-config-on-start = $logAkkaConfig
      |akka.remote.log-remote-lifecycle-events = $lifecycleEvents
      |akka.log-dead-letters = $lifecycleEvents
      |akka.log-dead-letters-during-shutdown = $lifecycleEvents
      """.stripMargin)

  val actorSystem = ActorSystem(name, akkaConf)

  actorSystem
}

class TestActorResponse extends Actor {
  def receive = {
    case x => println(x)
  }
}

class TestREPLManager extends Actor {
  def receive = {
    case codeMsg : String => {
      val (result, stdOutString) = dr.run(codeMsg)
      val interp = dr.iloop.intp
      val request = interp.prevRequestList.last
/*      val lastHandler: interp.memberHandlers.MemberHandler = request.handlers.last.asInstanceOf[interp.memberHandlers.MemberHandler]
      val line = request.lineRep
      val lhdt = lastHandler.definesTerm.get*/
      sender ! (request, result, stdOutString)
    }
  }

}

val actorSystem = createActorSystem(serverActorSystemName, defaultHost, defaultPort)

def serverInitialize(host: String = defaultHost, port: Int = defaultPort) = {
  actorSystem.actorOf(Props(new TestREPLManager()), name=serverActorName)
}

val server = serverInitialize()

import scala.concurrent.duration._
import scala.concurrent._

implicit val timeout = Timeout(10.seconds)

Await.result(server ? "$sc", 1.seconds)

val tar = actorSystem.actorOf(Props(new TestActorResponse()), name=serverActorName)




dr.run("1")

val interp = dr.iloop.intp

val request = interp.prevRequestList.last

val lastHandler: interp.memberHandlers.MemberHandler = request.handlers.last.asInstanceOf[interp.memberHandlers.MemberHandler]

val line = request.lineRep
val lhdt = lastHandler.definesTerm.get
line.readPath

interp.global

import scala.tools.nsc.interactive.Global
val x = lastHandler.definesTerm.get.asInstanceOf[scala.tools.nsc.Global#Name]
request.fullPath(x)


import scala.reflect.internal.Names


scala.reflect.internal.Names$TermName_R

request.fullPath(lastHandler.definesTerm.get.toString)


val renderObjectCode =
  """object $rendered {
    |  %s
    |  INSTANCE.`res1` // %s
    |  %s
    |}""".stripMargin.format(
      request.importsPreamble,
      request.fullPath(lastHandler.definesTerm.get.toString),
      request.importsTrailer
    )


val compiled = line.compile(renderObjectCode)



  val renderedClass2 = Class.forName(
    line.pathTo("$rendered")+"$", true, interp.classLoader
  )

  val o = renderedClass2.getDeclaredField(interp.global.nme.MODULE_INSTANCE_FIELD.toString).get()

  def iws(o:Any):NodeSeq = {
    val iw = o.getClass.getMethods.find(_.getName == "$iw")
    val o2 = iw map { m =>
      m.invoke(o)
    }
    o2 match {
      case Some(o3) => iws(o3)
      case None =>
        val r = o.getClass.getDeclaredMethod("rendered").invoke(o)
        val h = r.asInstanceOf[Widget].toHtml
        h
    }
  }

 // iws(o)


/*
val trdd = scc.makeRDD(1 to 1000).cache().setName("1")
trdd.count*/

//Automatic error recovery using replay and static jar and multi repl process, buffer of debugREPL jvms
import scala.util.Try
/*
scc.setLocalProperty("spark.scheduler.pool", "user2")
scc.setLocalProperty("spark.dynamic.jarPath", s"file:/$FAYALITE_JAR")
scc.setLocalProperty("spark.dynamic.userReplPath", $intp.classServer.uri)
scc.makeRDD(1 to 10).map{_ => Try{Test.test}}.first*/

org.fayalite.util.SparkAkkaUtilsExample.defaultHost
//file:///home/ubuntu/
def getTLC(userId: Int) : String ={
  ( """
    |$sc.setLocalProperty("spark.scheduler.pool", "user""" + s"$userId" + """")
    |$sc.setLocalProperty("spark.dynamic.jarPath", s"p""" + s"$userId" + """.jar")
    |$sc.setLocalProperty("spark.dynamic.userReplPath", $intp.classServer.uri)
|""").stripMargin.split("\n").filter{p => p != ""}.mkString(" ; ")}
getTLC(2)

def fixString(string: String) = {
  {
    string.stripMargin.split("\n").filter{p => p != ""}.mkString(" ; ")}
}

def getProps = {
  """
    |$sc.getLocalProperty(
  """.stripMargin
}



val userToREPL = List(1,2).map{x => (x, new DebugREPL(x))}.toMap
val userREPL = userToREPL.toList

userREPL.foreach{ur => ur._2.rebindSC(scc)}
userREPL.map{ur => ur._2.run("$sc")}

userREPL.map{case (u, r) => r.run(getTLC(u))}

scc.addJar("file:///home/ubuntu/p1.jar")
scc.addJar("file:///home/ubuntu/p2.jar")

userREPL.map{
  case (u, r) =>
   // val cmd = "import scala.util.Try; Try{ $sc.makeRDD(1 to 10).map{_ => Test.test}.first} ;"

    r.run(cmd)
}

userREPL.map{_._2}.map{_.iloop.intp.settings}

import java.net.URLClassLoader

new java.net.URL("/home/ubuntu/p2.jar")

//14/12/14 05:06:17 INFO Executor: RYLE - urls for classloader List(/ubuntu/p2.jar


