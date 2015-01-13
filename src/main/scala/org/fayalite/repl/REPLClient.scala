package org.fayalite.repl

/**
 * Created by ryle on 1/12/15.
 */

import akka.actor.{Actor, ActorRef}
import akka.io.Tcp.{Write, Received}
import akka.util.{ByteString, Timeout}
import scala.concurrent.duration._
import akka.pattern.ask

import scala.util.{Failure, Success, Try}
import REPL._

class REPLClient extends Actor {

  def receive = {


  }

}
