package org.fayalite.ui

import java.awt.Color
import org.fayalite.util.JSON
import org.scalatest.FunSuite
import spray.can.websocket.frame.{TextFrame, BinaryFrame}

import scala.io.Source


/**
 * Created by ryle on 11/9/2014.
 */

class TestRemoteEval extends FunSuite {

  case class TestEval(flag: String, code: String)
  val cli = new WebsocketPipeClient()

  def sendMessage(sampleJS: String) = {
    val msg = JSON.caseClassToJson(TestEval("eval", sampleJS))
    val frame = TextFrame(msg)
    cli.sendFrame(frame)
  }
/*
  test("Testing simple eval") {

    sendMessage("println(111);")

  }*/

  test("Testing eval of separate scala.js project") {

    val sampleJS = Source.fromFile("./app-dynamic/target/scala-2.10/fayalite-app-dynamic-fastopt.js")
      .mkString

    val msg = JSON.caseClassToJson(TestEval("eval", sampleJS))


      val frame = TextFrame(msg)
    //    Thread.sleep(3000)
        cli.sendFrame(frame)
   //   }
    }
}


