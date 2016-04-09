package org.fayalite.agg

import org.fayalite.agg.ProxyManager.ProxyDescr

import scala.collection.mutable
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
    readCSV("data\\companylist.csv").tail.map {
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

  case class SymbolPrice(symbol: String, price: Price)

  case class Observe(time: Int, symPrice: List[(String, Price)])

  case class Observe2(time: Int, symPrice: List[SymbolPrice])

  def sampleQuery(sym: String) = "https://query.yahooapis.com/v1/public/yql?q=select%20Ask%2C%20Bid%20from%20yahoo.finance.quotes%20where%20symbol%20in%20" + sym + "%0A%09%09&format=json&diagnostics=false&env=http%3A%2F%2Fdatatables.org%2Falltables.env"

  val fToSave = ".yahoo1"

  def intTime = System.currentTimeMillis().toInt

  def main(args: Array[String]): Unit = {
      import fa._
      val readIn = readLines(".yahoo").map{_.json[Observe]}.toSeq
      val maxS = readIn.map{_.symPrice.map(_._2.ask).max}.max
      val minS = readIn.map{_.symPrice.map(_._2.ask).min}.min
      println("min max " + minS + " " + maxS)
      println(s"num observations: ${readIn.size}")

      val r2 = readIn.map{q => Observe2(q.time, q.symPrice.map{
        case (x,y) => SymbolPrice(x,y)
      })}

      val lastSeen = mutable.Map[String, (Double, Int)]()

      r2.foreach{
        case Observe2(time, symprice) =>
          symprice.foreach{
            case SymbolPrice(s, p) =>
              val lastPrice = lastSeen.getOrElseUpdate(s, (p.ask, 0))
              if (lastPrice._1 != p.ask) lastSeen(s) = (p.ask, lastPrice._2 + 1)
          }
      }

      val changers = lastSeen.map{case (x, (y,z)) => x -> z }.toSeq
        .sortBy{_._2}.reverse

      changers.slice(0, 30).foreach{println}

    //  println(s"num delta per step: ${readIn.size}")



//    runCrawl
  }

  def runCrawl: Unit = {
    val smp = getSamples.toList
    while (true) {
      smp.map { case (syms, url) =>
        val rr = doRequest(url, syms)

        val obs = rr.map {
          _.flatMap {
            case (sp, pr) =>
              pr.map {
                _ -> sp
              }.map {
                _.swap
              }
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

  def doRequest(u: String, syms: List[String],
                proxy: Option[ProxyDescr] = None) = {
    import dispatch._
    val r = url(u)
    proxy.foreach { p =>
      r.setProxyServer( // make implicit conv
        new com.ning.http.client.ProxyServer(p.host, p.port, p.user, p.pass)
      )
    }
    val t: Req = r.GET
    val svc = r
    val country = Http(svc OK as.String)
    //  println("doing request " + u)
    country.map{
      c =>
        // println("processing response ")
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