


import java.io.File

import akka.actor._
import akka.io.IO
import ammonite.ops.RelPath
import com.github.tototoshi.csv.CSVParser
import org.apache.spark.rdd.RDD
import org.apache.spark.storage.StorageLevel
import org.apache.spark.{Logging, SparkContext}
import org.apache.spark.repl.{SparkCommandLine}
import org.fayalite.layer.MessageParser
import org.fayalite.layer.MessageParser.{ParseRequest, ParseResponseDebug}
import org.fayalite.repl.{JsREPL, REPLManagerLike}
import org.fayalite.ui.oauth.OAuth.OAuthResponse
import org.fayalite.ui.ws.Server.Push
import org.fayalite.util.SparkRef
import spray.can.websocket.FrameCommandFailed
import spray.can.websocket.frame.{TextFrame, BinaryFrame}
import spray.can.{websocket, Http}
import spray.can.server.UHttp
import spray.http.HttpRequest
import spray.routing
import spray.routing.HttpServiceActor

import scala.reflect.ClassTag
import scala.util.{Success, Failure, Try}

val sc = SparkRef.getSC


// These tests need to be re-incorporated back into the jar.

val rml = new REPLManagerLike()
val iloop = new MagicSparkILoop(Some(rml.iLoopBufferedReader), rml.iLoopOutputCatch)
val fy = "/home/ubuntu/fayalite"
val cp = fy + "/lib/spark-assembly-1.2.1-SNAPSHOT-hadoop1.0.4.jar" //+
// ":" + fy + "/target/scala-2.10/fayalite.jar"
val args = Array("-nowarn", "false", "-encoding", "UTF-8", "-classpath", cp)
val command = new SparkCommandLine(args.toList, msg => println(msg))
val settings = command.settings
val maybeFailed = scala.util.Try {
  iloop.process(settings)
}
def run(code: String, doRead: Boolean = true) = {
  val res = iloop.intp.interpret(code)
  val response = (res, rml.read()) //else (res, "")
  //logInfo("Manager response " + response)
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
} // save as single text file.
rebindSC(SparkRef.sc)

run("$sc.makeRDD(1 to 10).first")

import fa._
import org.fayalite.ui.{oauth, ParseServer}
import org.fayalite.ui.ws.{Bottleneck, Server}
import scala.concurrent.{ExecutionContext, Future}
import scala.sys.process._

val authed = List("ryledup@gmail.com", "nimbusaurus@gmail.com")
@volatile var authedTokens: Array[String] = scala.io.Source.fromFile("secretauthed").getLines(
).toArray.distinct

val metadata = scala.collection.mutable.Map[String, String]()
val metaray = scala.collection.mutable.Map[String, Array[String]]()

import rx.core.{Var, Obs}
import rx.ops._


val metastore = Var{Try{readFromFile("metastore")}.map{
  q => q.json[KVC]
}.getOrElse(Map[String, Array[String]]())}

Obs(metastore, skipInitial=true) {
  writeToFile("metastore", metastore().json)
}

val jt = Map("yo" -> Array("asdf")).json
jt.json[Map[String, Array[String]]]

var procSender: ActorRef = null

def testConn = procSender ! TextFrame(ParseResponseDebug(Array("YOYO")).json)

def getCode = scala.io.Source.fromFile("tmpl/src/main/scala/Inject.scala"
).getLines().mkString("\n")

def getAT(cookies: String) = cookies.split("&token_type").headOption.map {
  _.split("queryString=access_token=").last}

def getAuthed(cookies: String) = cookies.split("&token_type").headOption.map {_.split("queryString=access_token=").last}.exists {authedTokens.contains}

def blank = ParseResponseDebug(Array())

def mkMap = scala.collection.mutable.Map[String, String]()

val at2email = mkMap

val temp = """package app.templ
             |import scala.scalajs.js.JSApp
             |import scala.scalajs.js.annotation.JSExport
             |object Inject extends JSApp {
             |@JSExport
             |def main(): Unit = {
             |println("yo")
             |}
             |}
             |""".stripMargin

