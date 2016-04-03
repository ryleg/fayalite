
package org.fayalite.util.windows

import scala.sys.process._

class PuttyExec {

  val puttyWinExecPath = "C:\\Program Files (x86)\\PuTTY\\putty.exe"

  val tempScriptPath = "pscript"

  import fa._
  def run(s: String) = {
    writeToFile(tempScriptPath, s)
    Seq(puttyWinExecPath,
     "-m", tempScriptPath, "-T", "-A", "-l", "ubuntu", "fayalite.org").!!
  }
  //def run(s: Seq[String]) = run(s.mkString("\n"))

}

object PuttyExec {
  def main(args: Array[String]) {
    new PuttyExec().run("ls")
  }
}

