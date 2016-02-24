package org.fayalite.agg

import ammonite.ops.Path
import ammonite.ops.write.append
import org.fayalite.agg.ChromeRunner.Extr
import org.scalatest.FlatSpec
import org.scalatest.selenium.Chrome
import org.scalatest.selenium.WebBrowser.go
import fa._


class SimpleChrome(
                    override val dir: Path,
                    jobPages: List[String],
                    val srcC: String
                    ) extends ChromeRunner(srcC, 2) {
  override def getPages = jobPages.toIterator
}


object ChromeRunner {
  case class Extr(page: String, extr: Option[String])
}

class ChromeRunner(
                  outputFile: String,
                         sourceContains: String = "job-title",
                         forcedSleep: Int = 5
                         ) extends FlatSpec with Chrome {

  import scalaz.Scalaz._

  /**
   * Does this page contain extractable info as loaded
 *
   * @return : Option[PageHTMLSource]
   */
  private def extractCondition: Option[String] = pageSource.contains(
    sourceContains)
    .option {pageSource}

  private def wd = dir / currentTime

  private val pages = 1 -> 202

  def pageIter = Iterator.range(pages._1, pages._2)

  def getPages: Iterator[String] = Iterator()

  def onePageRun(pg: String) = {
    go to pg
    //implicitlyWait(Span(15, Seconds)) // Weaker guarantee of waiting
    Thread.sleep(forcedSleep*1000) // Stronger guarantee of waiting, safer
    val line = Extr(pg, extractCondition).json
    line
  }

  def runBlocking() = {
    val wdd = wd
    var x = 0
    getPages.foreach { q =>
      println("running page num: " + x + " out of " + getPages.size + " " + q)
      x += 1
      append(
        wdd,
        onePageRun(q)  + "\n")
    }
  }

  /**
   * Operation specific page load
 *
   * @param i: Offset into scroll on index
   * @return : Page extraction to convenient serializable
   *         representation
   */
  protected def run(i: Int) : String = {
    val p = "" //byPage(i)
    go to p
    //implicitlyWait(Span(15, Seconds)) // Weaker guarantee of waiting
    Thread.sleep(5000) // Stronger guarantee of waiting, safer
    Extr(p, extractCondition).json
  }

  /**
   * Runs Selenium to mimick full client
   * and visits every page on JBS saving source info if
   * successfully loaded based on relevant extract condition.
   */
  def start(): Unit = {
    val wd0 = wd
    Iterator.range(pages._1, pages._2).foreach { q =>
      /**
       * Export to file current batch on completion
       */
      append(
        wd0,
        run(q) + "\n"
      )
    }
  }
}

