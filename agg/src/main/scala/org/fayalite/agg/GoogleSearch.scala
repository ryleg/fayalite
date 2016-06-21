package org.fayalite.agg

import java.awt.{Robot, Toolkit}
import java.awt.datatransfer.StringSelection
import java.awt.event.KeyEvent

import fa._

import scala.collection.JavaConversions

/**
  * Unfinished, just testing out stuff
  */
class GoogleSearch(query : String = "asdf") {

  import GoogleSearch._

  val crawler = new SeleniumChrome(Some("https://google.com?gws_rd=ssl#q=" + query))

  crawler.started.foreach{
    _ =>
      println("Started")
      Thread.sleep(5000)
      import JavaConversions._
      val els = crawler.webDriver.findElementsByCssSelector("a").iterator().toSeq
      els
        .dropWhile(q => q.getText != "Search tools")
        .tail.filter {z =>
        val t = z.getText
          t.nonEmpty && !t.startsWith("Images for")}
        .zipWithIndex.foreach{
        case (q, i) =>
          println(q.getText, i)
      }
      crawler.webDriver.findElementByCssSelector(
        "#rso > div:nth-child(1) > div > h3 > a").getAttribute("data-href").p

      // NOTE: Google defeats this solution!
      val d = crawler.getPageSource.doc
      println(d.select("#rso > div:nth-child(1) > div > h3 > a").html())
      val datar = d.getElementsByAttribute("data-href").toScala
      //println(datar)
  }

}

object GoogleSearch {

  def pasteContents(contents: String) = {
    val user = new StringSelection(contents)
    val rb = new Robot()
    Toolkit.getDefaultToolkit().getSystemClipboard().setContents(user, null)
    //  println("set clip")
    rb.keyPress(KeyEvent.VK_CONTROL)
    rb.keyPress(KeyEvent.VK_V)
    rb.keyRelease(KeyEvent.VK_V)
    rb.keyRelease(KeyEvent.VK_CONTROL)
    //tab to password entry field
    rb.keyPress(KeyEvent.VK_TAB)
    rb.keyRelease(KeyEvent.VK_TAB)
    Thread.sleep(300)
    //press enter
    rb.keyPress(KeyEvent.VK_ENTER)
    rb.keyRelease(KeyEvent.VK_ENTER)
  }


  def main(args: Array[String]) {
    new GoogleSearch(args(0))
  }
}
