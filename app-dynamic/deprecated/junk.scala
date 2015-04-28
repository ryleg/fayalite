import org.fayalite.ui.app.canvas.elem.Text
import org.scalajs.dom.Text
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

/*


deprecated:


/*    val nodes = classF.grouped(numColumns()).toList.map {
      gro =>
        val curX = Var(bodyOffset())
        curY() += 37
     //   println("curY " + curY())
        Schema.TryPrintOpt {
          gro.zipWithIndex.map {
            case (vtx, idx) =>
              val childs = emap3.get(vtx.id).map {
                c =>
                  c.map {
                    _._2.dstId
                  }.map {
                    i2v.get
                  }.head
              }.flatten.map {
                cv =>
                  new Text(Var(cv.vd), x = Var(100), y = Var(400)) // x y not used
              }
              val tex = new Text(Var(vtx.vd), x = Var(curX()), y = Var(curY()))
              val n = new Node(Var(tex), editable = Var(childs))
              n.text().redraw()
              curX() += n.text().position().dx.toInt + 30
              n
          }
        }
    }

    val flatNodes = nodes.flatten.flatten.toSet

    flatNodes.foreach{n => println(n.text().text())}*/
/*
    flatNodes.foreach{
      n =>
        n.editable().map{e => Obs(e.onRedraw, skipInitial=true){
          flatNodes.filter{_ != n}.foreach{
            ne =>
              ne.editable().map{_.position().clear()}
          }
        }}
    }*/

/*  Obs(Canvas.onKeyDown, skipInitial = true) {
    if (Canvas.onKeyDown().keyCode == 86  && // v
      Canvas.ctrlKey() )
    //scala.util.Try{println( "Ctrl key " + Canvas.onKeyDown().ctrlKey)}
   // println(Canvas.onKeyDown().hasOwnProperty("key"))
  //  scala.util.Try{println("  other keys " + Canvas.onKeyDown().keyCode)}
   // scala.util.Try{println("  other keys " + Canvas.onKeyDown().charCode)}
  }*/
 // val test = Text("test", 200, 200)
 // test.redraw()
 // val nodet = new Node(Var(test), Var(Some(Text("testline\ntestlinet\n\nasdf", 200, 300))))

  println("new editor")
  import Editor._

  val graph = Var(GraphData(Array(), Array()))

  val layout = Rx {
    val g = graph()
    val emap = g.edges.map{ e => e.id -> e}.toMap
  //  val emap2 = g.edges.map{ e => e.dstId -> e}.toMap
    val emap3 = g.edges.map{ e => e.id -> e}.groupBy{_._1}.toMap

    val i2v = g.vertices.map{v => v.id -> v}.toMap
    val vesl = g.vertices.map{v => emap.get(v.id).map{_ -> v}}.flatten.toMap

  //  val ves = g.vertices.map{v => (v, emap.get(v.id))}



    val curY = Var(bodyOffsetY())

    val classF = g.vertices.filter{q => q.vd.charAt(0).isUpper && q.vd.length < 100}


  }

  PersistentWebSocket.sendKV("tab", "Editor",
    (pr: Dynamic) => {
      Schema.TryPrintOpt{
        graph() = Graph.fromJSON(pr.graph)
      }
    }
  )

 */
package org.fayalite.ui.app.canvas.elem

import org.fayalite.ui.app.canvas.Canvas
import org.fayalite.ui.app.canvas.Schema._
import org.fayalite.ui.app.canvas.elem.Text._
import org.scalajs.dom.raw.MouseEvent

import rx._
import rx.ops._
import scala.concurrent.duration._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.Try

class Cursor(
              val position: Var[Position],
              val underlyingRedraws: Var[Act] = Var({Act0}),
              val curSize: Var[Double] = Var(Text.defaultSpacing),
              val textBox: Var[Text] = Var(null.asInstanceOf[Text])
              ) {

  import Cursor._
  implicit val doms = new DomScheduler()

  def move(xy: Boolean, steps: Int) = {

    println("move called " + xy + steps)
    Try {
      //textBox().splitText
      clear()
      val dN = steps * Text.defaultSpacing
      val dxM = if (xy) dN else 0D
      val dyM = if (!xy) dN.toInt else 0
      val prP = position()

      position() = position().copy(x = prP.x + dxM, y = prP.y + dyM)
      draw()
      active() = true
      show() = true
    }
  }

  Obs(Canvas.onKeyDown, skipInitial = true) {
    TryPrintOpt {
      val kp = Canvas.onKeyDown()
      println(kp.keyCode)
      val cc = Canvas.onKeyDown().keyCode
      cc match {
        case 37 => move(true, -1)
        case 39 => move(true, 1)
        case _ =>
      }
    }
  }

  val blinkRate = Var(400) // in millis

  val t = Timer(400 millis)

  def clear() = {
    val p = position()
    p.clear()
    //    println("underlying redraws")
    underlyingRedraws()()
  }

  val active = Var(false)

  val show = Var(false)


  val draw = Rx {() => {
    val p = position()
    clear()
    val xj = p.x - 1
    val yj = p.y + 5
    Canvas.ctx.fillStyle = "red"
    Canvas.ctx.globalAlpha = .8
    Canvas.ctx.fillRect(xj, yj, p.dx, p.dy)
  }}

  val o = Obs(t) {
    if (show()) {
      val a = active()
      active() = !a
      if (a) draw()() else
      {
        clear()
      }
    }
  }


}


object Cursor {

  val cursor = new Cursor(
    Var(Position(200, 200, 2, 20))
  )

  def apply(x: Double, y: Int) : Unit = {
    cursor.clear()
    cursor.position() = cursor.position().copy(
      x = x, y = y)
  }

