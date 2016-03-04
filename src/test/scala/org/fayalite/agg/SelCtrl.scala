package org.fayalite.agg

import java.awt.Component
import java.awt.event.{ActionEvent, ActionListener}
import java.io.File
import javax.swing.{JFileChooser, JLabel, JList, JScrollPane}

import ammonite.ops._
import fa._
import org.fayalite.util.ToyFrame
import org.jsoup.nodes.Document
import rx._
import scala.util.Try
import Schema._

object SelExample {

  val myCookies = ".mycookies.txt"

  val storeFile = ".metaback"

  val storeZero = MetaStore(List[Cookie](), Map[String, Int]())

  def optStoreZero = storeZero

  case class MetaStore(
                        cookies: List[Cookie],
                        pageVistsByDomainTime: Map[String, Int]
                      )
}


class JSONSerHelper(fileBacking: String = ".metaback") {

  import SelExample._
  import rx.ops._

  val store = Var{Try{getStore}.getOrElse({
    println("optStoreZero")
    optStoreZero
  })}

  def getStore: MetaStore = {
    readFromFile(fileBacking).json[MetaStore]
  }

  def writeStore() = {
    writeToFile(fileBacking, store().json)
  }

  store.foreach{_ => writeStore()}

}

import rx.ops._

class SelExample(startingUrl: String = "http://linkedin.com") {

  import SelExample._

  val jss = new JSONSerHelper()

  import jss.store

  val cev = Var(null.asInstanceOf[ChromeWrapper])

  def cee = cev()

  val te = new ToyFrame

  import te.{addButton => button}

  def reInit() = {
    if (cee != null) cee.stop()
    cev() = new ChromeWrapper(Some(startingUrl))
  }

  button("Open Browser", reInit())

  button("Close Browser", cee.stop())

  def ad(e: Component) = te.jp.add(e)

  ad (new JLabel("Stored Cookies:"))

  private val cookies0: List[Cookie] = store().cookies
  val cookiesZero = cookies0.map{_.name}.toArray
  println("store " + store())


  val jls = new JList(cookiesZero)

  val jscp = new JScrollPane(jls)
  te.jp.add(jscp)


  button("Overwrite Cookie Session", {
    store() = store().copy(cookies=cee.dumpCookies)
    ".cookies.txt" app cee.dumpCookies.json
  })

  button("Clear cookies from active browser", cee.clearCookies())
  button("Load stored cookies into active browser", store().cookies.foreach{cee.addCookieProper})
/*
  button("Load .mycookies.txt *DEV*", {
    cee.loadCookiesFrom(myCookies)
  })
*/

 // te.jp.add(jll)

  val urls = Var(List[String]())

  //val selFl = Var("SELECTED_FILE.txt")

  val slf = new JLabel("Selected Files -- __ empty")
  //selFl.foreach{slf.setText}

  ad(slf)


  val fc = new JFileChooser()

  fc.setMultiSelectionEnabled(true)

  fc.addActionListener(new ActionListener{
    override def actionPerformed(e: ActionEvent): Unit = {
      println("action event on Select files")
      slf.setText(
        fc.getSelectedFiles.map{_.getName}.slice(0, 10).mkString(" | ")
      )
    }
  })
  button("Select Files", {
    println("file chooser")
    fc.showOpenDialog(te.jp)
    println("show open")
  })
/*
  button("Process URLs", {
    val fl = fc.getSelectedFile
    val u = scala.io.Source.fromFile(fl).getLines.toList
    println("u " + u)
    slf.setText(fl.getCanonicalPath + " #lines=" + u.length)
    urls() = u
  })
*/


  class MicroList {
    val jls = new JList(cookiesZero)
    val jscp = new JScrollPane(jls)
    te.jp.add(jscp)
  }



  ad { new JLabel("store" + store().pageVistsByDomainTime.toList.toString)}

  button("Run .urls.txt", {
    readLines(".urls.txt").foreach{
      q =>
        println("going to url " + q)
        val domain = q.domain
        println("url domain " + domain)
        cee.goto(q)
        val cur = currentDay + " " + domain
        val prvMap = store().pageVistsByDomainTime
        val prvV = prvMap.getOrElse(cur, 0)
        val newMap = prvMap.updated(cur, prvV + 1)
        println("newmap ? " + newMap)
        store() = store().copy(pageVistsByDomainTime=prvMap.updated(cur, prvV + 1))
        Thread.sleep(15*1000)
    }
  })

  te.finish()

}

object SelCtrl {

  import fa._

  /**
    * Workaround requiring a packaged install with same values in
    * directory or in working directory of IntelliJ git repo
    * @return : Path to driver executable relative
    */
  def getDriverPath = {
    val winDriver = "chromedriver.exe"
    val macDriver = "chromedriver-mac32"
    val driver = if (osName.toLowerCase.contains("win")) winDriver
    else macDriver
    driver
  }

  /**
    * Selenium / ChromeDriver runs a secondary process that proxies
    * communications back, this must reflect an updated binary
    * version of the chromedriver executable release
    * @return
    */
  def setDriverProperty() = {
    System.setProperty("webdriver.chrome.driver", getDriverPath)
  }

  def main(args: Array[String]) {
    setDriverProperty()
    new SelExample()
  }


}

