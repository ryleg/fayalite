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
                           fileRequest: Option[Array[String]]
                           )
  case class ParseResponse(
                            classRefs: Option[Array[String]] =
                            Some(fileGraphStatic),
                            classContents: Option[Map[String, String]] = None,
                            heartBeatActive: Boolean = true
                            )

  val fileGraphStatic =  FSMan.fileGraph()
  /*
  G1 G2
   */
  def parseBottleneck(msg: String, ref: ActorRef) = {
    println("parse bottlenck " + msg)
    Try {
      implicit val formats = JSON.formats
      val pmsg = JSON.parse4s(msg).extract[ParseRequest]
      // val refs = fileGraphStatic

    }.toOption
    val res = ParseResponse() //classRefs = Some(refs)
    println("sending response" + res.json)
    ref ! TextFrame(res.json)
  }
}