package org.fayalite.layer

import ammonite.ops._
import org.fayalite.ui.{ParseServer, WebsocketPipeClient}
import org.fayalite.util.RemoteClient

import scala.sys.process.Process

/**
 * This doesn't really work as promised, I mean, it sort of does
 * but looking at this makes me nervous.
 */
object Compile {

  def main(args: Array[String]) {
/*
    val rc = new WebsocketPipeClient()

    val stdOut = appDynamic()

    import rx._
    Obs(stdOut) {
      if (stdOut().contains("success") && stdOut().contains("Total time")) {
        rc.sendFrame(ParseServer.evalUIFrame);
        println("reload")
      }
    }*/


  }

/*  def appDynamic() = {

    import rx._

    val stdOut = Var("")
    val stdIn = Var("")

    Obs(stdOut, skipInitial = true) {
      println(stdOut())
      if (stdOut().contains("Set current project to fayalite-app-dynamic")) {
  //      stdIn() = "~fastOptJS\n"
      }
    }

    val pb = Process(Seq("sbt", "~fastOptJS"), new java.io.File("./app-dynamic"))
    import scala.sys.process.ProcessIO
    val pio = new ProcessIO({q  =>
      Obs(stdIn, skipInitial = true) {
        println("Writing : " + stdIn())
        q.write(stdIn().getBytes)
        q.flush()
      }
      ()},
      stdout => {
    //    val lines =
       //   while(true) {
         //   Thread.sleep(100)
            scala.io.Source.fromInputStream(stdout).getLines.foreach {
              l => stdOut() = l
            }
       //   }
        ()
     //   stdOut() = lines
       // lines
      },
      _ => ())
    pb.run(pio)

    stdOut
    //Rx{stdOut().contains("success") && stdOut().contains("Total time")}
  }*/

}
