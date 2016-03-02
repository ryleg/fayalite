package org.fayalite.layer

import java.io.File

import akka.actor.ActorRef
import com.github.tototoshi.csv.CSVParser
import org.fayalite.repl.{ JsREPL}
import org.fayalite.ui.ParseServer
import org.fayalite.util.{JSON}
import spray.can.websocket.frame.TextFrame

import fa._

import scala.concurrent.Future
import scala.util.{Failure, Try}

object MessageParser {

  case class ParseRequest(
                           code: String, //Array[String]

                             // tab: Option[String],
                         //  requestId: String,
                           cookies: String,
                         requestId: String
                          // fileRequest: Option[Array[String]],
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
                               kvp: Array[String] = Array(
                               "Upload CSV | Click Here" //,
                             //  "Salesforce API Key Input | your_api_key_here",
                           //    "Remote CSV/JSON HTTP, S3, HDFS URL Input | http://yourcsv.com/yourcsv.csv"//,
                          //     "Company Name Key | company_name",
                               )
                                 )

  import ammonite.ops._

  val target: Path = cwd / 'secret / 'csvs

  def getCSVs: Seq[String] = Try{
    new File(target.toString()).listFiles().map{_.toString.split("/")
      .last}.toSeq
  }.getOrElse(Seq())

  // obs(csv)s

  val metadata = scala.collection.mutable.Map[String, String]()

  def parse(e: String) = {
    new CSVParser(com.github.tototoshi.csv.defaultCSVFormat).parseLine(e)
  }

  def TryPrint[T](f: => T) = {
    val ret = Try{f}
    ret match {
      case Failure(e) => e.printStackTrace()
      case _ =>
    }
    ret
  }

  def queueJob(cnt: String) = {
    println("queuing job ")
/*
    val fnm =  target.toString + "/" + cnt
    println("texting file " + fnm)
    val res = SparkRef.run("$sc.textFile(" + fnm +
      ").first()"
    , true)
    println("first " + res.toString())
    /*val h = parse(hdr)
    println("parsed " + h)
    val p = c.map{parse}
    println("first parsed " + p.first())
    val ret = h.mkString(",")
    println("headrs " + ret)
    */
    val ret = res
    metadata(cnt) = ret
    ret // change this to a func that generalizes concept
    // of recalculating previous value. this should be a request
    // against a generic rdd that does some operation like run a job
    // and has re-run conditions.*/
  }

  def parseBottleneck(msg: String, ref: ActorRef, ps: ParseServer) = {

  //  println("parse bottlenck " + msg)
    val pr: Option[ParseRequest] = None //Try{msg.json[ParseRequest]}.toOption

    var metadataret = ""

    Try {
      pr.foreach {
        q =>

          if (q.code.startsWith("@fa")) {

        //    println(q.code)

            val cd: String = q.code.drop(3)

            val cntr = cd.drop(4)
            if (cd.startsWith("sjs")) {
              metadataret += "sjs:" + JsREPL.inject(cntr)
            }
            if (cd.startsWith("req")) {
              println("request " + cd)
              if (!metadata.contains(cntr)) Future{queueJob(cntr)}
              metadataret += "req:" + metadata.getOrElse(cntr, "")
            }
          } else {

            val j = q.code.split("\\|")

            if (j.nonEmpty) {
              val fnm = j.head
              val cnt = j.tail.mkString("|")
              if (fnm.length > 3) {
                println("code j " + j.toList)
                println("mkfile " + fnm)
                //      mkdir(target)
                scala.tools.nsc.io.File({
                  target / RelPath(fnm)
                }.toString)
                  .writeAll(cnt)
               Future{queueJob(fnm)}

                /*

              write.over(
                target / RelPath(fnm),
                cnt
              )*/
                // file handle errors! WARNING ammonite is fucked up
                // and cant handle large num open files, not sure if this is the line
                // or what
              }
            }
          }
/*

          val c = q.cookies.split("&token_type").headOption
          // println(c)
          val aj = c.map {
            _.split("queryString=access_token=").last
          }
          //      println(j)
          val ck = aj.get
*/
/*

          val csvs = ls.!!(target / RelPath(ck)).toList.map {
            zz =>
              zz.segments.last + "|" + read(zz)
          }
*/



      }
    } match {
      case Failure(e) =>
        e.printStackTrace()
      case _ =>

    }

     val isAuthed = pr.exists{
      qq =>
        //println("qq " + qq)
        /*  qq.cookies.split(";").collectFirst{
             case x if x.split(":").head == "accessToken" =>
           x.split(":").tail.mkString}.exists{
             ps.authedTokens.contains
           }*/
    //    println("qqc " + qq.cookies)



           val c = qq.cookies.split("&token_type").headOption
       // println(c)
        val j = c.map{_.split("queryString=access_token=").last}
       //      println(j)
      j.exists{
               z =>
                 ps.authedTokens.contains(z) // mk authtoken to email db map.
           }

/*        qq.code.foreach{
        q =>
          println("writecompile")
          org.fayalite.repl.JsREPL.writeCompile(q)
      }*/

    }


  //println("isauth " + isAuthed)
  //  val lcl = JsREPL.line()
   // if (lcl != "") println ( "have respnse for output " + lcl)
    val res = if (isAuthed) ParseResponseDebug(Array(
    "Upload CSV | Click Here",
    "CSVs|" + getCSVs.mkString(","),
    "meta|" + metadataret


  )) else ParseResponseDebug(Array())
    //if (isAuthed)    println("sending response" + res.json)
   // ref ! TextFrame(res.json)
  }
}
