package org.fayalite.repl

import java.io._

import akka.actor.{Props, ActorSystem, Actor}
import scala.concurrent._
import JSON._

import scala.concurrent.Future
import scala.tools.nsc.interpreter.ILoop
import akka.actor._
import scala.concurrent.Await
import akka.pattern.ask
import akka.util.Timeout
import scala.concurrent.duration._
import REPL._


/*

class REPLManager(useSparkILoop: Boolean = false) extends REPLManagerLike{


  val iloop = new ILoop(br, pw)
  val plex = new LoopSettings(iloop)
   val plexRun = Future { plex.run() }

}
*/
