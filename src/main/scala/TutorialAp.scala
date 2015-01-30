/**
 * Created by ryle on 1/29/15.
 */
package tutorial.webapp
import org.scalajs.dom
import dom.document
import scala.scalajs.js.JSApp
import scala.scalajs.js.annotation.JSExport
import org.scalajs.jquery.jQuery

object TutorialApp extends JSApp {
  @JSExport
  def addClickedMessage(): Unit = {
//    appendPar(document.body, "You clicked the button!")
    jQuery("body").append("<p>[message]</p>")

  }

  def appendPar(targetNode: dom.Node, text: String): Unit = {
    val parNode = document.createElement("p")
    val textNode = document.createTextNode(text)
    parNode.appendChild(textNode)
    targetNode.appendChild(parNode)
  }

  def main(): Unit = {
    jQuery("body").append("<p>[message]</p>")

   // appendPar(document.body, "Hello World")
  }
}
