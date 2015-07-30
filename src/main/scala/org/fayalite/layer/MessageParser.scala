package org.fayalite.layer

import akka.actor.ActorRef
import org.fayalite.repl.JsREPL
import org.fayalite.util.JSON
import spray.can.websocket.frame.TextFrame

import fa._

import scala.util.Try

object MessageParser {

  case class ParseRequest(
                           tab: Option[String],
                           requestId: Option[String],
                           cookies: Option[String],
                           fileRequest: Option[Array[String]],
                           code: Array[String]
                           )

  case class ParseResponse(
                            classRefs: Option[Array[String]] =
                            None, //Some(fileGraphStatic),
                            classContents: Option[Map[String, String]] = None,
                            heartBeatActive: Boolean = true
                            )

  case class FileIO(name: String, contents: String)
  case class IdIO(id: Int, io: String)
  case class RIO(asyncOutputs: Array[String], asyncInputs: Array[IdIO])


  @volatile var curScreen : String = ""

  def defaultPoll ={
    val newrd = JsREPL.readScreen()
    if (curScreen == newrd) None
    else {
      curScreen = newrd
      Some(newrd)
    }
  }

  case class Response(
                       classRefs: Option[Array[String]] = None,
                       files: Option[Array[FileIO]] = None,
                       replIO: Option[RIO] = None,
                       out: Option[String] = None,
                       pollOutput: Option[String] = defaultPoll
                       )

  //val fileGraphStatic =  FSMan.fileGraph()
  /*
  G1 G2
   */

  //JsREPL.initTailWatchScreenLog

  case class ParseResponseDebug(
                               sbtOut: String
                                 )

  def parseBottleneck(msg: String, ref: ActorRef) = {
//    println("parse bottlenck " + msg)
    val pr = Try{msg.json[ParseRequest]}.toOption
     pr.foreach{
      qq =>
        qq.code.foreach{
        q =>
          println("writecompile")
          org.fayalite.repl.JsREPL.writeCompile(q)
      }

    }
    val lcl = JsREPL.line()
   // if (lcl != "") println ( "have respnse for output " + lcl)
    val res = ParseResponseDebug(lcl)
//    println("sending response" + res.json)
    ref ! TextFrame(res.json)
  }
}
