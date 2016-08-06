package org.fayalite.sjs.input

import org.fayalite.sjs.Schema.{CanvasContextInfo, LatCoord}
import org.fayalite.sjs.canvas.CanvasBootstrap._
import org.fayalite.sjs.comm.XHR
import org.scalajs.dom._
import org.scalajs.dom.ext.KeyCode
import rx.ops.{DomScheduler, Timer}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.collection.mutable
import scala.util.Try


trait TileCoordinator {

  val absoluteLatResolve = mutable.HashMap[LatCoord, CanvasContextInfo]()

  /**
    *
    */
  val indexLatResolve = mutable.HashMap[LatCoord, CanvasContextInfo]()

  /**
    * It's easier to just re-use tiles than modify the DOM
    */
  val spareTiles = mutable.Queue[CanvasContextInfo]()

  /**
    * Last click element, a single canvas that moves around
    * representing location of last clicked element
    */
  val mLast =
    createCanvasZeroSquare(minSize, commentGreen, 0.1D)

  val bLast =
    createCanvasZeroSquare(bulkSize, annotationYellow, 0.03D)

  val bHover =
    createCanvasZeroSquare(bulkSize, keywordOrange, 0.03D)

  val mHover =
    createCanvasZeroSquare(minSize, methodGold, .1D)

  val wordHover =
    createCanvasZeroSquare(minSize, commentGreen, .5D)

  val wordLast =
    createCanvasZeroSquare(minSize, lightBlue, .01D)

  def handleBackspace(ke: KeyboardEvent) = {
    ke.preventDefault()
    val k = mLast.absoluteCoords.fromAbsolute.left.toAbsolute
    absoluteLatResolve.get(k).foreach{
      q =>
       // println("found tilemap ")
        absoluteLatResolve.remove(k)
        indexLatResolve.remove(mLast.latCoords)
        q.turnOff()
        spareTiles += q
    }
    mLast.shiftLeftCarriage()
  }

  def mkMinTile(c: String) = {
    val t = createCanvasZeroSquare(
      minSize, alpha=0D, zIndex=10
    ).copy(text = Some(c))
    t.moveTo(mLast)
  //  println("Made tile ", t.absoluteCoords)
    absoluteLatResolve(t.absoluteCoords) = t
    indexLatResolve(t.latCoords) = t
    Try{t.drawText(c)}
    //reactIsValModifier(c, t)
    t
  }

  def mkCharLikeSquareNode(charLike: String) = {
    val t = createCanvasZeroSquare(
      minSize, alpha=0D, zIndex=10
    ).copy(text = Some(charLike))
    Try{t.drawText(charLike)}
    t
  }

  def updateCoords(t: CanvasContextInfo) = {
    absoluteLatResolve(t.absoluteCoords) = t
    indexLatResolve(t.latCoords) = t
  }

  val wordResolve = mutable.HashMap[LatCoord, Array[CanvasContextInfo]]()
  val wordResolveArea = mutable.HashMap[LatCoord, (LatCoord, Int)]()
  val wordResolveStr = mutable.HashMap[LatCoord, String]()


  def mkWord(word: String, origin: LatCoord) = {
    val tempCnvRenders = word.map{_.toString}.map{mkCharLikeSquareNode}
    tempCnvRenders.head.move(origin)
    updateCoords(tempCnvRenders.head)
    // Proper way to do this should look like this
    /*    tempCnvRenders.tail.foldRight(tempCnvRenders.head) {
          case (nextElement, foldPrevEl) =>
        }*/
    // Heres the wrong way to do it:
    tempCnvRenders.zipWithIndex.tail.foreach{
      case (c, i) =>
        c.moveTo(tempCnvRenders.head)
        (0 until i).foreach { _ =>
          c.shiftRight()
        }
        updateCoords(c)
    }
    val len = word.length


    wordResolve(origin) = tempCnvRenders.toArray
    (0 until len).foreach { i =>
      wordResolveStr(origin.right(i)) = word
      wordResolveArea(origin.right(i)) = origin -> len
    }
  }

}

