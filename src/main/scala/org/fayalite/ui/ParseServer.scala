package org.fayalite.ui

import akka.actor.{ActorRef, Actor, Props}
import org.fayalite.Fayalite
import org.fayalite.aws.ServerManager
import org.fayalite.db.SparkDBManager
import org.fayalite.layer.{MessageParser, FSMan}
import org.fayalite.ui.oauth.OAuth.OAuthInfo
import org.fayalite.ui.ws.Server.TestEval
import org.fayalite.util._
import org.json4s.JsonAST.JObject
import spray.can.websocket.frame.{BinaryFrame, TextFrame}
import Fayalite._
import scala.concurrent.Future
import scala.io.Source
import scala.util.{Failure, Success, Try}

object ParseServer {
/*
The high decibels.
color me black
djohnson2009@yahoo

 */
  def main(args: Array[String]) {
    val sr = new SimpleRemoteServer({new ParseServer()} ,20000)
    Thread.sleep(Long.MaxValue)
  }


  def getField(msg: String, field: String) = {
    implicit val formats = JSON.formats
    val pj = JSON.parse4s(msg)
    Try{(pj \\ field).extract[String]}.toOption
  }

  case class AWSCredentials(
                           access: Option[String] = None,
                           secret: Option[String] = None,
                           pem: Option[String] = None
                             )

  case class UserCredentials(
                            email: String,
                            aws: AWSCredentials,
                            updateTime: Long = System.currentTimeMillis()
                              )

  def evalUIFrame = {
    val sampleJS = Source.fromFile("./app-dynamic/target/scala-2.11/fayalite-app-dynamic-fastopt.js")
      .mkString
    val msg = JSON.caseClassToJson(TestEval("eval", sampleJS))
    val frame = TextFrame(msg)
    frame
  }


}

class ParseServer extends Actor{
  import ParseServer._
  import MessageParser._

  def receive = {
    // TODO : Remove all this, switch to case class from shared submodule
    case xs if xs == "reload" || xs == "init" =>
        println("load")
        val sampleJS = Source.fromFile("./app-dynamic/target/scala-2.11/fayalite-app-dynamic-fastopt.js")
          .mkString
        val msg = JSON.caseClassToJson(TestEval("eval", sampleJS))
        val frame = TextFrame(msg)
        sender() ! frame
    case msg: String =>
      Try{Future{parseBottleneck(msg, sender())}}
      //sender() ! TextFrame(ret)
      //response.foreach{r => sender() ! TextFrame(r)}
    case TextFrame(msg) =>
      val umsg = msg.utf8String
      println("parse message" + umsg)
    //  sender() ! TextFrame("parsed")
    case BinaryFrame(dat) =>
      //TODO : Check how to binary serialize dom event input classes here.
      println("binaryframe.")
    case x => println("prse" + x)
  }
}