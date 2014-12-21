package org.fayalite.repl

import java.io.{InputStreamReader, BufferedReader, PipedOutputStream, PipedInputStream}

import _root_.scala.tools.nsc.interpreter._

import akka.actor.{ActorRef, Actor}
import org.apache.spark.repl.{SparkILoop}
import org.fayalite.repl.JSON._
import org.fayalite.repl.REPL._

import scala.concurrent.Future

import _root_.scala.concurrent.Future


object HackREPLManager {

  case class HackREPLMessage(code: String)
  case class Register(user: String)

}

/**
 * Spark-notebook is using the stdout as a java.io bytestream to output
 * over a websocket to the notebook. Instead of wrapping the stdin in a bytestream
 * it wants to use the IR.Result class to support better error handling
 * The error handling mechanism in SparkILoop is different from that of HackSparkILoop
 * these should ideally be merged into a single call that brings both error funcs
 * and their output to visible.
 *
 * Ideally client Actor should receive all messages of output from HackSparkILoop and
 * automatically write them to the PrintWriter in
 */
import HackREPLManager._


class HackREPLManager extends Actor{

  var subscribedClients = Set[ActorRef]()

  val replInputSource = new PipedInputStream()
  val replInputSink = new PipedOutputStream(replInputSource)
  val br = new BufferedReader(new InputStreamReader(replInputSource, "UTF-8"))

  val replOutputSink = new PipedOutputStream()
  val replOutputSource = new PipedInputStream(replOutputSink)

  val pw = new JPrintWriter(replOutputSink)
  val nrepl = new SparkILoop(Some(br), pw, None)

//  val settings = nrepl.multiplexProcess(Array(""))

//  val maybeFailed = nrepl.multiplexProcess(settings)

  val interp = nrepl.intp

  def readLoopUpdate() = {
    while (true) {
      val output = read()
      if (output != "") subscribedClients.foreach{c => c ! REPLOutput(output)}
    }
  }

  val readFailed = Future {readLoopUpdate()}

  def receive = {
    case code: String => sender ! interp.interpret(code)
    case Register(user) => subscribedClients = subscribedClients ++ Set(sender)
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


