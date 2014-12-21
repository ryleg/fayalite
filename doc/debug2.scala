

import org.apache.spark.SparkContext
import org.apache.spark.repl.SparkILoop
import java.io.{InputStreamReader, BufferedReader, PipedOutputStream, PipedInputStream}
import scala.tools.nsc.interpreter.{JPrintWriter}
import scala.tools.nsc.interpreter.NamedParam
import _root_.scala.tools.nsc.Settings
import scala.reflect.ClassTag
/*
 ./dist/bin/spark-shell --jars ~/repo/fayalite/target/scala-2.10/fayalite.jar
 */
class DebugREPL {

  val replInputSource = new PipedInputStream()
  val replInputSink = new PipedOutputStream(replInputSource)
  val br = new BufferedReader(new InputStreamReader(replInputSource, "UTF-8"))

  val replOutputSink = new PipedOutputStream()
  val replOutputSource = new PipedInputStream(replOutputSink)

  val pw = new JPrintWriter(replOutputSink)
  val iloop = new SparkILoop(Some(br), pw, None)
  val intp = iloop.intp

  val maybeFailed = iloop.multiplexProcess(settings)

  var allHistory : String = ""

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



import org.apache.spark.SparkContext
val scc = new SparkContext("local[*]", "supermaster")
val userToREPL = List(1,2).map{x => (x, new DebugREPL())}.toMap
val userREPL = userToREPL.toList

userREPL.foreach{_._2.rebindSC(scc)}

val dr = new DebugREPL()

def mread() = {
  userREPL.foreach{
    case (user, drepl) => println(s"user $user, output: ${drepl.read()}")
  }
}

mread()


def mrun(code: String) = {
  userREPL.foreach {
    case (user, drepl) => drepl.iloop.interpret(code)
  }
  mread()
}


rebindSC()

mrun("$sc")



userREPL.map{
  case (user, drepl) => drepl.iloop.reset()
}

val result = nrepl.interpret("$sc")


val result = nrepl.interpret("$sc.makeRDD(1 to 10).count")
read()


//iloop.intp.quietBind(NamedParam[Test]("sc", scA)(tagOfSparkContext, classTag[Test])))





//println($intp.classServer.uri)
//val result = iloop.interpret("$intp.classServer.uri")








def rebindSC() = {
  import _root_.scala.tools.nsc.Settings
  type Test = SparkContext
  lazy val tagOfSparkContext = userREPL(0)._2.iloop.tagOfStaticClass[org.apache.spark.SparkContext]
  import _root_.scala.reflect._
  import scala.tools.nsc.interpreter.NamedParam
  val scA = scc

  userToREPL.toList.map { case (user, drepl) => drepl.iloop.intp.quietBind(scala.tools.nsc.interpreter.NamedParam[Test]("$sc",
    scA)(tagOfSparkContext, classTag[Test]))
  }
}


def rebindSC() = {
  import _root_.scala.tools.nsc.Settings
  type Test = SparkContext
  lazy val tagOfSparkContext = userREPL(0)._2.iloop.tagOfStaticClass[org.apache.spark.SparkContext]
  import _root_.scala.reflect._
  import scala.tools.nsc.interpreter.NamedParam
  val scA = scc

  userToREPL.toList.map { case (user, drepl) => drepl.iloop.intp.quietBind(scala.tools.nsc.interpreter.NamedParam[Test]("$sc",
    scA)(tagOfSparkContext, classTag[Test]))
  }
}

import java.io.{InputStreamReader, BufferedReader, PipedOutputStream, PipedInputStream}
import scala.tools.nsc.interpreter.{JPrintWriter}
import _root_.scala.tools.nsc.interpreter._
import akka.actor.{Props, ActorRef, Actor}
import akka.actor._
import akka.pattern._
import org.apache.spark.repl.{SparkILoop}
import org.fayalite.repl.JSON._
import org.fayalite.repl.REPL._
import scala.concurrent.Future
import _root_.scala.concurrent.Future
import org.fayalite.util.SparkAkkaUtilsExample._
import org.fayalite.util.SparkReference


case class RebindSC()
case class Register(user: String)

class HackREPLManager extends Actor{

  implicit val ec = org.fayalite.repl.REPL.ec

  var subscribedClients = Set[ActorRef]()

  val replInputSource = new PipedInputStream()
  val replInputSink = new PipedOutputStream(replInputSource)
  val br = new BufferedReader(new InputStreamReader(replInputSource, "UTF-8"))

  val replOutputSink = new PipedOutputStream()
  val replOutputSource = new PipedInputStream(replOutputSink)

  val pw = new JPrintWriter(replOutputSink)
  val nrepl = new SparkILoop(Some(br), pw, None)

  val settings = nrepl.multiplexProcess(Array(""))

  val maybeFailed = nrepl.multiplexProcess(settings)

  val interp = nrepl.intp

  def readLoopUpdate() = {
    while (true) {
      val output = read()
      if (output != "") subscribedClients.foreach{c => c ! output}
    }
  }

  val readFailed = Future {readLoopUpdate()}

  def receive = {
    case code: String => sender ! interp.interpret(code)
    case Register(user) => subscribedClients = subscribedClients ++ Set(sender)
    case RebindSC() =>  nrepl.rebindSC(SparkReference.sc)
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

import akka.pattern.ask
val actorSystem = createActorSystem(serverActorSystemName, defaultHost, defaultPort)
val server = actorSystem.actorOf(Props(new HackREPLManager()), name=serverActorName)


implicit val actorSystem2 = createActorSystem(clientActorSystemName, defaultHost, defaultPort+1)
implicit val rap = RemoteActorPath()
val server2 = getActor()

case class RegistrationRequest(registrationServer: ActorRef)

class EchoClient extends Actor {
  import akka.pattern._
  import akka.actor._

  val userName = "client0"

  def receive = {
    case RegistrationRequest(serverToRegisterWith) => serverToRegisterWith ! Register(userName)
    case x => println(s"From client: $x")
  }
}

val client = actorSystem2.actorOf(Props(new EchoClient()))

client ! RegistrationRequest(server)

import org.fayalite.util.SparkReference

import org.apache.spark.SparkContext
val sc = new SparkContext("local[*]", "supermaster")
SparkReference.sc = sc

val response = (server ? "$intp.classServer.uri").getAs[IR.Result]

println("first response " + response)


val scCheck = (server ? "sc").get


//server ! Register("client0")