/**
  * Setup listeners for inputs from client
  * in terms of mouse / key actions
  */
object InputBootstrap extends InputHelp
with TileCoordinator {

  case class FileResponse(files: Array[String])

  case class FileRequest(file: String)

  // For any future / thread related stuff use this
  implicit val scheduler = new DomScheduler()

  //Unused temporarily
  def processTileMatrix(tm: Array[Array[CanvasContextInfo]]) = {}

  // Primary flash rate for the cursor
  val heartBeat = Timer(1400.millis)

  var codeSample = ""

  def init() : Unit = {
    //disableRightClick() // TODO : Enable when scrolling is implemented
    println("Input bootstrap")
    //mkMinTile("AD")

/*
    document.onkeydown = (ke: KeyboardEvent) => {
      val numShifts = if (ke.ctrlKey) {
        if (ke.altKey) 3 else 2
      } else 1

      ke.keyCode match {
        case KeyCode.a if ke.ctrlKey =>
          println("Cell capture attempt ")
        case KeyCode.backspace =>
          handleBackspace(ke)
        case KeyCode.left =>
          (0 until numShifts).foreach{_ => mLast.shiftLeft()}
        case KeyCode.right =>
          (0 until numShifts).foreach{_ => mLast.shiftRight()}
        case KeyCode.up =>
          (0 until numShifts).foreach{_ => mLast.shiftUp()}
        case KeyCode.down =>
          (0 until numShifts).foreach{_ => mLast.shiftDown()}
        case KeyCode.tab =>
          ke.preventDefault()
          mLast.shiftHorizontal(4)
        case _ =>
      }
    }
/**/
    document.onkeypress = (ke: KeyboardEvent) => {
      val chr = ke.keyString
      codeSample += chr
     // println("Key down " + chr)
      ke.keyCode match {
        case KeyCode.enter =>
          mLast.shiftDownLeftZero(minSize)
          println("Code sample " + codeSample)


        case KeyCode.backspace =>
          handleBackspace(ke)
        case kc =>
      }
      mkMinTile(chr)
      mLast.shiftRight()
    }

    heartBeat.foreach{
      _ =>
        mLast.onOff()
    }
/**/

    var lastSelectedWord : LatCoord = LatCoord(0,0)

    window.onmousedown = (me: MouseEvent) => {
      val minXY = me.tileCoordinates(minSize)
      val bulXY = me.tileCoordinates(bulkSize)
      mLast.move(minXY)
      bLast.move(bulXY)
      val maybeTuple: Option[(LatCoord, Int)] = wordResolveArea.get(minXY)
      if (maybeTuple.isEmpty) {
        wordLast.clear()
      }
      maybeTuple.foreach {
        case (o, l) =>
          val w = wordResolveStr(minXY)
          println("Clicked on w " + w)
          wordLast.moveTo(wordHover)
          lastSelectedWord = wordLast.location
          val selectAsOfPost = lastSelectedWord
          val parentAsOfPost = w
          XHR.post[FileRequest,
          FileResponse](FileRequest(w), {
            fileResponse =>
              fileResponse.files.zipWithIndex.foreach{
                case (f, fidx) =>
                  val lc = lastSelectedWord.down(fidx+1).right
                  mkWord(f, lc)
              }
          }, "files")
          wordLast.canvas.width = wordHover.canvas.width
          wordLast.clear()
          wordLast.fillAll(lightBlue, 0.25D)
      }
    }
*/

    window.onmousemove = (me: MouseEvent) => {
      //println("ON Mouse move")
      val coordH: LatCoord = me.tileCoordinates(minSize)
      val getOptH = wordResolveArea.get(coordH)
      getOptH.foreach{
        case (origin, len) =>
        //  println("Moving")
          wordHover.move(origin)
          wordHover.canvas.width = len*minSize
          wordHover.clear()
          wordHover.fillAll(commentGreen, 0.2D)
      }
      if (getOptH.isEmpty) {
        wordHover.clear()
      }

      mHover.move(coordH)
        bHover.move(me.tileCoordinates(bulkSize))


      }
    }
}
