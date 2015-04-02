package org.fayalite.util.dsl

import akka.util.{ByteString, Timeout}
import org.fayalite.util.JSON

import scala.concurrent.Await
import scala.concurrent.duration._

import akka.actor.{ActorSystem, ActorRef}
import akka.pattern.ask
import org.fayalite.repl.REPL._


import JSON._

import scala.reflect.ClassTag


trait AkkaExt {

  implicit val akkaTimeout = Timeout(5 seconds)

  implicit class ActorExt(actor: ActorRef) {

    def ??[T](msg: Any) = {
      (actor ? msg).getAs[T]
    }


    def ??[T](msg: Any, timeout: Int=3) = {
      (actor ? msg).getAs[T](timeout)
    }

    /**
     * Listener actor receives actual data.
     */
      def read() : Unit = {
      //  actor ! ByteString(JSON.caseClassToJson(REPLMessage(instruction = "read")))
      }

      def write(text: String) = {
       // actor ! ByteString(JSON.caseClassToJson(REPLMessage(instruction = "write", text = text)))
      }

  }

  implicit class actorAccessories(as: ActorSystem) {
    def getByPath(actorPath: String) = {
      val checkExistingActor = as.actorSelection(actorPath).resolveOne()
      val foundResult = Await.ready(checkExistingActor, 1.seconds)
      checkExistingActor.value.get
    }
  }


}