package org.fayalite.ui.app.canvas


object Canvas {

  val rect = Var(getRect)

  // change to lift
  val onKeyDown = Var(null.asInstanceOf[KeyboardEvent])
  val onKeyUp = Var(null.asInstanceOf[KeyboardEvent])
  val onclick = Var(null.asInstanceOf[MouseEvent])
  val onresize = Var(null.asInstanceOf[UIEvent])
  val rightClick = Var(null.asInstanceOf[MouseEvent])


  window.onkeydown = (ke: KeyboardEvent) => {onKeyDown() = ke}
  window.onkeyup = (ke: KeyboardEvent) => onKeyUp() = ke

  window.onclick = (me: MouseEvent) => {
    onclick() = me
  }

  val area = Rx { LatCoordD(widthR(), heightR())}

  Obs(canvasR, skipInitial = true) {
    if (canvasR() != null) {
      heightR() = canvasR().height.toDouble
      widthR() = canvasR().width.toDouble
    }
  }


}
