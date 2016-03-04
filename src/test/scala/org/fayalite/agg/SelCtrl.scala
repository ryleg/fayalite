package org.fayalite.agg

import java.awt.Component
import javax.swing.{JLabel, JList, JScrollPane}

import ammonite.ops._
import fa._
import org.fayalite.agg.ChromeRunner.Extr
import org.fayalite.layer.Schema
import org.fayalite.util.ToyFrame
import org.jsoup.nodes.Document
import rx._

import scala.util.Try
import Schema._

object SelExample {

  val myCookies = ".mycookies.txt"

  val storeFile = ".metaback"

  val storeZero = MetaStore(List[Cookie](), Map[String, Int]())

  def cookiesZeroS = parseCookiesFromFile(myCookies)

  def optStoreZero = Try{storeZero.copy(
    cookies = cookiesZeroS
  )}.getOrElse(storeZero)

  case class MetaStore(
                        cookies: List[Cookie],
                        pageVistsByDomainTime: Map[String, Int]
                      )
}


class JSONSerHelper(fileBacking: String = ".metaback") {

  import SelExample._
  import rx.ops._

  val store = Var{Try{getStore}.getOrElse(optStoreZero)}

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

  println("cookiezero " + cookiesZeroS)

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

  ad { new JLabel(store().pageVistsByDomainTime.toList.toString)}

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
    val driver = "C:\\chromedriver.exe"
    System.setProperty("webdriver.chrome.driver", driver)
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

