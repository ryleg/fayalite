package org.fayalite.repl

import akka.actor.{Actor, ActorRef}
import akka.io.Tcp.{Write, Received}
import akka.util.{ByteString, Timeout}
import org.apache.spark.repl.SparkIMain
import org.fayalite.util.HackAkkaDuplex
import org.fayalite.util.RemoteAkkaUtils.RemoteActorPath
import scala.concurrent.duration._
import akka.pattern._

import scala.util.{Failure, Success, Try}
import REPL._


/**
 * Main bottleneck for communicating with notebook jvm processes
 * and handling traffic to REPL objects. Yes it's wrong but it's quick.
 * @param duplex : Connector to client
 */
class Supervisor(duplex: HackAkkaDuplex)(implicit masterIntp: SparkIMain = _) extends Actor {


  var repls : Map[Int, SparkREPLManager] = Map()

  var replSubscribers : Map[Int, List[ActorRef]] = Map()

  def subscribe(client: ActorRef, replId: Int) = {
    val subscribers = client :: replSubscribers.getOrElse(replId, List())
    replSubscribers = replSubscribers ++
      Map(replId -> subscribers)
  }

  def receive = {
    case Start(clientPort, replId) =>
      val client = duplex.startClient(clientPort)
      repls.get(replId) match {
        case Some(repl) =>
        // send ack todo
        case None =>
          val sparkManager = new SparkREPLManager(replId)
          repls = repls ++ Map(replId -> sparkManager)
      }
      subscribe(client, replId)
    case Evaluate(code, replId) =>
        val stdOut = if (replId == 0 && masterIntp != null) {
          masterIntp.interpret(code).toString //god no
        } else {
          val repl = repls.get(replId).get //no
          val (res, stdOut) = repl.run(code)
          stdOut
        }
        replSubscribers.get(replId).get.foreach{
          subscriber => subscriber ! Output(stdOut)
        }
  }
}
