package org.fayalite.repl

import java.io.{File, OutputStream}
import java.net.URL

import ammonite.ops.ls
//import org.fayalite.io.RemoteTerminalEmulator
import rx.ops._
import scala.concurrent.Future
import scala.sys.process.ProcessIO
import rx._


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
   // val t = new RemoteTerminalEmulator()
 //   t.out.foreach{println}

    write.over(
    cwd / 'tmp / 'template2 / 'src / 'main / 'scala /
    RelPath("Inject.scala"),
    MAIN + s + MAIN_TERMINATION
    )
 //   t.run() = 0 -> SBT_COMPILE
    // SBT_COMPILE_LOCAL.!!

  }



  val scrnNm = "sbbt" // + Random.nextInt(500).toString
  def scrw(scr: String = scrnNm) = s"screen -S $scr -p 0 -X stuff" + " "
  val SBT_COMPILE = scrw(scrnNm) + """"fastOptJS"$'\012'"""
  val SBT_COMPILE_LOCAL = scrw(scrnNm) + "fastOptJS$'012'"

  val rs = """screen -r sbbt -p0 -X hardcopy $(tty)"""
  def readScreen() = {
    //t.run() = 0 -> rs
    //Seq("screen", "-r", "sbbt", "-p0", "-X", "hardcopy", "scrout").!!
   // Seq("tail", "-n", "500", "scrout").!!
    ""
  }

  val SBT_START = scrw(scrnNm) + """usr/local/bin/sbt'$'\012'"""
  val SBT_CD = scrw(scrnNm) + """cd /Users/ryle/Documents/repo/fayalite/tmp/template2;"$'\012'"""
  val SBT_INIT = Seq("screen", "-d", "-m", "-S", "sbbt", "-L", "bash",
    ">", "screnlg", "2>&1 &")
  // screen -d -m -S sbbt -L "bash"

  def initScreen() = {
    //SBT_CD.!!
    //SBT_START.!!
  }


  val line = Var("")

/*
  line.foreach{
    q =>
      println("line out from screenlog: " + q)
  }
*/

  val lastClientLines = Var("")
/*
  val lineHistory = line.reduce{
    (a: String,b: String) =>
      lastClientLines() = lastClientLines() + b
      val c = a + b
      c : String
  }*/

  def initTailWatchScreenLog = {
    import fa._
    Future{
      while (true) {
        val nl = readScreen()
        if (line() != nl) line() = nl
        Thread.sleep(500)
     //   println("tailing line from scrlog.")
      }
/*      Seq("tail", "-f", "./tmp/template2/screenlog.0").lines.foreach {
      l => line() = l
    }*/


    }
  }
  //initTailWatchScreenLog

  def inject(code: String) = {
    scala.tools.nsc.io.File("tmpl/src/main/scala" +
      "/Inject.scala").writeAll(
     code // MAIN + code + MAIN_TERMINATION
    )
    import scala.sys.process._

    val p = sys.process.Process(
      Seq(
        //"cd", "templ", "&",
     //   "C:/Program Files (x86)/sbt/bin/sbt.bat",
      "sbt",
        "fastOptJS"),
    new File("tmpl")
    )
    p.!!
  }


  def main(args: Array[String]): Unit = {

    // import ammonite.ops._
    inject("println(\"yo\")")
   /*
    val pb = new ProcessBuilder(
      "C:/Program Files (x86)/sbt/bin/sbt",
      "x",
      "myjar.jar",
      "*.*",
      "new");
    pb.directory(new File("H:/"));
    pb. redirectErrorStream(true);

    val p = pb.start();*/
    //  ls.!!(Path("/Users")) foreach println
    /*
    */
 //   println(SBT_INIT.!!)
//    Thread.sleep(Long.MaxValue)
 //   writeCompile("println(1)")

   // println(readScreen())
    //Thread.sleep(Long.MaxValue)
/*    println(
     readScreen()
    )*/
  }

}

/*
 > $CLUSTER_SUBMIT_STDOUT_LOG 2>&1 &"
}; export -f submit


    //    in() = "fastOptJS\n"
 /*   t.run() = (Random.nextInt(Int.MaxValue) // alias to maxInt
      // example of UI parse-dag link
      , SBT_COMPILE)*/

  val wb = Var(0)
  val wba = Var(Array[Byte]())
  class RStream extends OutputStream {
    def write(i: Int) = wb() = i
    override def write(b: Array[Byte]) = {
      System.out.write(b)
      wba() = b
      System.out.flush()
    }
  }


 */
  /*  Seq(SBT_INIT, SBT_CD, SBT_START).foreach { q =>
      t.runNL(0, q)
      Thread.sleep(5000)
    }*/
/*
  implicit def strByte(s: String) : Array[Byte] = s.toCharArray.map{_.toByte}

  class PIO(cmd: Seq[String], path: String) {
    val pb = Process( """sbt""", new File(path))
    val in = Var("fastOptJS\n")
    val out = Var("" -> "")
    var stdin_ : OutputStream = null.asInstanceOf[OutputStream]
    val pio = new ProcessIO(stdin => {
      Obs(in, skipInitial = true) {
        val q = in();
        stdin.write(q.toCharArray.map {
          _.toByte
        })
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

  }
 */
