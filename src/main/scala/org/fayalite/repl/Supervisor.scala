package org.fayalite.repl

import akka.actor.{ActorSystem, Actor, ActorRef}
import akka.io.Tcp.{Write, Received}
import akka.util.{ByteString, Timeout}
import org.apache.spark.Logging
import org.apache.spark.repl.SparkIMain
import org.fayalite.util.{SparkReference, HackAkkaDuplex}
import org.fayalite.util.RemoteAkkaUtils.RemoteActorPath
import scala.concurrent.Future
import scala.concurrent.duration._
import akka.pattern._
import scala.collection.mutable.{Map => MMap, Map}

import scala.util.{Failure, Success, Try}
import org.fayalite.repl.REPL._


/**
 * Main bottleneck for communicating with notebook jvm processes
 * and handling traffic to REPL objects. Yes it's wrong but it's quick
 * and it works with case classes
 * @param duplex : Connector helper method for creating connection
 *               to client and host of main server actor system
 */
class Supervisor(duplex: HackAkkaDuplex)
                (implicit masterIntp: SparkIMain = null) extends Actor with Logging {

  SparkReference.getSC

  var repls : MMap[Int, SparkREPLManager] = MMap()

  var replSubscribers : MMap[Int, ActorRef] = MMap()
  var clientAS : MMap[Int, ActorSystem] = MMap()

  def subscribe(client: ActorRef, clientPort: Int) = {
    replSubscribers(clientPort) = client
  }


  def keepAlive() = Try {
    Future {
      while (true) {
        replSubscribers.foreach {
          case (port, r) => r ! Heartbeat(port)
        }

        val beatTime = System.currentTimeMillis()
        Thread.sleep(10000)

        val portDelta = replSubscribers.toList.map {
          case (port, r) => heartbeats.get(port) match {
            case Some(prevTime) =>
              val delta = beatTime - prevTime
              (port, delta)
            case None => (port, Long.MaxValue)
          }
        }

        portDelta.filter {
          _._2 > 35000L
        }.foreach {
          case (port, delta) =>
            logInfo("Heartbeat not received from port " + port + " in 10s --- FakeRemoving subscriber")
          //  replSubscribers.remove(port)
        }

      }
    }
  }

  val keptAlive = keepAlive()

  var heartbeats : scala.collection.mutable.Map[Int, Long] = scala.collection.mutable.Map()


  def receive = {
    case Heartbeat(port) => {
      logInfo("Received heartbeat on port " + port)
      heartbeats(port) = System.currentTimeMillis()
    }
    case si @ SuperInstruction(code, replId, userId, notebookId, clientPort: Int) =>
      logInfo(s"Evaluate received: $si")
      replSubscribers.get(clientPort) match {
        case Some(client) =>
        case None =>
          val tempASClientPort = 15000 + 100 * scala.util.Random.nextInt(15) + 42
          val client = duplex.startClient(tempASClientPort, clientPort)
          subscribe(client, clientPort)
          logInfo("Subscribing " + client + " clientPort: " + clientPort)
          Supervisor.replSubscribers = replSubscribers
      }

      // todo: not this
      val repl = repls.get(replId)
      repl match {
        case Some(r) =>
          // send ack todo
          code match {
            case c if c.startsWith(":startcp=") =>
              r.iloop.intp.close()
              logInfo("Found existing repl under id " + replId + " and destroyed it for a new cp")
              val sparkManager = new SparkREPLManager(replId, c.replaceAll(":startcp=", ""))
              repls(replId) = sparkManager
              Supervisor.repls = repls
            case _ =>
              val stdOut = r.run(code)
              logInfo("Supervisor output of code run "  +stdOut)
              replSubscribers.foreach{
                case (id, subscriber) => subscriber ! Output(stdOut.toString, si)
              }
          }
        case None =>
          logInfo("New repl under id " + replId)
          val sparkManager = new SparkREPLManager(replId)
          repls(replId) = sparkManager
          Supervisor.repls = repls
          val stdOut = sparkManager.run(code)
          logInfo("Supervisor output of code run "  +stdOut)
          replSubscribers.foreach{
            case (id, subscriber) => subscriber ! Output(stdOut.toString, si)
          }
      }
    }
}
/* if (replId == -1 && masterIntp != null) {
    masterIntp.interpret(code).toString //god no
  } else {*/
//          val res = repl.run(code)
//          res
//        }
object Supervisor{

  //Debug
  var repls : Map[Int, SparkREPLManager] = _
  var replSubscribers : MMap[Int, ActorRef] = MMap()

  def superInstruct(si: SuperInstruction) = {


  }


}
