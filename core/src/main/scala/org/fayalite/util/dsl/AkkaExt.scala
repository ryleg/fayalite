package org.fayalite.util.dsl

import akka.util.{ByteString, Timeout}
import org.fayalite.util.JSON

import scala.concurrent.Await
import scala.concurrent.duration._

import akka.actor.{ActorSystem, ActorRef}
import akka.pattern.ask


import fa._
import scala.util.Random


/**
 * Why does Akka not have these?
 */
trait AkkaExt {

  def rport = Random.nextInt(50000) + 1500

  implicit val akkaTimeout = Timeout(5 seconds)

  implicit class ActorExt(actor: ActorRef) {

    def ??[T](msg: Any) = {
      ask(actor, msg).getAs[T]
    }


    def ??[T](msg: Any, timeout: Int=3) = {
      ask(actor, msg).getAs[T](timeout)
    }

  }

  implicit class actorAccessories(as: ActorSystem) {

    def enterToShutdown() = {
      readLine("Hit ENTER to exit ...\n")
      as.shutdown()
      as.awaitTermination()
    }

    def getByPath(actorPath: String) = {
      val checkExistingActor = as.actorSelection(actorPath).resolveOne()
      val foundResult = Await.ready(checkExistingActor, 1.seconds)
      checkExistingActor.value.get
    }
  }


}
