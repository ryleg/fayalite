import org.scalajs.dom._

/*

  window.oninput = (e: Event) => {
    println("oninput" + e)
  }
  window.addEventListener("paste",
    (e: Event) => {
      println(e.valueOf())
      println(e.hasOwnProperty("clipboardData"))
    //  e.cast[ClipboardEvent]
  //    val dt = e.cast[dom.DataTransfer]
  //    println("dt types " + dt.types)
  //    println(dt.types.length)
  //    println(Array.tabulate(dt.types.length){i => dt.types.apply(i)})
//      println("dt " + dt.getData("pasteundefined"))
      println("paste" + e.cast[dom.DataTransfer].types )
      println("len " +  e.cast[dom.DataTransfer].getData("text/plain"))

  })
  //var elementListeners : Map
*/

def testKeyBind() = {
  val attempt = Try {
    window.onkeypress = (ke: KeyboardEvent) => {
      val k = ke.keyCode.toChar.toString
      println("kp " + k)
      draw(k)
      //     cursor += cursorDx
      //     curText += k
    }
  }
  attempt match { case Failure(e) => e.printStackTrace(); case _ =>}
}



/*
  Obs(onclick) {
    println("obsonclickpure")
  }*/

def resetCanvasTriggers() = {

  window.oncontextmenu = (me: MouseEvent) => {
    me.preventDefault()
    rightClick() = me
  }

  window.onclick = (me: MouseEvent) =>
  {
    onclick() = me
    //  println("onclick")
    val sxi = me.screenX
    val syi = me.screenY
    val cxi = me.clientX
    val cyi = me.clientY
    /*    println(s"Window onclick " + //screenX $sxi "screenY $syi  " +
          s"clientX $cxi clientY $cyi " +
          s"numTriggers: ${elementTriggers.size}")*/
    elementTriggers.foreach{
      case (elem, trigger) =>
        val isInside =
          (cxi > elem.position.x - xButtonBuffer) &&
            (cxi < elem.position.x2 + xButtonBuffer) &&
            (cyi > elem.position.y - yButtonBuffer) &&
            (cyi < elem.position.y2 + yButtonBuffer)
        /*          println(s"isInside: $isInside $elem x2,y2" +
                    s" ${elem.position.x2},${elem.position.y2}")*/
        if (isInside) {
          activeElem = Some(elem)
          trigger()
        }
    }
  }
}