//writeToCSVFile("tmpl/src/main/scala/Inject.scala", temp)
/* val res = SparkRef.run("$sc.textFile(" + fnm.withQuotes +
   ").first()"
   , true)
 var ress = ""
 println("first " + res.toString())
 ress += "REPL first: " + res.toString*/

def ts[T](f: => T) =  {
  Try{
    f
  } match { case Success(x) =>
    Left(x) ; case Failure(e) =>
    e.printStackTrace()
    Right(e.getStackTraceString)
  }
}

def tss[T](f: => T): String = ts(f) match {
  case Left(x) => x.toString ; case Right(e) => e
}


val sep =  "\n___\n"

type Histogram = (Array[Double], Array[Long])

implicit class Histext6(h: Histogram) {
  def prStr = {
    val bounds = h._1.sliding(2).toList.map{
      case Array(x,y) => (x,y)}
    bounds.zip(h._2).map{
      case ((bot, top), cnt) =>
        s"$cnt between $bot & $top"
    } mkString "\n"
  }
}

def driverCSV(f: String) = {
val reader = com.github.tototoshi.csv.CSVReader.open(new File(f))
SparkRef.sc.makeRDD(reader.iterator.toSeq).map{
  _.map{_.replaceAll("\r\n", "").replaceAll("\n", "")}
}
}



/*
val tx = driverCSV("sfbiz")
tx.map{_.size}.countByValue
import org.fayalite.util.JSON
val psf = JSON.parse4s(scala.io.Source.fromFile("secret/sfbus.json").getLines().mkString)
val dt = {psf \\ "data"}
import org.json4s.JsonAST.JArray
dt.asInstanceOf[JArray].children.map{
  _.asInstanceOf[JArray].children
}.head
import org.apache.spark.SparkContext._
val tst = MessageParser.target.toString + "/" + "sfbus"
val tx = SparkRef.sc.textFile(tst)
val prz = tx.map{prs}
prz.first()
prz.map{_.nonEmpty}.countByValue()
tx.zipWithIndex().groupBy{q => math.ceil(q._2.toDouble / 3D)}.map{
  case (x,y) =>
    y.map{_._1}.mkString(", ")
    //  j.slice(0, 2).mkString(""", """) + "LAST" + j.last
}.repartition(1).saveAsTextFile("sfbiz")
  //take(10).foreach{q => println("new " + q)}
val cb = tx.map(prs).flatMap{x => x}.countByValue.toList
val cbd = SparkRef.sc.makeRDD(cb)
cbd.map{_._2}.histogram(10)
*/



//tx.take(10).foreach{println}

def prs(l: String) = {
  val p = new CSVParser(com.github.tototoshi.csv.defaultCSVFormat)
  val ret: Option[List[String]] = p.parseLine(l)
  ret
}

//tx map prs take 10 foreach println

def cbvs[T](r: RDD[T]) = r.countByValue().toList.sortBy{_._2}.reverse.slice(0, 10).map{
  case (q,w) =>
    "Count: " + w + " @ value: " +  q.toString
}.mkString("\n")
def gc[T](n: String, f: () => RDD[T]) = {
  SparkRef.sc.getPersistentRDDs.collectFirst{
    case q if q._2.name == n => q._2.asInstanceOf[RDD[T]]}.getOrElse(f())
}
def pd[T](n: String, f: RDD[T]) = {
  f.persist(StorageLevel.DISK_ONLY).setName(n)
}


