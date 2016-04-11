package org.fayalite.sjs

//import rx.ops.{DomScheduler, Timer}

//import scala.scalajs.js.Dynamic.{global => g}
import rx.ops.{DomScheduler, Timer}

import scala.scalajs.js.JSApp
import scala.scalajs.js.annotation.JSExport

object App extends JSApp {

  import scala.concurrent.ExecutionContext.Implicits.global
  import scala.concurrent.duration._

  implicit val scheduler = new DomScheduler()

  val heartBeat = Timer(15.seconds)

  @JSExport
  def main(): Unit = {
    println("SJS fayalite initialized")
  }
}

