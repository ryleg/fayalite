package org.fayalite.agg

import org.fayalite.agg.ChromeRunner.Extr
import org.jsoup.nodes.Document

import scala.collection.JavaConversions
import scala.util.{Success, Failure, Try}

import ammonite.ops._

import fa._

/**
 * NOTE: Selenium tests require a binary of ChromeDriver
 * Download and make available during runtime by setting
 * VM opt -Dwebdriver.chrome.driver=/your_path_to/chromedriver
 *
 */


object SeleniumScrapeExample {
  case class ParsedExtr(url: String, soup: Document)

  import fa._
  def readExtrParse[T](path: Path, parser: ParsedExtr => Traversable[T]
                      ) = path.jsonRec[Extr].flatMap { case Extr(url, qq) =>
    qq.map {
      q => parser(ParsedExtr(url, q.soup))
    }
  }.toList.flatten


  def indeedRemoteJobsUrl(page: Int) = {
    "http://www.indeed.com/jobs?q=&l=Remote&start=" + page*10
  }


  def main(args: Array[String]) {

    println("main")
  }


    def tes = {
   new SimpleChrome(
          cwd / 'secret / 'thelocal / 'run,
          (1 to 22).toList.map{i =>
            "http://www.thelocal.se/jobs/?job_keyword=&job_category=engineer&job_category=it&page=" +
              i.toString},
        "a"
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

   /* readExtrParse(cwd / 'secret / 'thelocal / 'run, parse)
      .dedupe(getUniqueExistingCompanyNamesSanitized)
      .save((cwd / 'secret / 'thelocal / RelPath("new.csv")).toString())
*/
  }
}

