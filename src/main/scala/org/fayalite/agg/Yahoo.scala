package org.fayalite.agg

import java.awt.Color
import java.awt.image.DataBufferByte
import java.io.File
import java.text.SimpleDateFormat

import com.github.tototoshi.csv.CSVReader
import dispatch.{Http, as, url}
import org.fayalite.agg.ProxyManager.ProxyDescr

import scala.collection.mutable
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
    }.toOption.filter {
      _ > 0
    }

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

  val hidDir = new File(".hidden")
  val yahooSave = new File(hidDir, "yahoo")

  val gbtime = new File(hidDir, "gbtime")

  import fa._


  def getHistoricalCSVs = {
    val storM = mutable.HashMap[String, mutable.HashMap[String, Float]]()
    yahooSave.listFiles().par.foreach{f =>
      val nm = f.getName
      println("Reading csv " + f.getCanonicalPath)
      CSVReader.open(f).toStream().tail
        .withFilter{_.nonEmpty}
        .withFilter{_(0) != ""}
        .foreach{
          q =>
            val time = q.head
            Try {
              val open = q(1).toFloat
              synchronized {
                if (storM.contains(time)) {
                  storM(time)(nm) = open
                } else {
                  storM(time) = mutable.HashMap(nm -> open)
                }
              }
            }
        }
    }
    storM.par.foreach{
      case (datetime, quotes) =>
        val f = new File(gbtime, datetime.replaceAll("\\-", "_"))
        writeToFile(f, quotes.toSeq.sortBy{_._1}.prettyTSVString)
    }

    //storM.map{_._2.size}.toSeq.sorted.reverse.slice(0,100).foreach{println}
  }

  def getColor(power: Double) = {
    val H = power
    val S = 0.9D
    val B = 0.9D
    Color.getHSBColor(H.toFloat, S.toFloat, B.toFloat)
  }

  def main(args: Array[String]): Unit = {
    // processCrawl

    val gbf = gbtime.listFiles()

    val r2 = gbf.map{
      q =>
        val f = q.getName
        val qts = readLines(q).map{
          q =>
            val a = q.split("\t")
            a(0) -> a(1).toDouble
        }.toMap
        f -> qts
    }

    val minV = r2.map{_._2.map{_._2}.min}.min
    println("MinV" + minV)
    val maxV = r2.map{_._2.map{_._2}.max}.max
    println("Maxv " + maxV)

    val uniqD = r2.map{_._1}.zipWithIndex.toMap

    val ssy = r2.flatMap{_._2.keys}.toSet[String].toSeq.zipWithIndex.toMap

    println("Number unique sym " + ssy.size)

    val i = createImage(uniqD.size, ssy.size)
    //val db = i.getAllData

    println(i.pixelLength.toString + " pixel length")


    val r3 = r2.map{
      case (x,y) =>
        uniqD.get(x).get -> y.map{case (z,w) => ssy.get(z).get -> w}
    }

    r3.foreach{
      case (dayIdent, colEntries) =>
        colEntries.foreach{
          case (companyIdent, dblVal) =>
            val d: Double = dblVal - minV
            i.setRGB(dayIdent, companyIdent, getColor( Math.log(d) / Math.log(maxV)).getRGB)
        }
    }

    i.save(".omg.png")



    // println(getSamples.size)
    //  parCrawl
    //  println(s"num delta per step: ${readIn.size}")
    // historicalRequest
//    getHistoricalCSVs

    /*

  }
  qts.map{
    case (t, o) =>
      t -> (nm -> o)
  }
}.toList.gbk.toList.sortBy{_._1}.foreach{
case (t, g) =>
  println(t, g.length)
}

*/


    //    runCrawl
  }
