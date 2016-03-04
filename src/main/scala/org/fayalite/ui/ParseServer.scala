package org.fayalite.ui

import akka.actor.Actor
import fa.SCPR.CodeUpdate
import org.fayalite.layer.MessageParser
import org.fayalite.ui.oauth.OAuth.OAuthInfo
import org.fayalite.ui.ws.Server.TestEval
import org.fayalite.util._
import spray.can.websocket.frame.{BinaryFrame, TextFrame}

import scala.io.Source
import scala.util.Try

object ParseServer {


  def main(args: Array[String]) {
    val sr = new SimpleRemoteServer({new ParseServer()} ,20000)
    Thread.sleep(Long.MaxValue)
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

 // import ammonite.ops._
 // val oauthLog = cwd / 'secret / 'oauth

}

class ParseServer extends Actor{
  import MessageParser._

  println("PARSE ACTOR PATH : " + this.self.path)
  val authed = List()
  @volatile var authedTokens = Array[String]("")

  def receive = {
    // TODO : Remove all this, switch to case class from shared submodule
    case o : OAuthInfo =>
      println(o)
      if (
        authed.contains(o.authResponse.email) ||
        o.authResponse.email.endsWith("")
      ) {
        println("authed")
        authedTokens :+= o.accessToken
      }
    case cu : Array[CodeUpdate] =>
      println("codeupdate ")
      cu.foreach{
        q => import ammonite.ops._ ;
          write.over(cwd / RelPath(q.path), q.contents)
      }

    case xs if xs == "reload" || xs == "init" =>
        println("load")
       /* val sampleJS = Source.fromFile("./app-dynamic/target/scala-2.11/fayalite-app-dynamic-fastopt.js")
          .mkString
        val msg = JSON.caseClassToJson(TestEval("eval", sampleJS))
        val frame = TextFrame(msg)
        sender() ! frame*/
/*    case o @ OAuthInfo(at, ar) =>
      oauthLog jsa o*/
    case msg: String =>
     val ret: Unit =  Try{parseBottleneck(msg, sender(), this)}.toOption.getOrElse("bottleneck failed")
    //  sender() ! TextFrame(ret)
      //response.foreach{r => sender() ! TextFrame(r)}
    case TextFrame(msg) =>
      val umsg = msg.utf8String
 //     println("parse message" + umsg)
      sender() ! TextFrame("parsed")
    case BinaryFrame(dat) =>
      //TODO : Check how to binary serialize dom event input classes here.
      println("binaryframe.")
    case x => println("prse" + x)
  }
}