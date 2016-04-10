package org.fayalite.layer

import org.fayalite.ui.{ParseServer, WebsocketPipeClient}

/**
 * Not really sure I like this. Needs more RX
 */
object FSObs {

  def main(args: Array[String]): Unit = {

    val c = new WebsocketPipeClient()
    c.sendFrame(ParseServer.evalUIFrame)
    var lt = System.currentTimeMillis()
    observeDirectory(
      "/Users/ryle/Documents/repo/fayalite/app-dynamic/target/scala-2.11",
      _ => {//println("callback") ;
        val ct = System.currentTimeMillis()
        if (ct - lt > 5000) {
          println("sent frame")
           lt = ct; c.sendFrame(ParseServer.evalUIFrame)
        }
        }
    )

  }

  def observeDirectory(dir: String, callBack: String => Unit = (pt: String) => ()) = {
    import akka.actor.ActorSystem
  /*  import com.beachape.filemanagement.MonitorActor
    import com.beachape.filemanagement.RegistryTypes._
    import com.beachape.filemanagement.Messages._

    import java.io.{FileWriter, BufferedWriter}

    import java.nio.file.Paths
    import java.nio.file.StandardWatchEventKinds._

    implicit val system = ActorSystem("actorSystem")
    val fileMonitorActor = system.actorOf(MonitorActor(concurrency = 2))
/*
    val modifyCallbackFile: Callback = {
      path => println(s"Something was modified in a file: $path")
      callBack(path.toString)

    }*/
    val modifyCallbackDirectory: Callback = { path =>
      println(s"Something was modified in a directory: $path")
      callBack(path.toString)
    }

    val desktop = Paths get dir
    //val desktopFile = Paths get "/Users/ryle/test"
*/
/*
    println("desktop " + desktop.toString)
/*
/*


      This will receive callbacks for just the one file
     */
    fileMonitorActor ! RegisterCallback(
      ENTRY_MODIFY,
      None,
      recursive = false,
      path = desktop,
      modifyCallbackFile)

*/

    /*
      If desktopFile is modified, this will also receive a callback
      it will receive callbacks for everything under the desktop directory
    */
    fileMonitorActor ! RegisterCallback(
      ENTRY_MODIFY,
      None,
      recursive = true,
      path = desktop,
      modifyCallbackDirectory)
*/

/*

    //modify a monitored file
    val writer = new BufferedWriter(new FileWriter(desktopFile.toFile))
    writer.write("Theres text in here wee!!")
    writer.close
*/

    // #=> Something was modified in a file: /Users/lloyd/Desktop/test.txt
    //     Something was modified in a directory: /Users/lloyd/Desktop/test.txt
  }
}