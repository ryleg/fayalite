package org.fayalite.ui

import akka.actor.{Actor, Props}
import org.fayalite.Fayalite
import org.fayalite.aws.ServerManager
import org.fayalite.db.SparkDBManager
import org.fayalite.layer.FSMan
import org.fayalite.layer.FSMan.Graph
import org.fayalite.ui.oauth.OAuth.OAuthInfo
import org.fayalite.ui.ws.WSServer.TestEval
import org.fayalite.util._
import org.json4s.JsonAST.JObject
import spray.can.websocket.frame.{BinaryFrame, TextFrame}
import Fayalite._
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


  case class ParseRequest(
                         tab: Option[String],
                         requestId: Option[String],
                         cookies: String

                           ) {
    def splitCookies = {
      cookies.split(";").map {
        _.split("=").map {
          _.trim
        } match {
          case Array(x, y) => Some((x, y))
          case Array() => None
        }
      }.flatMap{ x => x}
    }
    def apply(key: String) = splitCookies.toMap.get(key)
    def accessToken = apply("access_token")

  }

  // switch to rolling s3 once this is more stable
  // instantly deprecated from inception.
/*  @deprecated
  val tempOAuthLocalFileStream = Common.home + "/oauthfstream"

  val oauthDB = SparkDBManager.oauthDB*/
/*
            val sampleJS = Source.fromFile("./app-dynamic/target/scala-2.11/fayalite-app-dynamic-fastopt.js")
                  .mkString
                val msg = JSON.caseClassToJson(TestEval("eval", sampleJS))
                val frame = TextFrame(msg)
                //    Thread.sleep(3000)
                sender() ! frame
 */
  case class ParseResponse(
                            flag: String,
                            email: String,
                  //          user: Option[UserCredentials]=None,
                            requestId: String = "0",
                            graph: Graph = Graph(List(), List())
                            )

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


  /*val userFStream = Common.home + "/userFStream"

  def loadUsers = {
    val ret = Try{userFStream.text.map{
      t =>
        implicit val formats = JSON.formats
        JSON.parse4s(t).extract[UserCredentials]
    }.cache()}.toOption.getOrElse(UserCredentials("fake", AWSCredentials(Some("yo"))).rdd)
    import scala.sys.process._
    val fstreamRDD = userFStream + "rdd"
    val deduped = ret.groupBy{_.email}.map{case (g, vs) => vs.maxBy(_.updateTime)}
    deduped.count()
    s"rm -rf $fstreamRDD".!!
    deduped.map{x => x : String}.repartition(1).saveAsTextFile(fstreamRDD)
    s"mv ${fstreamRDD + "/part-00000"} ${userFStream}".!!
    deduped
  }

  var users = loadUsers.cache()

  def addUpdateUser(ui: UserCredentials) = {
    val u = queryUser(ui)
    u.map{
      ua =>
        var awsPrime = ua.aws
        ui.aws.access.foreach{a => awsPrime = awsPrime.copy(access=Some(a))}
        ui.aws.secret.foreach{a => awsPrime = awsPrime.copy(secret=Some(a))}
        ui.aws.pem.foreach{a => awsPrime = awsPrime.copy(pem=Some(a))}
        val uiPrime = UserCredentials(ui.email, awsPrime)
        users = users.filter{
          _.email != ui.email
        }.union(uiPrime.rdd).cache().setName("users")

    }
    if (u.isEmpty) {
      users = users.union(ui.rdd)
    }
    userFStream.append(ui)
    u
  }

  def queryUser(ui: UserCredentials) = {
    users.filter{_.email == ui.email}.collect().sortBy{_.updateTime}.reverse.headOption
  }*/

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

  def receive = {
    case xs if xs == "reload" || xs == "init" =>
        println("load")
        val sampleJS = Source.fromFile("./app-dynamic/target/scala-2.11/fayalite-app-dynamic-fastopt.js")
          .mkString
        val msg = JSON.caseClassToJson(TestEval("eval", sampleJS))
        val frame = TextFrame(msg)
        sender() ! frame
    case msg: String =>
      println("message : " + msg)
      val cookies = getField(msg, "cookies")
      implicit val formats = JSON.formats
      val pmsg = JSON.parse4s(msg).extract[ParseRequest]

      val response = pmsg.tab.map{
          case "Servers" =>
            Some(ServerManager.requestServerInfo())
          case _ => println("no tab match")
            None
        }.flatten

/*        val em = pmsg.accessToken.map{
        SparkDBManager.queryAccessTokenToEmail
      }.flatten*/
/*

        val user = em.map{e =>
          val access = getField(msg, "awsAccess")
          val secret = getField(msg, "awsSecret")
          val pem = getField(msg, "pem")
          val messageHasUpdate = Seq(access,secret,pem).exists{_.nonEmpty}
          val awsc = AWSCredentials(access, secret, pem)
          val uc = UserCredentials(e, awsc)
          addUpdateUser(uc)
        }.flatten
*/

        val graph = pmsg.tab.filter{_ == "Editor"}.map{
          _ => FSMan.fileGraph()
        }

        val ret = ParseResponse(
          "auth",
          //em.getOrElse(
            "guest@login.com",
        //  user=user,
          requestId=pmsg.requestId.getOrElse("0"),
        graph=graph.getOrElse(Graph(List(),List()))
        ) : String
        println("response" + ret)
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
   //   tempOAuthLocalFileStream.append(oai)
  //    SparkDBManager.queryInfoResponse(oai)
    case x => println("prse" + x)
  }
}
/*
        waitforpastedata(elem, savedcontent);

 */