var jobStart = (cnt: String) => {
  println("queuing job2 ")
  Future {
    val fnm = MessageParser.target.toString + "/" + cnt
    println("texting file " + fnm)
    var ress = ""
    def as = {
      metadata(cnt) = ress
      ress += sep
    }
    def aa(s: String) = {
      println("aa " + s)
      ress += s
      as
    }
    val errors: String = tss {
      val pr = gc(fnm, () => {
        val dc = driverCSV(fnm) //SparkRef.sc.textFile(fnm)
       dc.persist(StorageLevel.DISK_ONLY).setName(fnm)
      })
        /* val pr = r map { l =>
      val p = new CSVParser(com.github.tototoshi.csv.defaultCSVFormat)
      p.parseLine(l).get}*/
      val total = pr.count
      aa { "Total Num Rows: " + total }
      val hdrrr = pr.first()
      val hdrz = hdrrr.length
      aa {"Total Num Columns: " + hdrz}
      aa { "Headers: " + hdrrr.mkString(",")}
      val rows = pr.filter{j => !j.sameElements(hdrrr)}
      import org.apache.spark.SparkContext._
      aa { "Row length distribution: \n" + cbvs(pr.map{_.size}) } //pr.map{_.size}.histogram(10).prStr }
      val cbv = pr.flatMap {
        x => x.map{z => z -> 1}
      }.groupByKey().map{_._2.size.toDouble}
      aa {"Overall top value cardinalities: \n" + cbvs(cbv)}
      aa {
        "Overall value cardinality distribution: \n" + cbv.histogram(10
        ).prStr
      }
      val sum: Double = pr.map {
        _.count {
          _ == ""
        }
      }.sum
    aa {
      "Number of missing values (by empty string): " + sum}
      aa {"Percent missing values: " + sum*100/(total*hdrz) + "%"}
      aa {"Distribution of fraction of missing values per row: \n" +
        pr.map{x => x.count{_.isEmpty}.toDouble / hdrz}.histogram(10).prStr
      }
      val numChars = pr.map{_.map{_.length}.sum}.sum
      aa { "Overall number of characters: " + numChars }
        val numUnic = pr.map {
          _.map {
            _.count {
              _.isUnicodeIdentifierStart
            }
          }.sum
        }.sum
      aa { "Overall number of unicode characters: " + numUnic }
      val letterOrDigit = pr.map{
        _.map{
          _.count{
            _.isLetterOrDigit
          }
        }.sum
      }.sum
      aa {"Percent unicode characters: " +
        {numUnic.toDouble*100/numChars}.toString.slice(0,4) + "%"}
      aa {"Overall number of letter or digit characters: " + letterOrDigit}
      aa {"Percent letter or digit characters: " + letterOrDigit.toDouble*100/numChars}
        aa {"\nSuccess"}
      }
      aa { errors }
      println("Job finished " + ress)
  }
}

def up(k: String, v: Array[String], preserve: Boolean = false) = {
  val prv = if (preserve)
    metastore().getOrElse(k, Array[String]())
  else Array[String]()
  metastore() = metastore().updated(k, prv ++ v)
}

var genReq = (r: String, em: Option[String]) => {
  var rz = Array[String]()
  if (!r.startsWith("@fa")) {
    println("from email: " + em)
    if (r.startsWith("runreq")) {
      val f = r.split(":").last
      jobStart(f)
    }
    if (r.startsWith("schema")) {
      val y = r drop 6 split "___"
      metastore() = metastore().updated(y.head, y.tail.mkString("___").split(","))//," toList
    }
    println("genreq " + r)
    println("genreq ret" + rz.toList)
  }
  rz
}

// Ops // Intro, etc.


var proc = (m: String, sendr: ActorRef) => {
  procSender = sendr
  Try {
    val q = m.json[MessageParser.ParseRequest]
    if (!getAuthed(q.cookies)) blank
    else {
      val c = q.code
      var resps = Array("test|as")
      if (c != "@fa") {
        println(c)
      }
      val at = getAT(q.cookies)
      val em: Option[String] = at.flatMap{ att =>
        at2email.get(att)}
      em.foreach{
        e =>
          at.foreach{
            a =>
              up(a, Array(e))
          }
      }
      c match {
        case x if x.startsWith("compile") =>
          println("compiling")
          Future{JsREPL.inject(c drop 7)}(ec).onComplete{q =>
            q.foreach{z => println("sending compile response")
              sendr ! TextFrame(
              ParseResponseDebug(Array("compile" + z)))}
          }
        case x if x.startsWith("file") =>
          val cnt = x.drop(4).split("\\|")
          println("attempting write to file " + cnt.head)
          writeToCSVFile(cnt.head, cnt.tail.mkString("|"))
        case x if x.startsWith("req") =>
          val fnm = x.drop(4)
          resps = resps ++ Array(
            x + ":" + metadata.getOrElse(fnm, "unprocessed")
          )
          if (!metadata.contains(fnm)) jobStart(fnm)
        case x if x.startsWith("save") => {
          println("saving " + c)
          writeToCSVFile("tmpl/src/main/scala/Inject.scala", c drop 4)
        }
        case x => resps = resps ++ genReq(x, em)
      }
      val qqq = metastore().map{
        case (x,y) => x + "___" + y.mkString("---")
      }.toArray
      resps = resps ++ qqq
      val res = ParseResponseDebug(Array(
        "Test|asdf",
        "Upload CSV | Click Here",
        "CSVs|" + MessageParser.getCSVs.mkString(","),
        "sjs" + getCode,
        "email" + at.flatMap{att =>
          at2email.get(att)}.getOrElse("anon")
      ) ++ resps)
      //  println(res.json)
      res
    }
  } match {
    case Failure(e) =>
    e.printStackTrace()
    ParseResponseDebug(Array(e.getStackTraceString))
  case Success(x) => x
  }
}.json




