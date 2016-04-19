
package org.fayalite.util.windows

import java.io.File

import scala.sys.process._

class PuttyExec {

}

object PuttyExec {


  val winStart = Seq("cmd.exe", "START", "/B")
  val dotHidden = new File(".hidden")

  val fppk = new File(dotHidden, "fa.ppk")

  val puttyWinExecPath = "C:\\Program Files (x86)\\PuTTY\\putty.exe"
  val pagent = "C:\\Program Files (x86)\\PuTTY\\pageant.exe"

  val tempScriptPath = "pscript"

  import fa._
  def run(s: String) = {
    writeToFile(tempScriptPath, s)
    Seq(puttyWinExecPath,
      "-m", tempScriptPath, "-T", "-A", "-l", "ubuntu", "fayalite.org").!!
  }
  //def run(s: Seq[String]) = run(s.mkString("\n"))

  def ensurePagent = println ( (
  //  winStart ++
    Seq(pagent, fppk.getCanonicalPath)
    ).!!
  )
  def main(args: Array[String]) {
    F { ensurePagent }
      Thread.sleep(1500)
    run(
      Seq(
        "wget https://s3-us-west-1.amazonaws.com/fayalite/bootstrap.sh",
        "chmod +x bootstrap.sh",
          "bootstrap.sh & disown").mkString("\n")
    )
  }
}

