package fa

import java.io.File
import java.rmi.server.RemoteServer

import akka.actor.ActorRef
import org.fayalite.util.{RemoteClient, SimpleRemoteServer}

/**
 * Created by ryle on 8/12/2015.
 */
object SCPR {

  import java.io.File

  def recursiveListFiles(f: File): Array[File] = {
    val these = f.listFiles
    these ++ these.filter(_.isDirectory).flatMap(recursiveListFiles)
  }

  def getCode: Array[CodeUpdate] = {
    recursiveListFiles(new File(".")).filterNot {
      z => List("target", ".git",".DS_Store", ".idea").exists {
        t =>
          fileQualifier(z, t)
      }
    }
  } map { q => CodeUpdate(q.getCanonicalPath.replaceAll("""\""", "/"), scala.io.Source.fromFile(q).getLines().mkString("\n"))}

  def fileQualifier(z: File, t: String): Boolean = {
    z.getAbsolutePath.split("\\\\").contains(t) || z.getAbsolutePath.contains(".dll") ||
      z.isDirectory
  }

  case class CodeUpdate(path: String, contents: String)

  def main(args: Array[String]) {

    import fa._

    new RemoteClient() getServerRef(20000)


  }

}
