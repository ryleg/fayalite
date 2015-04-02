package org.fayalite.ui.app.canvas

import org.fayalite.ui.app.PersistentWebSocket
import org.fayalite.ui.app.canvas.Canvas._

import scala.scalajs.js
import scala.scalajs.js.Array
import scala.util.{Failure, Success, Try}

object Schema {

  case class Vtx(id: Long, vd: String)

  case class Edg(id: Long, dstId: Long, ed: String)

  case class GraphData(vertices: js.Array[Vtx],
                       edges: js.Array[Edg]) {



  }

  case class ParseResponse(
                            flag: String,
                            email: String,
                            requestId: String,
                            graph: GraphData)

  case class AWSCredentials(
                             access: Option[String] = None,
                             secret: Option[String] = None,
                             pem: Option[String] = None
                             )

  case class UserCredentials(
                              email: String,
                              aws: AWSCredentials,
                              updateTime: Long = System.currentTimeMillis()
                              )

  type Act = () => Unit

  def TryPrintOpt[T](f : => T) = {
    Try{f} match {
      case Success(x) => //println("success");
       Some(x);
      case Failure(e) => e.printStackTrace(); None
    }
  }

  case class Position(
                       x: Int,
                       y: Int,
                       dx: Double = null.asInstanceOf[Double],
                       dy: Int = null.asInstanceOf[Int]
                       ) {
    val x2 = x + dx
    val y2 = y + dy
    def clear() = {
/*      println("clear call on canvas " +
        s"$x $y $dx $dy" )*/
      TryPrintOpt(Canvas.ctx.clearRect(//rekt
        x,
        y,
        dx,
        dy)
      )
    }
  }

  val Act0: Act = () => ()

  case class Elem(
                  name: String,
                  position: Position,
                  trigger: Act,
                  draw: Act = Act0,
                  flag: Enumeration#Value,
                  tabSync: Boolean = true,
                  key: String = "tab",
                  setActiveOnTrigger: Boolean = false
                   ) {

    def redrawText(s: String) = {
      println("redraw")
      deregister()
      val elemP = ElementFactory(
        this.copy(name=s, position=position.copy(y=position.y+17) // lol
        ))()
      elemP
    }

/*    def setText(text: String) = {
      deregister()
      val ret = this.copy(name=text)
      ret.register()
      ret
    }*/

    def register() = {
      draw()
      elementTriggers = elementTriggers ++ Map(this -> {
        () => {
          if (tabSync) PersistentWebSocket.sendKV(key, name)
          trigger()
        }
    } )
      resetCanvasTriggers()
    }
    def deregister() = {
      position.clear()
      elementTriggers = elementTriggers.-(this)
      resetCanvasTriggers()
    }


  }

}