val system = ActorSystem()


val yourURL = "yoururl.com"

def parser(q: String, access_token: String) = {
  val e: String = q.json[OAuthResponse].email
  at2email(access_token) = e
  e match {
    case x if x.endsWith(yourURL) || authed.contains(x) =>
      authedTokens = authedTokens ++ Array(access_token)
      scala.tools.nsc.io.File(
        "secretauthed").appendAll(access_token + "\n")
    case _ =>
  }
}

var lastSender: ActorRef = null

class WebSocketWorker2(val serverConnection: akka.actor.ActorRef) extends HttpServiceActor with websocket.WebSocketServerWorker {
  override def receive = handshaking orElse businessLogicNoUpgrade orElse closeLogic
  def businessLogic: Receive = {
    case TextFrame(xq) =>
      lastSender = sender()
      sender() ! TextFrame(
        proc(xq.utf8String, sender())
      )
    case x: FrameCommandFailed =>
      println("frame failed " + x)
    //log.error("frame command failed", x)
    case x: HttpRequest =>
      println("httprequest " + x)
    // do something
    // }
    case x => println("unrecognized frame" + x)
  }
  def businessLogicNoUpgrade: Receive = {
    implicit val refFactory: akka.actor.ActorRefFactory = context
    runRoute {
      pathPrefix("js") {
        get {
          getFromResourceDirectory("js")
        }
      } ~
        path("oauth_callback") {
          get {
            getFromResource("oauth.html")
          }
        } ~
        path("oauth_catch") {
          get { parameters('access_token) { access_token =>
            import fa._ // i fa (with hovers); jump to start of line, etc.
            // split stream, split var exec into 2 paths. jump between paths.
            // given an obj, draw two edges split into a flow. F1 F2 hotkeys.
            oauth.OAuth.performGoogleOAuthRequest(access_token)
              .onComplete {
                _.foreach {
                  q =>
                    parser(q, access_token)
                }
              }(ec)
            getIndex
          }
          }
        } ~ pathPrefix("fayalite-app-dynamic") {
        get {
          val r1 = unmatchedPath {
            path =>
              val fp = Common.currentDir + // USE L / R to indicate syntax parsing order instead of
              // brackets.
                "app-dynamic/target/scala-2.11/fayalite-app-dynamic" +
                path.toString()
              getFromFile(fp)
          }
          r1
        }
      } ~ getIndex
    }
  }
  def getIndex: routing.Route = {
    getFromFile(Common.currentDir + "index-fastopt.html")
  }
}


// Fix this to polymorphic in the jar.
class WebSocketServer extends akka.actor.Actor with akka.actor.ActorLogging {
  def receive = {
    // when a new connection comes in we register a WebSocketConnection actor as the per connection handler
    case Http.Connected(remoteAddress, localAddress) =>
      val serverConnection = sender()
      val p = akka.actor.Props({new WebSocketWorker2(serverConnection)})
      val conn = context.actorOf(p)
      serverConnection ! Http.Register(conn)
  }
}


val server = system.actorOf(Props(new WebSocketServer()), "websocket")

IO(UHttp)(system) ! Http.Bind(server, "0.0.0.0", 80)


SparkRef.run = (x: String, y: Boolean) => run(x, y).toString


