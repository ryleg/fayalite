package org.fayalite.ui

import akka.actor.{Actor, Props}
import org.fayalite.Fayalite
import org.fayalite.aws.ServerManager
import org.fayalite.db.SparkDBManager
import org.fayalite.ui.oauth.OAuth.OAuthInfo
import org.fayalite.util._
import spray.can.websocket.frame.{BinaryFrame, TextFrame}
import Fayalite._
import scala.util.{Failure, Success, Try}

object ParseServer {


  def main(args: Array[String]) {
    val sr = new SimpleRemoteServer({new ParseServer()} ,20000)
    Thread.sleep(Long.MaxValue)
  }
  case class ParseRequest(
                         tab: String,
                         cookies: String
                           ) {
    def splitCookies=cookies.split(";").map{_.split("=").map{_.trim} match { case Array(x,y) => (x,y)}}
    def apply(key: String) = splitCookies.toMap.get(key)
    def accessToken = apply("access_token")

  }


  // switch to rolling s3 once this is more stable
  // instantly deprecated from inception.
  @deprecated
  val tempOAuthLocalFileStream = Common.home + "/oauthfstream"

  val oauthDB = SparkDBManager.getOAuthTable

  case class ParseResponse(flag: String, email: String)

}

class ParseServer extends Actor{
  import ParseServer._

  def receive = {
    case msg: String =>
      println("message : " + msg)
      implicit val formats = JSON.formats
      val pmsg = JSON.parse4s(msg).extract[ParseRequest]
      val response = pmsg.tab match {
        case "Servers" =>
          Some(ServerManager.requestServerInfo())
        case _ => println("no tab match")
          None
      }
      val em = pmsg.accessToken.map{
        a =>
          val oauth = SparkDBManager.
      }
      val ret = ParseResponse("auth", em.getOrElse("guest@login.com")) : String
      sender() ! TextFrame(ret)
      //response.foreach{r => sender() ! TextFrame(r)}
    case TextFrame(msg) =>
      val umsg = msg.utf8String
      println("parse message" + umsg)
    //  sender() ! TextFrame("parsed")
    case BinaryFrame(dat) =>
      //TODO : Check how to binary serialize dom event input classes here.
      println("binaryframe.")
    case oai : OAuthInfo =>
      println("oauth parse " + oai)
      tempOAuthLocalFileStream.append(oai)
      SparkDBManager.oauthInsert(oai)
  }
}
