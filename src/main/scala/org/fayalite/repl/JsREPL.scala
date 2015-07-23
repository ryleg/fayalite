package org.fayalite.repl

import java.io.{File, OutputStream}

import org.fayalite.io.TerminalEmulator
import rx.ops._
import scala.sys.process._
import scala.sys.process.ProcessIO
import rx._

  import rx._

  import scala.sys.process.ProcessIO
import scala.util.Random

/**
 * Hacked attempt to get something approaching a
 * Scala.js REPL
 * TODO : Match REPLLike trait TBI
 */
  object JsREPL {

  /**
   * Template for injecting code into.
   */
    val MAIN = {"package app.templ\n" +
        "import scala.scalajs.js.JSApp\n" +
        "import scala.scalajs.js.annotation.JSExport\n" +
        "object Inject extends JSApp {\n" +
        "@JSExport\n" + "def main(): Unit = {\n"
    }
    val MAIN_TERMINATION = "\n}\n}\n"

  implicit def strByte(s: String) : Array[Byte] = s.toCharArray.map{_.toByte}
/*

  /**
   * Single sbt process rebuilding templates
   */
    val pb = Process("""sbt""", new File("./tmp/template2"))
    val in = Var("fastOptJS\n")
    val out = Var("" -> "")
    var stdin_ : OutputStream = null.asInstanceOf[OutputStream]
    val pio = new ProcessIO(stdin =>
    {
      Obs(in, skipInitial=true){
      val q = in(); stdin.write(q.toCharArray.map{_.toByte})
        stdin_ = stdin
    }
      ()
    },
    stdout =>
      scala.io.Source.fromInputStream(stdout)
    .getLines.foreach { l =>
        out() = l -> out()._2
        println("stdout: " + l)
      },
      stderr => {
        scala.io.Source.fromInputStream(stderr)
          .getLines.foreach { l =>
          out() = out()._1 -> l
          println("stderr: " + l)
        }
      }
    )
    
    pb.run(pio)
*/

  /**
   * Writes arbitrary code (as a single String) inside of
   * templated main method matching Scala.js syntax
   * Calls a running SBT process with fastOptJS for development
   * NOTE: This overwrites all class files / target compiled .js
   * Hence requests against /template2/.../something.js will only
   * reflect the latest version.
   * @param s : Code block to run as single string
   */
    def writeCompile(s: String) : Unit = {
    import ammonite.ops._
    write.over(
      cwd / 'tmp / 'template2 / 'src / 'main / 'scala /
        RelPath("Inject.scala"),
      MAIN + s + MAIN_TERMINATION
    )
    //    in() = "fastOptJS\n"
    t.run() = (Random.nextInt(Int.MaxValue) // alias to maxInt
      // example of UI parse-dag link
      , SBT_COMPILE)
  }

  val SBT_COMPILE = """screen -S sbbt -p 0 -X stuff "fastOptJS"$'\012'"""
  val SBT_START = "pwd" // """screen -S sbbt -p 0 -X stuff " /usr/local/bin/sbt'"$'\012'"""
  val SBT_INIT = "pwd" // ""screen -d -m -S sbbt -L "bash -s 'cd /Users/ryle/Documents/repo/fayalite/tmp/template2;'"'"""

    val t = new TerminalEmulator()
    t.out.foreach{println}
   /* t.runNL(0, SBT_INIT)
    Thread.sleep(5000)
    t.runNL(0, SBT_START)
*/
    def main(args: Array[String]): Unit = {
      writeCompile("println(1)")
      Thread.sleep(Long.MaxValue)
    }

  }
