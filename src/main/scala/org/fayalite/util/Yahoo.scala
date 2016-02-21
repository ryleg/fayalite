package org.fayalite.util

import java.io.File

import dispatch.{Req, RequestHandlerTupleBuilder}
import org.json4s

import scala.concurrent.Future
import scala.util.{Failure, Success, Try}

/**
  * Yahoo Finance Stock quote access crawling
  * convenience methods
  */
object Yahoo {

  import fa._

  def getSamples = symbols.grouped(199).map {
    g =>
      g -> sampleQuery(formatSymbols(g))
  }

  val symbols = getSymbols

  val symbolsI = getSymbols.zipWithIndex.toMap

  def getSymbols: List[String] = {
    loadCSV("data\\companylist.csv").tail.map {
      _ (0)
    }.filter { q =>
      !q.contains("""^""") && !q.contains(""".""") &&
      q.length < 5 && !q.contains(" ")
    }
  }

  def formatSymbols(s: Seq[String]) = {
    "(" + s.map { q => "%22" + q + "%22" }.mkString("%2C") +
      ")"
  }

  def formatNumber(b: String) =
    Try {
      b.replaceAll(""""""", "").toDouble
    }.toOption.filter{_ > 0}

  case class BA(Bid: String, Ask: String)

  case class Res(quote: Array[BA])

  case class Qry(results: Res)

  case class Response(query: Qry)

  case class Price(ask: Double, bid: Double)

  case class Observe(time: Int, symPrice: List[(String, Price)])

  def sampleQuery(sym: String) = "https://query.yahooapis.com/v1/public/yql?q=select%20Ask%2C%20Bid%20from%20yahoo.finance.quotes%20where%20symbol%20in%20" + sym + "%0A%09%09&format=json&diagnostics=false&env=http%3A%2F%2Fdatatables.org%2Falltables.env"

  val fToSave = ".yahoo"

  def intTime = System.currentTimeMillis().toInt

  def main(args: Array[String]): Unit = {

    val smp = getSamples.toList

    while (true) {

      smp.map { case (syms, url) =>

        val rr = doRequest(url, syms)

        val obs = rr.map { _.flatMap {
          case (sp, pr) =>
            pr.map{ _ -> sp }.map{_.swap}
        }
        }

        obs.onComplete {
          case Success(x) =>
            val t = intTime
            val sr = Observe(t, x).json
            println(sr)
            fToSave app sr
          case Failure(e) =>
            e.printStackTrace()
        }
        Thread.sleep(30 * 1000)
        0
      }
    }
  }

  def isValid(p: Price) = {
    p.ask > 0 && p.bid > 0
  }

  def doRequest(u: String, syms: List[String]) = {
    import dispatch._
    val r = url(u)
    val t: Req = r.GET
    val svc = r
    val country = Http(svc OK as.String)
    println("doing request " + u)
    country.map{
      c =>
        println("processing response ")
        val pp = c.json[Response].query.results.quote.toList.map{
          case ba @ BA(b, a) =>
            val res = formatNumber(a).flatMap{aa =>
              formatNumber(b).map{
                bb => Price(aa, bb)
              }.filter{isValid}
            }
            res
        }
        syms.zip(pp)
    }
  }
}