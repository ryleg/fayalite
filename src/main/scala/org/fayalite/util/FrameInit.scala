package org.fayalite.util

import java.awt.Graphics
import java.awt.event.{KeyListener, KeyEvent}

import ammonite.repl.Storage
import org.fayalite.repl.REPLManagerLike

import scala.reflect._
import scala.tools.nsc.{io, Properties, Settings}
import scala.tools.nsc.interpreter._
import scala.tools.nsc.util.ScalaClassLoader._
import scala.tools.reflect.StdRuntimeTags._

/**
  * Direct draw frame builder
  */
class FrameInit {

  val cm = new SymbolRegistry()

  val f = new FFrame()

  class Listen extends KeyListener {
    override def keyTyped(e: KeyEvent): Unit = {}

    override def keyPressed(e: KeyEvent): Unit = {
      val img = cm.get(e.getKeyChar.toString)
      f.update((d: Graphics) => {
        img.draw(d, 50, 50)
      })
    }

    override def keyReleased(e: KeyEvent): Unit = {}
  }

  val l = new Listen()
  f.addKeyListener(l)
  f.init()
  f.start()
}

object FrameInit {

  import scala.tools.nsc.interpreter.ILoop

  def main(args: Array[String]) {
    import ammonite.repl.Repl
    val storage = Storage(ammonite.ops.home, None)
    //   val r = new Repl(System.in, System.out, System.err, Ref(storage))
    //  r.run()

    val rml = new REPLManagerLike()

    val i = new ILoop(Some(rml.iLoopBufferedReader), rml.iLoopOutputCatch) {
      def addThunk2(body: => Unit) = addThunk(body)
    }


    import i.{intp, addThunk2 => addThunk}

    savingContextLoader {

      i.createInterpreter()
      val in0 = SimpleReader(rml.iLoopBufferedReader,  rml.iLoopOutputCatch, true)
      // Bind intp somewhere out of the regular namespace where
      // we can get at it in generated code.
      addThunk(intp.quietBind(NamedParam[IMain]("$intp", intp)(tagOfIMain, classTag[IMain])))
      addThunk({
        import scala.tools.nsc.io._
        import Properties.userHome
        import scala.compat.Platform.EOL
        val autorun = replProps.replAutorunCode.option flatMap (f => io.File(f).safeSlurp())
        if (autorun.isDefined) intp.quietRun(autorun.get)
      })
      // it is broken on startup; go ahead and exit
      if (intp.reporter.hasErrors) println("has errors")
      intp.initializeSynchronous()
      postInitialization()

    }

    println(i.intp.interpret("val x = 1"))

    Thread.sleep(Long.MaxValue)

  }
}
