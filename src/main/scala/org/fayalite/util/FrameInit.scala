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
        // img.draw(d, 50, 50)
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

    Thread.sleep(Long.MaxValue)

  }
}
