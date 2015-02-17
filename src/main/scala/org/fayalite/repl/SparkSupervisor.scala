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
import scala.collection.mutable.{Map => MMap}

import scala.util.{Failure, Success, Try}
import org.fayalite.repl.REPL._


/**
 * Main bottleneck for communicating with driver
 * and handling traffic to REPL objects. Yes it's wrong to put the
 * repls and drivers all together but this can be addressed later
 * It's hardly a scaling issue so long as the driver is adequate.
 */
class SparkSupervisor()(implicit masterIntp: SparkIMain = null) extends Actor with Logging {

  def receive = {
    case si @ SuperInstruction(code, replId, userId, notebookId, clientPort: Int) =>
      logInfo(s"Evaluate received: $si")

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
      //        SparkSupervisor.repls = repls
            case _ =>
              val stdOut = r.run(code)
              logInfo("SparkSupervisor output of code run "  +stdOut)
              replSubscribers.foreach{
                case (id, subscriber) => subscriber ! Output(stdOut.toString, si)
              }
          }
        case None =>
          logInfo("New repl under id " + replId)
          val sparkManager = new SparkREPLManager(replId)
          repls(replId) = sparkManager
       //   SparkSupervisor.repls = repls
          val stdOut = sparkManager.run(code)
          logInfo("SparkSupervisor output of code run "  +stdOut)
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
object SparkSupervisor{

  //Debug
  var repls : Map[Int, SparkREPLManager] = _
  var replSubscribers : MMap[Int, ActorRef] = MMap()

  def superInstruct(si: SuperInstruction) = {


  }


}
