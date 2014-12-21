package org.fayalite.repl

import java.io._

import akka.actor.Actor
import org.fayalite.repl.JSON.{REPLHistory, REPLRead, REPLWrite, REPLWriteHistory}
import org.fayalite.repl.REPL._

import scala.concurrent.Future


/*

trait REPLManagerLike extends Actor{

  val replInputSource = new PipedInputStream()
  val replInputSink = new PipedOutputStream(replInputSource)
  val br = new BufferedReader(new InputStreamReader(replInputSource, "UTF-8"))

  val replOutputSink = new PipedOutputStream()
  val replOutputSource = new PipedInputStream(replOutputSink)

  val pw = new PrintWriter(replOutputSink)

//  val plex : PlexLike = ???

 // val plexRun = Future { plex.run() }



  def receive = {

    case x : REPLRead => println("attempting read"); sender ! read()
    case REPLWrite(text) => println("attempting write of " + text); write(text)
    case REPLHistory(depth) => println("attempting get repl history")
      sender ! allHistory.takeRight(depth)
    case REPLWriteHistory(depth) => println("attempting get repl write history")
      sender ! allWriteHistory.takeRight(depth).mkString("\n")

  }

  val allWriteHistory = scala.collection.mutable.MutableList[String]()


  def write(stringData : String) : Unit = {

    allWriteHistory += stringData
    val byteData = stringData.map {
      _.toChar
    }.toCharArray.map {
      _.toByte
    }
    replInputSink.write(byteData)
    replInputSink.flush()
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
*/