  def apply(t: Text,
            me: MouseEvent): Unit = {
    cursor.textBox() = t
    textToCursorCoordinates(t, me) /*match {
      case coords @ Coordinates(x,y,xj,yj) =>
        println(coords)
        Cursor.apply(xj, yj)
        cursor.underlyingRedraws() = () => t.redraw()
        cursor.draw()()
        cursor.show() = true
    }*/
    /*  */
  }

  case class Coordinates(
                          xStep: Int,
                          yStep: Int,
                          xActual: Double,
                          yActual: Int
                          )

  def textToCursorCoordinates(t: Text,
                              me: MouseEvent) = {
    val cx = me.clientX
    val dx = t.style{"a".measure.width}
    val ils = interLineSpacing()
    val y0 = t.y()
    val yIdx = Math.abs((me.clientY-y0)/ils).toInt
    val xo = me.clientX - t.x()
    val xspace = t.hardMonospaceDeltaX()
    val row = t.splitText()(yIdx)
    val yj = t.y() + (yIdx-1)*ils
    val half = xspace.toDouble/2

    val xIdx = (xo / xspace).toInt
    val xOffset = xIdx*xspace
    val char = row(xIdx)
    println("xIdx " + xIdx + " char " + char)

    Canvas.ctx.fillStyle = "green"
    Canvas.ctx.globalAlpha = .8
    Canvas.ctx.fillRect(t.x() +xOffset, yj + 10, 1, 40)
    val curI = (0 until row.length).map{i =>
      val xact = i*xspace
      val xMiddleChar = xact + half
      val delt = xMiddleChar - xo
      val isLeft = delt < 0
      val xCursor = if (isLeft) xact else (i+1)*xspace
      /*



      */

      (i, xCursor.toDouble, Math.abs(delt))
    }


    val (xj, xidx) = if (row.nonEmpty) {
      val (charxj, cursxj, delt) = curI.minBy{_._3}
      val midChar = row(charxj)
      // println("midchar " + midChar)
      (cursxj, charxj)
    } else {
      (t.x().toDouble, -1)
    }

    Coordinates(xidx, yIdx, xj+t.x(), yj)
    /*
        val charMid = cIdx.map{case CharTextIdx(tx, chridx, wid) =>
          val lw = t.style{tx.last.toString.measure.width}
          (chridx, wid + lw/2)
        }
          //(0 until row.length).map{q => (q, q*dx + dx/2 + 7.81)}.toList

    /*

      //println(charMid)
        t.canvasIdx(row).foreach { case CharTextIdx(tx, chridx, wid) =>
          Canvas.ctx.fillStyle = "red"
          Canvas.ctx.globalAlpha = .8
          val lw = t.style{tx.last.toString.measure.width}

          Canvas.ctx.fillRect(t.x() + wid, yj + 10, 1, 40)

          Canvas.ctx.fillStyle = "green"
          Canvas.ctx.globalAlpha = .8
          Canvas.ctx.fillRect(t.x() + wid - lw/2, yj + 10, 1, 40)

        }

    */

        val (midIdxO, midOffsetO) = charMid.minBy{case (idx, cm) => Math.abs(xo - cm)}

        val midIdx = midIdxO - 1

        val idxP = if (row.isEmpty) None else Some({
          if (midIdx > 0) {
         if (midIdx < row.length) midIdx else row.length-1
        }  else {
          0
        }})

        val midOffset = idxP.map{i => charMid(i)._2}
        val isLeftO = midOffset.map{i => (i, (xo - i) < 0)}
        val xjo = isLeftO.map{ case (midO, isLeft) => if (isLeft) midO - dx/2 else midO + dx/2 }
        val xj = t.x() + xjo.getOrElse(0D)

        val midChar = idxP.map{i => charMid(i)}
        //val midChar = row(idxP)
        val isLeftOffsetIdx = isLeftO.exists{_._2}

        val xActualIdx = idxP.map{i => if (isLeftOffsetIdx) i - 1 else i}.getOrElse(0)


     /*   println(s"is left $isLeftO xj $xj xo $xo midchar $midChar clientX ${me.clientX} " +
          s"tx ${t.x()}" )*/
        /*
            Canvas.ctx.fillStyle = "yellow"
            Canvas.ctx.globalAlpha = .8
            Canvas.ctx.fillRect(xj, yj + 10, 4, 50)
        */

        (xj, yj, midChar, isLeftO, Coordinates(if (xActualIdx > 0) xActualIdx else 0, yIdx))*/
  }

}

/*

    val xj = t.x() + xoP

     val xIdx = (xo/dx).toInt - 1
    val xoP = xIdx*dx //- dx/2
*/

/*
    val cp = t.splitText().map{st => t.canvasIdx(st)}
    val lineElem = cp(yIdx)
    val ctidxi = cp(yIdx).collectFirst{
      case cti : CharTextIdx if cti.width + t.x() > me.clientX
      => cti
    }
    val dxi = ctidxi.map{_.width}.getOrElse(t.widths().max)
    val xIdx = ctidxi.map{_.charIdx}.getOrElse(0)
    val prevChar = if (xIdx == 0) lineElem(0) else lineElem(xIdx-1)
    val middleOffset = (prevChar.width - dxi) / 2
    val onew = lineElem(0).width/2
    lineElem.foreach{
      le =>
        Canvas.ctx.fillStyle = "red"
        Canvas.ctx.globalAlpha = .8
        Canvas.ctx.fillRect(t.x() + le.width - onew, yj+10, 1, 40)
    }*/

/*    val xj = t.x() + dxi
    println(s"xj yj, $xj, $yj $ctidxi $xIdx $prevChar")*/