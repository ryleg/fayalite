package org.fayalite.sjs.input

import javafx.scene.input.MouseButton

import org.fayalite.sjs.Schema.{CanvasContextInfo, LatCoord}
import org.fayalite.sjs.canvas.CanvasBootstrap._
import org.fayalite.sjs.comm.XHR
import org.scalajs.dom._
import org.scalajs.dom.ext.{Image, KeyCode}
import rx.ops.{DomScheduler, Timer}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.collection.mutable
import scala.util.Try

/**
  * A lot of this is low-performance and garbage. But it's convenient to setup
  * this way because the ideal structure for char manipulation / large char graphs
  * is one where you have direct access to byte pixel data (for server side synchronization)
  * and for reducing rendering time. Each canvas should ideally be able to 'drop' pixel data
  * down from those that it overlays, so that we can consider all user operations as transformations
  * on a single uber-canvas. (For rendering performance, we want around a few hundred different
  * canvas elements, maybe even a few thousands, each of which handles portions / areas of screen.
  * This gives us translations / rotations in pixeldata space at a lower computational cost.
  *
  * Ideally, canvas char render representations should be pre-cached, mixed between DOM nodes
  * and capable of swapping between canvas areas by direct pixel representations.
  */
trait TileCoordinator extends InputHelp {

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
    createCanvasZeroSquare(minSize, commentGreen, .2D)

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
  val wordIdResolve = mutable.HashMap[Int, Word]()
  val wordIdResolveLocation = mutable.HashMap[LatCoord, Word]()
  val wordResolveArea = mutable.HashMap[LatCoord, (LatCoord, Int)]()
  val wordResolveStr = mutable.HashMap[LatCoord, String]()
  val wordResolveType = mutable.HashMap[LatCoord, String]()
  var wordHoverTooltipActive = false
  var wordHoverTooltipProccing = false

  def mkTransientTooltip(location: LatCoord, toolTip: String) = {
    val word = mkWord("[" + toolTip + "]", location.up)
    val tooltipBG = createCanvasZeroSquare(minSize, darkGrey, 1D, zIndex = 4)
    tooltipBG.move(word.origin)
    tooltipBG.canvas.width = word.word.length*minSize
    tooltipBG.clear()
    tooltipBG.fillAll(darkerBlackBlue, 1D)

    wordHoverTooltipActive = true
    setTimeout(() => {
   //   println("attempting delete")
      word.delete()
      tooltipBG.delete()
      wordHoverTooltipActive = false
    }, 2500D)
  }

  var lastWordId = 0

  case class Word(id: Int, word: String, origin: LatCoord, renders: Seq[CanvasContextInfo]) {

    def delete(): Unit = {
      wordResolve.remove(origin)
      wordResolveType.remove(origin)
      val len = word.length
      (0 until len).foreach { i =>
        wordResolveStr.remove(origin.right(i))
        wordResolveArea.remove(origin.right(i))
      }
      renders.foreach{
        r =>
          document.body.removeChild(r.canvas)
      }
    }
    val children = mutable.HashSet[Word]()
  }

  val defaultZIndex = 3

  val RIGHT_MOUSE_CODE = 2
  val LEFT_MOUSE_CODE = 0

  //val testImage = Image.createBase64Svg("TestBase64SVG")
  //testImage.style.zIndex = "20"
  //appendBody(testImage)

  var activeMovingContext : Option[CanvasContextInfo] = None

  def mkWord(word: String, origin: LatCoord, zIndex: Int = defaultZIndex) = {
    val id = lastWordId
    lastWordId += 1
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
    val wordc = Word(id, word, origin, tempCnvRenders)
    wordIdResolveLocation(origin) = wordc
    wordc
  }

}

/**
  * Setup listeners for inputs from client
  * in terms of mouse / key actions
  */
object InputBootstrap extends TileCoordinator {

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
    disableRightClick() // TODO : Enable when scrolling is implemented
   // println("Input bootstrap")
    //mkMinTile("AD")


    document.onkeydown = (ke: KeyboardEvent) => {
      val numShifts = if (ke.ctrlKey) {
        if (ke.altKey) 3 else 2
      } else 1

      ke.keyCode match {
        case KeyCode.a if ke.ctrlKey =>
        //  println("Cell capture attempt ")
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

    document.onkeypress = (ke: KeyboardEvent) => {
      val chr = ke.keyString
      codeSample += chr
     // println("Key down " + chr)
      ke.keyCode match {
        case KeyCode.enter =>
          mLast.shiftDownLeftZero(minSize)
        //  println("Code sample " + codeSample)
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


    var lastSelectedWord : LatCoord = LatCoord(0,0)

    window.onmousedown = (me: MouseEvent) => if (me.button == LEFT_MOUSE_CODE) {
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
          val charContext = absoluteLatResolve(minXY)
        //  println("Clicked on w " + w)
          wordLast.moveTo(wordHover)
          lastSelectedWord = wordLast.location
          val selectAsOfPost = lastSelectedWord
          val parentAsOfPost = w
          XHR.post[FileRequest,
          FileResponse](FileRequest(w), {
            fileResponse =>
              fileResponse.files.zipWithIndex.foreach{
                case (f, fidx) =>
                  val lc = selectAsOfPost.down(fidx+1).right
                  val cw = mkWord(f, lc)
                  selectAsOfPost
              }
          }, "files")
          wordLast.canvas.width = wordHover.canvas.width
          wordLast.clear()
          wordLast.fillAll(lightBlue, 0.12D)
      }
    }

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
          wordHover.fillAll(commentGreen, 0.12D)

          if (!wordHoverTooltipActive && !wordHoverTooltipProccing) {
            setTimeout(() => {
              if (wordHover.location == origin) {
                wordResolveType.get(origin).foreach { typ =>
                  mkTransientTooltip(origin, typ)
                }
              }
              wordHoverTooltipProccing = false
            }, 1200D)
            wordHoverTooltipProccing = true
          }
      }
      if (getOptH.isEmpty) {
        wordHover.clear()
        wordHover.move(LatCoord(-1, -1))
      }

      mHover.move(coordH)
        bHover.move(me.tileCoordinates(bulkSize))


      }
    }
}
