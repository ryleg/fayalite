package org.fayalite.repl

import java.awt.Graphics
import java.awt.event.{KeyEvent, KeyListener}

import ammonite.repl.Storage
import org.fayalite.util.{FFrame, SymbolRegistry}
import fa._

/**
  * Direct draw frame builder
  */
class REPLFrame {

  val cm = new SymbolRegistry()

  val f = new FFrame()

  val nr = new NativeREPL()

  val rs = F{ nr.interpret("val x = 1")}
  rs.onComplete(q => println("Interpreter output " + q.get))

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




object REPLFrame {

  def main(args: Array[String]) {
    val storage = Storage(ammonite.ops.home, None)
    //   val r = new Repl(System.in, System.out, System.err, Ref(storage))
    //  r.run()

    Thread.sleep(Long.MaxValue)

  }
}
