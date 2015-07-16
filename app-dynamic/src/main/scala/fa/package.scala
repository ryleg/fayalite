import org.fayalite.ui.app.canvas.Schema

import scala.util.{Failure, Success, Try}

package object fa {

  // use macros to redefine tpl{something} to tpl something
  def tpl[T](f : => T) = {
    val r = Try{f}
    r match {
      case Success(x) =>
      case Failure(e) => e.printStackTrace()
    }
    r
  }

}
