package org.fayalite.layer

import akka.actor.ActorRef
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
                          code: Option[String]
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
  case class Response(
                       classRefs: Option[Array[String]] = None,
                       files: Option[Array[FileIO]] = None,
                       replIO: Option[RIO] = None,
                       out: Option[String] = None
                       )

  //val fileGraphStatic =  FSMan.fileGraph()
  /*
  G1 G2
   */
  def parseBottleneck(msg: String, ref: ActorRef) = {
    println("parse bottlenck " + msg)
    val pr = Try{msg.json[ParseRequest]}.toOption
    pr.foreach{
      _.code.foreach{
        q => org.fayalite.repl.JsREPL.writeCompile(q)
      }
    }
      // val refs = fileGraphStatic
    val res = Response() //classRefs = Some(refs)
    println("sending response" + res.json)
    ref ! TextFrame(res.json)
  }
}
