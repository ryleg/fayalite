package org.fayalite.agg

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

/**
 * NOTE: Selenium tests require a binary of ChromeDriver
 * Download and make available during runtime by setting
 * VM opt -Dwebdriver.chrome.driver=/your_path_to/chromedriver
 *
 */
class ChromeExt(startingUrl: Option[String] = None) extends FlatSpec with Chrome {
  def goto(tou: String) = go to tou
  def src = pageSource
  def stop() = close
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
  startingUrl.foreach{q => F{goto(q)}}
                  }
/*
java -cp target\scala-2.10\fayalite-test-0.0.3.jar org.fayalite.agg.LIRunner -Dwebdriver.chrome.driver=C:\chromedriver.exe
 */

class SelExample(startingUrl: String) {

  var cee: ChromeExt = new ChromeExt(Some(startingUrl))

  val te = new ToyFrame

  te.addButton("Open Browser", {
    cee.stop()
    val ce = new ChromeExt(Some(startingUrl))
    cee = ce
  })

  te.addButton("Close Browser", {
      cee.stop()
  })

  te.addButton("Dump Cookies", {
    ".cookies.txt" app cee.dumpCookies.json
  })

  te.addButton("Load Cookies(TBI)", {
     val jc = readFromFile(".mycookies.txt").json[List[SelSer.Cookie]]
    println(jc)
    jc.foreach{ cee.addCookieProper }
  })
  te.addButton("Load URL CSV & Run", {
    readLines("urls.txt").foreach{
      q => cee.goto(q)
      Thread.sleep(15*1000)
    }
  })

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

