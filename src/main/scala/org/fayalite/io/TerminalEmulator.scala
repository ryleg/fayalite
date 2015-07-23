package org.fayalite.io

import java.io.{FileOutputStream, OutputStream}
import java.nio.charset.Charset

import com.decodified.scalassh.{SshClient, SSH}
import com.googlecode.lanterna.TerminalFacade
import com.googlecode.lanterna.terminal.text.UnixTerminal

import rx._
import ops._

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Random

// TODO : Oh god what's going on in here.

class TerminalEmulator(host: String = "localhost") {
  //  return new UnixTerminal(terminalInput, terminalOutput, terminalCharset)
  val run = Var(0 -> "ls -a")

  def runNL(sui: (Int, String)) = run() = sui._1 -> {sui._2 + "\n"}
  val out = Var(0 -> ("", ""))

  val client = SshClient(host)

  run.foreach { case (ridx, r) =>
    val body = (c: SshClient) => c.exec(r)
    client.right.foreach {
      client =>
        val result = {
          try {
            body(client)
          }
          catch {
            case e: Exception ⇒ Left(e.toString)
          }
        }
        result.right.foreach {
          rr =>
            out() = ridx -> (rr.stdOutAsString() -> rr.stdErrAsString())
        }
    }
      client.left.foreach{
        q =>
          println(q)
      }
  }
 // out.foreach{println}
}

  object TerminalEmulator {

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

    val msgs = wba.map{
      w => w.map{_.toChar}.mkString
    }

    def main(args: Array[String]) {
/*
tmux send-keys -t <session:win.pane> '<command>' Enter
tmux capture-pane -t <session:win.pane>
tmux show-buffer
 */
      // msgs.foreach{println}
      //  return new UnixTerminal(terminalInput, terminalOutput, terminalCharset)
      val t = new TerminalEmulator()
      t.out.foreach{println}
      t.run() = 1 -> {
        "ssh -i keypair " +
          "server" +
          " '" +
          "tmux capture-pane -t 6 -S -1500; tmux show-buffer;" +
          "'"
      }
     // t.run() = 2 -> "tmux list-sessions"
      /*
          val ss = SSH("localhost") { client =>
            client.exec("ls -a").right.map { result =>
              println("Result:\n" + result.stdOutAsString())
            }
          }
          println(ss)
      */

      /*
         val textGUI = TerminalFacade.createGUIScreen();
          if(textGUI == null) {
            System.err.println("Couldn't allocate a terminal!");
            return;
          }
          textGUI.getScreen().startScreen();
          textGUI.setTitle("GUI Test");
      */

      //TerminalFacade.createSwingTerminal()
      /* val ros = new RStream()
       val terminal = TerminalFacade.createTerminal(System.in,
       System.out,
         //ros,
         Charset.forName("UTF8"));
       */
      //terminal.moveCursor(10, 5);
      /*
          "ls\n".map{q => terminal.putCharacter(q); println(q)}
          terminal.flush()
      */
      import ExecutionContext.Implicits.global
      //Future{
      // Thread.sleep(Long.MaxValue)
      //}
    }
  }