/*
  def getOpens = {
    getHistoricalCSVs.map {
      case (nm, z) =>
        // val df = new SimpleDateFormat("YYYY-MM-dd")
        val ret = nm -> z.tail.map{q =>
          //df.parse(q(0)).getTime
          q(0) -> q(1).toFloat}
        println("processed " + nm)
        ret
    }
  }*/

  def processCrawl: Unit = {
    import fa._
    val readIn = readLines(".yahoo").map {
      _.json[Observe]
    }.toSeq
    val maxS = readIn.map {
      _.symPrice.map(_._2.ask).max
    }.max
    val minS = readIn.map {
      _.symPrice.map(_._2.ask).min
    }.min
    println("min max " + minS + " " + maxS)
    println(s"num observations: ${readIn.size}")

    val r2 = readIn.map { q => Observe2(q.time, q.symPrice.map {
      case (x, y) => SymbolPrice(x, y)
    })
    }

    val lastSeen = mutable.Map[String, (Double, Int)]()

    r2.foreach {
      case Observe2(time, symprice) =>
        symprice.foreach {
          case SymbolPrice(s, p) =>
            val lastPrice = lastSeen.getOrElseUpdate(s, (p.ask, 0))
            if (lastPrice._1 != p.ask) lastSeen(s) = (p.ask, lastPrice._2 + 1)
        }
    }

    val changers = lastSeen.map { case (x, (y, z)) => x -> z }.toSeq
      .sortBy {
        _._2
      }.reverse

    changers.slice(0, 30).foreach {
      println
    }
  }

  def runCrawl: Unit = {
    val smp = getSamples.toList
    while (true) {
      smp.map { case (syms, url) =>
        val rr = doRequest(url, syms)

        val obs = stripGarbage(rr)

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


  def stripGarbage(
                    rr: Future[List[(String, Option[Price])]]) = rr.map {
    // Ignore garbage responses
    _.flatMap {
      case (sp, pr) =>
        pr.map {
          _ -> sp
        }.map {
          _.swap
        }
    }
  }

  def parCrawl = {
    getSamples.toList
      .zip(ProxyManager.getProxies)
      .foreach {
        case ((syms, url), p) =>
          F {
            while (true) {
              val rr = doRequest(url, syms, Some(p))
              val o = stripGarbage(rr)
              processResponse(o)
              Thread.sleep(30 * 1000)
            }
          }
      }
    Thread.sleep(Long.MaxValue)
  }

  def processResponse(o: Future[List[(String, Price)]]): Unit = {
    o.onComplete {
      case Success(x) =>
        val t = System.currentTimeMillis()
        x.foreach {
          case (sym, price) =>
            new File(yahooSave, sym)
              .getCanonicalPath
              .append(
                Seq(t, price.ask, price.bid)
                  .map {
                    _.toString
                  }
                  .mkString("\t")
              )
        }
      case Failure(e) =>
        e.printStackTrace()
    }
  }

  def isValid(p: Price) = {
    p.ask > 0 && p.bid > 0
  }

  def formatHistoricalRequest(s: String) = "http://real-chart.finance.yahoo.com" +
    s"/table.csv?s=$s" +
    "&d=3&e=9&f=2016&g=d&a=3&b=12&c=1996&ignore=.csv"

  def historicalURLs = symbols.map { q => q -> formatHistoricalRequest(q) }

  def doRequest(u: String, syms: List[String],
                proxy: Option[ProxyDescr] = None) = {
    import dispatch._
    val r = url(u)
    proxy.foreach { p =>
      r.setProxyServer(// make implicit conv
        new com.ning.http.client.ProxyServer(p.host, p.port, p.user, p.pass)
      )
    }
    val t: Req = r.GET
    val svc = r
    val country = Http(svc OK as.String)
    //  println("doing request " + u)
    country.map {
      c =>
        // println("processing response ")
        val pp = c.json[Response].query.results.quote.toList.map {
          case ba@BA(b, a) =>
            val res = formatNumber(a).flatMap { aa =>
              formatNumber(b).map {
                bb => Price(aa, bb)
              }.filter {
                isValid
              }
            }
            res
        }
        syms.zip(pp)
    }
  }

  import scala.util.matching.Regex


  def getRequest(x: String)(implicit proxyD: Option[ProxyDescr] = None) = {
    val rq = url(x).GET
    proxyD.foreach { p =>
      rq.setProxyServer(// make implicit conv
        new com.ning.http.client.ProxyServer(p.host, p.port, p.user, p.pass)
      )
    }
    Http(rq OK as.String)
  }

  def requestToAppend(url: String, fnm: String)
                     (implicit proxyD: Option[ProxyDescr] = None) = {
    getRequest(url).onComplete {
      case Success(x) => fnm.app(x)
      case Failure(e) => e.printStackTrace()
    }
  }

  def historicalRequest = {

    val urls = historicalURLs

    println(urls)

    historicalURLs.foreach {
      case (sym, url) =>
        val appendTo: String = new File(yahooSave, sym).getCanonicalPath
        println(appendTo)
        requestToAppend(url, appendTo)
        Thread.sleep(1000)


      //  Thread.sleep(Long.MaxValue)
      /*   import dispatch._
     val r = "[-+]?([0-9][0-9]\\.[0-9][0-9])".r
     // getRequest("https://ca.finance.yahoo.com/q/hp?s=YHOO")
 //      .foreach{
   //    q =>
       val rt = r findAllIn extrSampl
       val rtl = rt.toList
     println(rtl.size)
     //}*/
    }

  }
}