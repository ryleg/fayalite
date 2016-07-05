package org.fayalite.agg.yahoo.finance

import java.awt.Color
import java.io.File
import dispatch.{Http, as, url}
import org.fayalite.agg.ProxyManager
import org.fayalite.agg.ProxyManager.ProxyDescr

import scala.collection.mutable
import scala.concurrent.Future
import scala.util.{Failure, Success}
import fa._


/**
  * Yahoo Finance Stock quote access crawling
  * convenience methods
  */
object Yahoo extends YahooFinanceRequestor
  {

  import YahooRelatedSchema._

  def main(args: Array[String]): Unit = {
    val img = readImg(".tesyahoo.png")
    val data = img.getAllData // just for demonstrating conversion
    // you can also just get the bytes from the file directly.

    println("data length" + data.length)
    val h = img.getHeight
    println("h w " + h + " " + img.getWidth)

    val m = data.grouped(4*img.getWidth).toArray





    /*
    val img2 = img.sizeDuplicate
    val data2 = img2.getAllData
    data.copyTo(data2)
    img2.save(".tesyahoo2.png")
*/
    //img.getRGB(0, 0, img.getWidth, 1)
    //reIndex



  }

  def reIndex: Unit = {
    val r2 = getGroupByTimeIndexed

    val byTime = r2.map {
      case (x, mz) =>
        DayQuotes(Day(x), mz.toSeq.map { case (q, z) => CompanyPrice(q, z) })
    }.toSeq

    val timeIdx = byTime.map {
      _.day
    }.distinct.sortBy {
      _.encoded
    }
      .zipWithIndex.toMap
    val companyIdx = byTime.flatMap {
      _.quotes.map {
        _.company
      }
    }.distinct
      .sorted.zipWithIndex.toMap
    val priceIdx = byTime.flatMap {
      _.quotes.map {
        _.price
      }
    }
      .distinct.sorted.zipWithIndex.toMap

    println("Price Idx Length " + priceIdx)

    val enc = byTime.map {
      s =>
        EncodedDayQuotes(timeIdx.get(s.day).get, s.quotes.map {
          z =>
            EncodedCompanyPrice(companyIdx.get(z.company).get,
              priceIdx.get(z.price).get)
        }
        )
    }

    enc.saveAsJson(".testyahoo")
  }

  def recodeToImage: Boolean = {
    val enc = ".testyahoo".jsonLines[EncodedDayQuotes]

    val i = createImage(6000, 3000).black
    enc.foreach {
      q =>
        val row = q.day
        q.quotes.foreach {
          t =>
            val col = t.company
            val color = t.price
            i.setRGB(row, col, color)
          // println(color)
        }
    }
    i.save(".tesyahoo.png")
  }

  def dbg = {

    val r2 = getGroupByTimeIndexed

    println(r2.size.toString + " r2 size")
    val minV = r2.map {
      _._2.map {
        _._2
      }.min
    }.min
    println("MinV" + minV)
    val maxV = r2.map {
      _._2.map {
        _._2
      }.max
    }.max
    println("Maxv " + maxV)

    val uniqD = r2.map {
      _._1
    }.zipWithIndex.toMap

    val ssy = r2.flatMap {
      _._2.keys
    }.toSet[String].toSeq.zipWithIndex.toMap

    println("Number unique sym " + ssy.size)

    val r3 = r2.map {
      case (x, y) =>
        uniqD.get(x).get -> y.map { case (z, w) => ssy.get(z).get -> w }
    }

    val r3t = r3.flatMap{
      case (day, cmppr) =>
        cmppr.map{
          case (cmp, pr) =>
            cmp -> (day -> pr)
        }
    }.groupBy{_._1}.map{
      case (k, v) => k -> v.map{_._2}.toMap
    } // cmp day pr


    var r3p = runOp(r3t.toSeq)

    for (iter <- 1 to 10) {
      println("Iteration " + iter)
      r3p = runOp(r3p)

      val i = createImage(uniqD.size, ssy.size)
      val g = i.createGraphics()
      g.setColor(Color.BLACK)
      g.fillRect(0, 0, uniqD.size, ssy.size)

      r3p.foreach {
        case (cmpIdent, colEntries) =>
          colEntries.foreach {
            case (dayIdent, dblVal) =>
              val d: Double = dblVal - minV
              i.setRGB(dayIdent, cmpIdent, getHueColor(Math.log(d) / Math.log(maxV)).getRGB)
          }
      }

      i.save(s".omg-$iter.png")
    }
  }

  def runOp(r3: Seq[(Int, Map[Int, Double])]): Seq[(Int, Map[Int, Double])] = {
    r3.grouped(3).withFilter {
      _.size == 3
    }.toSeq.flatMap {
      case Seq(x, y, z) =>
        val prefs = x._2.map {
          case (k, v) =>
            val c1 = y._2.get(k)
            val c2 = z._2.get(k)
            val xPrefersZ = if (c1.isEmpty) true
            else {
              if (c2.isEmpty) false
              else {
                val c1g = c1.get
                val c2g = c2.get
                val vCloserC2 = Math.abs(v - c2g) < Math.abs(v - c1g)
                vCloserC2
              }
            }
            xPrefersZ
        }

        val tp = prefs.size
        val votes = prefs.count { q => q }
        if (votes > tp / 2) {
          val newZ = y._1 -> z._2
          val newY = z._1 -> y._2
          println("swap " + votes)
          Seq(x, newY, newZ)
        } else {
          Seq(x, y, z)
        }
    }
  }

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
/*

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

*/

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

/*
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
*/

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

  def historicalURLs = getSymbols.map { q => q -> formatHistoricalRequest(q) }

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