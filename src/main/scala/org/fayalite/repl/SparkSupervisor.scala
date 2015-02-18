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

import org.apache.spark.executor.Executor

object SparkSupervisor{
  //Debug
  val supervisorPort = 31415
  @volatile var repls : MMap[Int, SparkREPLManager] = MMap()
  case class Start(id: Int, classPath: Option[String])
  case class Run(code: String, id: Int, async: Boolean = true)
  case class Completion(code: String, id: Int)
  case class Kill(id: Int)
  case class RunMaster(code: String, async: Boolean = true)
}


/*
        case x@(_: BinaryFrame | _: TextFrame) =>

 */
/**
 * Main bottleneck for communicating with driver
 * and handling traffic to REPL objects. Yes it's wrong to put the
 * repls and drivers all together but this can be addressed later
 * It's hardly a scaling issue so long as the driver is adequate.
 * Quick semi-fix would be to make some more actors, one spawned
 * per REPL.
 */
class SparkSupervisor()(implicit masterIntp: Option[SparkIMain] = None) extends Actor with Logging {
  import SparkSupervisor._
  def receive = {
    case Start(id, classPath) =>
      sender ! (
        repls.get(id) match {
        case Some(repl) => false
        case None =>
          val sparkManager = new SparkREPLManager(id, classPath)
          repls(id) = sparkManager
          true
      })
    case RunMaster(code, async) =>
      sender ! masterIntp.map{
        intp => intp.interpret(code)
      }
    case Run(code, id, async) =>
      sender ! {repls.get(id).map {
        repl =>
          repl.run(code, doRead = !async)
        }
      }
    case Kill(id) =>
      val success = {repls.get(id).map{
        repl => repl.iloop.intp.close();
      }}
      sender ! success
    }
}
