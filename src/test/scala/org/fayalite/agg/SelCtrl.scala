package org.fayalite.agg

import java.util.Scanner
import javax.swing.JFileChooser

import org.fayalite.agg.ChromeRunner.Extr
import org.fayalite.util.ToyFrame
import org.jsoup.nodes.Document
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
               ) extends FlatSpec with Chrome {

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
    val jc = readLines(s).flatMap{_.json[List[SelSer.Cookie]]}
    jc.foreach{ addCookieProper }
  }

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


  case class MetaStore(
                        cookies: List[SelSer.Cookie],
                        pageVistsByDomain: Map[String, Int]
                      )
}


class JSONSerHelper(fileBacking: String = ".metaback") {

}

class SelExample(startingUrl: String) {

  import SelExample._

  var cee: ChromeWrapper = null //new ChromeExt() //Some(startingUrl))

  def reInit() = {
    if (cee != null) cee.stop()
    cee = new ChromeWrapper(Some(startingUrl))
    cee.started.foreach{_.onComplete{_ => cee.loadCookiesFrom(myCookies)}}
  }

  val te = new ToyFrame

  import te.{addButton => button}

  button("Open Browser", reInit())
  button("Close Browser", cee.stop())
  button("Clear Cookies", cee.clearCookies())
  button("Save Cookie Session", {
    ".cookies.txt" app cee.dumpCookies.json
  })


  button("Load .mycookies.txt", {
    cee.loadCookiesFrom(myCookies)
  })

  button("Load URL CSV & Run", {
    readLines("urls.txt").foreach{
      q =>
        println("going to url " + q)
        val domain = q.domain
        println("url domain " + domain)
        cee.goto(q)
        Thread.sleep(15*1000)
    }
  })

  button("Select CSV File(s)", {
    val fc = new JFileChooser()
    fc.showOpenDialog(te.jp)
    val fl = fc.getSelectedFiles.toList
  })

  val ta = te.addTextInput()

  button("Run Query", {
    println(ta.getText)
  })


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



  def main(args: Array[String]) {
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

