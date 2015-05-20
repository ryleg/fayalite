
package org.fayalite.ui.app.manager

import org.fayalite.ui.app.canvas.elem._

object Editor {

  val editor = new Editor()

}



class Editor() {

  val grid = Grid()

  val symbolManager = new SymbolManager(grid)

  implicit val grid_ = grid

  val tree = new FileTree(this)

}

