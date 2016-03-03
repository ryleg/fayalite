package org.fayalite.agg

import java.awt.Component
import java.util.Scanner
import javax.swing.{JScrollPane, JList, JLabel, JFileChooser}

import org.fayalite.agg.ChromeRunner.Extr
import org.fayalite.agg.SelSer.Cookie
import org.fayalite.util.ToyFrame
import org.jsoup.nodes.Document
import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.{ChromeOptions, ChromeDriver}
import org.scalatest.FlatSpec
import org.scalatest.selenium.Chrome

import scala.collection.JavaConversions
import scala.util.{Success, Failure, Try}

import ammonite.ops._

import fa._


object SelSer {

  case class Cookie(
                     name: String,
                     domain: String,
                     path: String,
                     value: String,
                     expiry: String
                   )

  def parseCookiesFromFile(s: String) =
    readFromFile(s).json[List[SelSer.Cookie]]
}

import rx._

/**
  * Basic fixes to make Selenium chrome actually useful
  *
  * NOTE: Selenium tests require a binary of ChromeDriver
  * Download and make available during runtime by setting
  * VM opt -Dwebdriver.chrome.driver=/your_path_to/chromedriver
  *
  */
class ChromeWrapper(
                     startingUrl: Option[String] = None
                   ) extends org.scalatest.selenium.WebBrowser {

  val opts = new ChromeOptions()

  val userAgent = "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2226.0 Safari/537.36"
  opts.addArguments("user-agent=" + userAgent)

  implicit val webDriver =  new ChromeDriver(opts)

  def size(x: Int, y: Int) = {
    webDriver.manage().window.setSize(new org.openqa.selenium.Dimension(x, y))
  }

  size(800, 600)

  def goto(tou: String) = {
    go to tou
    numVisits() += 1
  }
  def src = pageSource
  def stop() = close
  def clearCookies() = delete all cookies
  def dumpCookies = {
    import JavaConversions._ // Move to implicits
    webDriver.manage().getCookies.iterator().toList.flatMap{
      q => Try{SelSer.Cookie(q.getName,q.getDomain, q.getPath, q.getValue,
        q.getExpiry.toString)}.toOption
    }
  }

  def addCookieProper(
                       c: SelSer.Cookie
                     ) = {
    add cookie(name=c.name, value=c.value, path=c.path, domain=c.domain)
  }

  def loadCookiesFrom(s: String) = {
    val jc = SelSer.parseCookiesFromFile(s)
    jc.foreach{ addCookieProper }
  }

  val isStarted = Var(false) // change to monad of switch on/off dag

  /**
    * Use this for reacting off of to determine when
    * the browser has actually started and loaded something
    */
  val started = startingUrl.map{q => F{goto(q)}}

  val numVisits = Var(0)



}
/*
java -cp target\scala-2.10\fayalite-test-0.0.3.jar org.fayalite.agg.LIRunner -Dwebdriver.chrome.driver=C:\chromedriver.exe
 */


object SelExample {

  val myCookies = ".mycookies.txt"

  val storeFile = ".metaback"

  val storeZero = MetaStore(List[Cookie](), Map[String, Int]())

  def cookiesZeroS = SelSer.parseCookiesFromFile(myCookies)

  def optStoreZero = Try{storeZero.copy(
    cookies = cookiesZeroS
  )}.getOrElse(storeZero)

  case class MetaStore(
                        cookies: List[SelSer.Cookie],
                        pageVistsByDomainTime: Map[String, Int]
                      )
}


class JSONSerHelper(fileBacking: String = ".metaback") {

}

import rx.ops._

class SelExample(startingUrl: String) {

  import SelExample._

  println("cookiezero " + cookiesZeroS)

  val cev = Var(null.asInstanceOf[ChromeWrapper])

  val store = Var{Try{getStore}.getOrElse(optStoreZero)}

  def getStore: MetaStore = {
    readFromFile(".metaback").json[MetaStore]
  }

  def writeStore() = {
    writeToFile(".metaback", store().json)
  }

  store.foreach{_ => writeStore()}

  def cee = cev()

  val te = new ToyFrame

  import te.{addButton => button}


  val jll = new JLabel("Num page visits: 0")

  def reInit() = {
    if (cee != null) cee.stop()
    cev() = new ChromeWrapper(Some(startingUrl))
    cee.started.foreach{_.onComplete{_ =>
      cee.loadCookiesFrom(myCookies)
      /*      cee.numVisits.foreach{
              v =>

            }*/
    }}
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
/*
  val slf = new JLabel("")
  //selFl.foreach{slf.setText}

  ad(slf)

  val fc = new JFileChooser()

  button("Select URLs File", {
    println("file chooser")
    fc.showOpenDialog(te.jp)
    println("show open")
  })

  button("Process URLs", {
    val fl = fc.getSelectedFile
    val u = scala.io.Source.fromFile(fl).getLines.toList
    println("u " + u)
    slf.setText(fl.getCanonicalPath + " #lines=" + u.length)
    urls() = u
  })*/

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
        store() = store().copy(pageVistsByDomainTime=prvMap.updated(cur, prvV + 1))
        Thread.sleep(15*1000)
    }
  })

 // val ta = te.addTextInput("500")

/*
  button("Run Query", {
    println(ta.getText)
  })
*/
 // page history

  te.finish()

}

object S3QuickSave {

}

object SelCtrl {
  case class ParsedExtr(url: String, soup: Document)

  import fa._
  def readExtrParse[T](path: Path, parser: ParsedExtr => Traversable[T]
                      ) = path.jsonRec[Extr].flatMap { case Extr(url, qq) =>
    qq.map {
      q => parser(ParsedExtr(url, q.soup))
    }
  }.toList.flatten



  def main(args: Array[String]) { //C:\chromedriver.exe
   // System.setProperty("webdriver.chrome.driver=" + driver )
    new SelExample()
  }


  /*

 def indeedRemoteJobsUrl(page: Int) = {
   "http://www.indeed.com/jobs?q=&l=Remote&start=" + page*10
 }

   def tes = {
  new SimpleChrome(
         cwd / 'secret / 'thelocal / 'run,
         (1 to 22).toList.map{i =>
           "http://www.thelocal.se/jobs/?job_keyword=&job_category=engineer&job_category=it&page=" +
             i.toString}
  //     "a"
       ).runBlocking()
   def parse(parsedExtr: ParsedExtr) = parsedExtr match {
     case ParsedExtr(url, q) =>
       q.sel("div.jobsitem").map {
         j =>
           Map("JobTitle" -> j.fsel("div.jobstitle").text,
             "JobLocation" -> j.fsel("div.jilocation").text,
             "JobDescription" -> Try{j.fsel("div.jisummary").text}.getOrElse("---MISSING---"),
             "JobSource" -> j.fsel("img.employer-logo").attr("alt"),
             "JobURL" -> j.parent().attr("href")
           ) ++
             Array("CompanyName", "JobDatePostedAsOfCrawl").zip(
               j.fsel("div.jicompany").text.split("\\|")).toMap
       }
   }
*/
  /* readExtrParse(cwd / 'secret / 'thelocal / 'run, parse)
     .dedupe(getUniqueExistingCompanyNamesSanitized)
     .save((cwd / 'secret / 'thelocal / RelPath("new.csv")).toString())
*/

}

