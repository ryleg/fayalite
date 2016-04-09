package org.fayalite.agg

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

  def getHistoricalCSVs = {
    val storM = mutable.HashMap[String, mutable.HashMap[String, Float]]()
    yahooSave.listFiles().par.map{f =>
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
      println(storM.size)
    }
  }


  def main(args: Array[String]): Unit = {
    // processCrawl

    // println(getSamples.size)
    //  parCrawl
    //  println(s"num delta per step: ${readIn.size}")
    // historicalRequest
    getHistoricalCSVs

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

  val hidDir = new File(".hidden")
  val yahooSave = new File(hidDir, "yahoo")

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

  val extrSampl = "                            </th></tr><tr><td class=\"yfnc_tabledata1\" nowrap align=\"right\">Apr 8, 2016</td><td class=\"yfnc_tabledata1\" align=\"right\">36.85</td><td class=\"yfnc_tabledata1\" align=\"right\">36.92</td><td class=\"yfnc_tabledata1\" align=\"right\">35.62</td><td class=\"yfnc_tabledata1\" align=\"right\">36.07</td><td class=\"yfnc_tabledata1\" align=\"right\">20,251,100</td><td class=\"yfnc_tabledata1\" align=\"right\">36.07</td></tr><tr><td class=\"yfnc_tabledata1\" nowrap align=\"right\">Apr 7, 2016</td><td class=\"yfnc_tabledata1\" align=\"right\">35.86</td><td class=\"yfnc_tabledata1\" align=\"right\">37.25</td><td class=\"yfnc_tabledata1\" align=\"right\">35.72</td><td class=\"yfnc_tabledata1\" align=\"right\">36.17</td><td class=\"yfnc_tabledata1\" align=\"right\">38,653,800</td><td class=\"yfnc_tabledata1\" align=\"right\">36.17</td></tr><tr><td class=\"yfnc_tabledata1\" nowrap align=\"right\">Apr 6, 2016</td><td class=\"yfnc_tabledata1\" align=\"right\">36.40</td><td class=\"yfnc_tabledata1\" align=\"right\">37.00</td><td class=\"yfnc_tabledata1\" align=\"right\">36.31</td><td class=\"yfnc_tabledata1\" align=\"right\">36.66</td><td class=\"yfnc_tabledata1\" align=\"right\">19,418,600</td><td class=\"yfnc_tabledata1\" align=\"right\">36.66</td></tr><tr><td class=\"yfnc_tabledata1\" nowrap align=\"right\">Apr 5, 2016</td><td class=\"yfnc_tabledata1\" align=\"right\">36.70</td><td class=\"yfnc_tabledata1\" align=\"right\">36.92</td><td class=\"yfnc_tabledata1\" align=\"right\">36.22</td><td class=\"yfnc_tabledata1\" align=\"right\">36.41</td><td class=\"yfnc_tabledata1\" align=\"right\">12,439,200</td><td class=\"yfnc_tabledata1\" align=\"right\">36.41</td></tr><tr><td class=\"yfnc_tabledata1\" nowrap align=\"right\">Apr 4, 2016</td><td class=\"yfnc_tabledata1\" align=\"right\">36.55</td><td class=\"yfnc_tabledata1\" align=\"right\">37.50</td><td class=\"yfnc_tabledata1\" align=\"right\">36.54</td><td class=\"yfnc_tabledata1\" align=\"right\">37.02</td><td class=\"yfnc_tabledata1\" align=\"right\">20,210,800</td><td class=\"yfnc_tabledata1\" align=\"right\">37.02</td></tr><tr><td class=\"yfnc_tabledata1\" nowrap align=\"right\">Apr 1, 2016</td><td class=\"yfnc_tabledata1\" align=\"right\">36.54</td><td class=\"yfnc_tabledata1\" align=\"right\">36.88</td><td class=\"yfnc_tabledata1\" align=\"right\">36.31</td><td class=\"yfnc_tabledata1\" align=\"right\">36.48</td><td class=\"yfnc_tabledata1\" align=\"right\">13,650,400</td><td class=\"yfnc_tabledata1\" align=\"right\">36.48</td></tr><tr><td class=\"yfnc_tabledata1\" nowrap align=\"right\">Mar 31, 2016</td><td class=\"yfnc_tabledata1\" align=\"right\">36.51</td><td class=\"yfnc_tabledata1\" align=\"right\">37.02</td><td class=\"yfnc_tabledata1\" align=\"right\">36.50</td><td class=\"yfnc_tabledata1\" align=\"right\">36.81</td><td class=\"yfnc_tabledata1\" align=\"right\">18,666,200</td><td class=\"yfnc_tabledata1\" align=\"right\">36.81</td></tr><tr><td class=\"yfnc_tabledata1\" nowrap align=\"right\">Mar 30, 2016</td><td class=\"yfnc_tabledata1\" align=\"right\">36.61</td><td class=\"yfnc_tabledata1\" align=\"right\">37.28</td><td class=\"yfnc_tabledata1\" align=\"right\">36.44</td><td class=\"yfnc_tabledata1\" align=\"right\">36.56</td><td class=\"yfnc_tabledata1\" align=\"right\">19,836,500</td><td class=\"yfnc_tabledata1\" align=\"right\">36.56</td></tr><tr><td class=\"yfnc_tabledata1\" nowrap align=\"right\">Mar 29, 2016</td><td class=\"yfnc_tabledata1\" align=\"right\">35.05</td><td class=\"yfnc_tabledata1\" align=\"right\">36.43</td><td class=\"yfnc_tabledata1\" align=\"right\">35.01</td><td class=\"yfnc_tabledata1\" align=\"right\">36.32</td><td class=\"yfnc_tabledata1\" align=\"right\">23,166,500</td><td class=\"yfnc_tabledata1\" align=\"right\">36.32</td></tr><tr><td class=\"yfnc_tabledata1\" nowrap align=\"right\">Mar 28, 2016</td><td class=\"yfnc_tabledata1\" align=\"right\">35.39</td><td class=\"yfnc_tabledata1\" align=\"right\">35.45</td><td class=\"yfnc_tabledata1\" align=\"right\">34.62</td><td class=\"yfnc_tabledata1\" align=\"right\">35.23</td><td class=\"yfnc_tabledata1\" align=\"right\">12,976,800</td><td class=\"yfnc_tabledata1\" align=\"right\">35.23</td></tr><tr><td class=\"yfnc_tabledata1\" nowrap align=\"right\">Mar 24, 2016</td><td class=\"yfnc_tabledata1\" align=\"right\">34.45</td><td class=\"yfnc_tabledata1\" align=\"right\">34.87</td><td class=\"yfnc_tabledata1\" align=\"right\">33.93</td><td class=\"yfnc_tabledata1\" align=\"right\">34.86</td><td class=\"yfnc_tabledata1\" align=\"right\">14,101,500</td><td class=\"yfnc_tabledata1\" align=\"right\">34.86</td></tr><tr><td class=\"yfnc_tabledata1\" nowrap align=\"right\">Mar 23, 2016</td><td class=\"yfnc_tabledata1\" align=\"right\">35.43</td><td class=\"yfnc_tabledata1\" align=\"right\">35.70</td><td class=\"yfnc_tabledata1\" align=\"right\">34.71</td><td class=\"yfnc_tabledata1\" align=\"right\">34.80</td><td class=\"yfnc_tabledata1\" align=\"right\">12,192,600</td><td class=\"yfnc_tabledata1\" align=\"right\">34.80</td></tr><tr><td class=\"yfnc_tabledata1\" nowrap align=\"right\">Mar 22, 2016</td><td class=\"yfnc_tabledata1\" align=\"right\">34.68</td><td class=\"yfnc_tabledata1\" align=\"right\">35.61</td><td class=\"yfnc_tabledata1\" align=\"right\">34.68</td><td class=\"yfnc_tabledata1\" align=\"right\">35.41</td><td class=\"yfnc_tabledata1\" align=\"right\">11,272,300</td><td class=\"yfnc_tabledata1\" align=\"right\">35.41</td></tr><tr><td class=\"yfnc_tabledata1\" nowrap align=\"right\">Mar 21, 2016</td><td class=\"yfnc_tabledata1\" align=\"right\">35.00</td><td class=\"yfnc_tabledata1\" align=\"right\">36.10</td><td class=\"yfnc_tabledata1\" align=\"right\">35.00</td><td class=\"yfnc_tabledata1\" align=\"right\">35.47</td><td class=\"yfnc_tabledata1\" align=\"right\">13,279,400</td><td class=\"yfnc_tabledata1\" align=\"right\">35.47</td></tr><tr><td class=\"yfnc_tabledata1\" nowrap align=\"right\">Mar 18, 2016</td><td class=\"yfnc_tabledata1\" align=\"right\">34.54</td><td class=\"yfnc_tabledata1\" align=\"right\">35.21</td><td class=\"yfnc_tabledata1\" align=\"right\">34.38</td><td class=\"yfnc_tabledata1\" align=\"right\">35.17</td><td class=\"yfnc_tabledata1\" align=\"right\">20,490,800</td><td class=\"yfnc_tabledata1\" align=\"right\">35.17</td></tr><tr><td class=\"yfnc_tabledata1\" nowrap align=\"right\">Mar 17, 2016</td><td class=\"yfnc_tabledata1\" align=\"right\">33.88</td><td class=\"yfnc_tabledata1\" align=\"right\">34.55</td><td class=\"yfnc_tabledata1\" align=\"right\">33.87</td><td class=\"yfnc_tabledata1\" align=\"right\">34.28</td><td class=\"yfnc_tabledata1\" align=\"right\">9,334,100</td><td class=\"yfnc_tabledata1\" align=\"right\">34.28</td></tr><tr><td class=\"yfnc_tabledata1\" nowrap align=\"right\">Mar 16, 2016</td><td class=\"yfnc_tabledata1\" align=\"right\">33.03</td><td class=\"yfnc_tabledata1\" align=\"right\">34.08</td><td class=\"yfnc_tabledata1\" align=\"right\">33.00</td><td class=\"yfnc_tabledata1\" align=\"right\">34.01</td><td class=\"yfnc_tabledata1\" align=\"right\">10,975,700</td><td class=\"yfnc_tabledata1\" align=\"right\">34.01</td></tr><tr><td class=\"yfnc_tabledata1\" nowrap align=\"right\">Mar 15, 2016</td><td class=\"yfnc_tabledata1\" align=\"right\">33.32</td><td class=\"yfnc_tabledata1\" align=\"right\">33.46</td><td class=\"yfnc_tabledata1\" align=\"right\">33.11</td><td class=\"yfnc_tabledata1\" align=\"right\">33.26</td><td class=\"yfnc_tabledata1\" align=\"right\">10,660,800</td><td class=\"yfnc_tabledata1\" align=\"right\">33.26</td></tr><tr><td class=\"yfnc_tabledata1\" nowrap align=\"right\">Mar 14, 2016</td><td class=\"yfnc_tabledata1\" align=\"right\">33.75</td><td class=\"yfnc_tabledata1\" align=\"right\">34.09</td><td class=\"yfnc_tabledata1\" align=\"right\">33.51</td><td class=\"yfnc_tabledata1\" align=\"right\">33.58</td><td class=\"yfnc_tabledata1\" align=\"right\">7,960,300</td><td class=\"yfnc_tabledata1\" align=\"right\">33.58</td></tr><tr><td class=\"yfnc_tabledata1\" nowrap align=\"right\">Mar 11, 2016</td><td class=\"yfnc_tabledata1\" align=\"right\">33.29</td><td class=\"yfnc_tabledata1\" align=\"right\">33.86</td><td class=\"yfnc_tabledata1\" align=\"right\">32.84</td><td class=\"yfnc_tabledata1\" align=\"right\">33.81</td><td class=\"yfnc_tabledata1\" align=\"right\">11,682,700</td><td class=\"yfnc_tabledata1\" align=\"right\">33.81</td></tr><tr><td class=\"yfnc_tabledata1\" nowrap align=\"right\">Mar 10, 2016</td><td class=\"yfnc_tabledata1\" align=\"right\">33.59</td><td class=\"yfnc_tabledata1\" align=\"right\">33.60</td><td class=\"yfnc_tabledata1\" align=\"right\">32.09</td><td class=\"yfnc_tabledata1\" align=\"right\">32.82</td><td class=\"yfnc_tabledata1\" align=\"right\">19,452,000</td><td class=\"yfnc_tabledata1\" align=\"right\">32.82</td></tr><tr><td class=\"yfnc_tabledata1\" nowrap align=\"right\">Mar 9, 2016</td><td class=\"yfnc_tabledata1\" align=\"right\">33.09</td><td class=\"yfnc_tabledata1\" align=\"right\">33.52</td><td class=\"yfnc_tabledata1\" align=\"right\">32.78</td><td class=\"yfnc_tabledata1\" align=\"right\">33.51</td><td class=\"yfnc_tabledata1\" align=\"right\">12,647,100</td><td class=\"yfnc_tabledata1\" align=\"right\">33.51</td></tr><tr><td class=\"yfnc_tabledata1\" nowrap align=\"right\">Mar 8, 2016</td><td class=\"yfnc_tabledata1\" align=\"right\">33.66</td><td class=\"yfnc_tabledata1\" align=\"right\">33.82</td><td class=\"yfnc_tabledata1\" align=\"right\">32.84</td><td class=\"yfnc_tabledata1\" align=\"right\">32.93</td><td class=\"yfnc_tabledata1\" align=\"right\">21,007,300</td><td class=\"yfnc_tabledata1\" align=\"right\">32.93</td></tr><tr><td class=\"yfnc_tabledata1\" nowrap align=\"right\">Mar 7, 2016</td><td class=\"yfnc_tabledata1\" align=\"right\">33.68</td><td class=\"yfnc_tabledata1\" align=\"right\">34.38</td><td class=\"yfnc_tabledata1\" align=\"right\">33.59</td><td class=\"yfnc_tabledata1\" align=\"right\">33.96</td><td class=\"yfnc_tabledata1\" align=\"right\">22,924,000</td><td class=\"yfnc_tabledata1\" align=\"right\">33.96</td></tr><tr><td class=\"yfnc_tabledata1\" nowrap align=\"right\">Mar 4, 2016</td><td class=\"yfnc_tabledata1\" align=\"right\">32.91</td><td class=\"yfnc_tabledata1\" align=\"right\">33.93</td><td class=\"yfnc_tabledata1\" align=\"right\">32.76</td><td class=\"yfnc_tabledata1\" align=\"right\">33.86</td><td class=\"yfnc_tabledata1\" align=\"right\">22,915,200</td><td class=\"yfnc_tabledata1\" align=\"right\">33.86</td></tr><tr><td class=\"yfnc_tabledata1\" nowrap align=\"right\">Mar 3, 2016</td><td class=\"yfnc_tabledata1\" align=\"right\">32.71</td><td class=\"yfnc_tabledata1\" align=\"right\">33.21</td><td class=\"yfnc_tabledata1\" align=\"right\">32.42</td><td class=\"yfnc_tabledata1\" align=\"right\">32.88</td><td class=\"yfnc_tabledata1\" align=\"right\">11,355,600</td><td class=\"yfnc_tabledata1\" align=\"right\">32.88</td></tr><tr><td class=\"yfnc_tabledata1\" nowrap align=\"right\">Mar 2, 2016</td><td class=\"yfnc_tabledata1\" align=\"right\">32.81</td><td class=\"yfnc_tabledata1\" align=\"right\">33.24</td><td class=\"yfnc_tabledata1\" align=\"right\">32.77</td><td class=\"yfnc_tabledata1\" align=\"right\">32.91</td><td class=\"yfnc_tabledata1\" align=\"right\">10,458,600</td><td class=\"yfnc_tabledata1\" align=\"right\">32.91</td></tr><tr><td class=\"yfnc_tabledata1\" nowrap align=\"right\">Mar 1, 2016</td><td class=\"yfnc_tabledata1\" align=\"right\">32.04</td><td class=\"yfnc_tabledata1\" align=\"right\">32.85</td><td class=\"yfnc_tabledata1\" align=\"right\">32.04</td><td class=\"yfnc_tabledata1\" align=\"right\">32.80</td><td class=\"yfnc_tabledata1\" align=\"right\">14,833,600</td><td class=\"yfnc_tabledata1\" align=\"right\">32.80</td></tr><tr><td class=\"yfnc_tabledata1\" nowrap align=\"right\">Feb 29, 2016</td><td class=\"yfnc_tabledata1\" align=\"right\">31.31</td><td class=\"yfnc_tabledata1\" align=\"right\">32.46</td><td class=\"yfnc_tabledata1\" align=\"right\">31.31</td><td class=\"yfnc_tabledata1\" align=\"right\">31.79</td><td class=\"yfnc_tabledata1\" align=\"right\">19,208,700</td><td class=\"yfnc_tabledata1\" align=\"right\">31.79</td></tr><tr><td class=\"yfnc_tabledata1\" nowrap align=\"right\">Feb 26, 2016</td><td class=\"yfnc_tabledata1\" align=\"right\">31.68</td><td class=\"yfnc_tabledata1\" align=\"right\">31.90</td><td class=\"yfnc_tabledata1\" align=\"right\">31.22</td><td class=\"yfnc_tabledata1\" align=\"right\">31.37</td><td class=\"yfnc_tabledata1\" align=\"right\">16,680,200</td><td class=\"yfnc_tabledata1\" align=\"right\">31.37</td></tr><tr><td class=\"yfnc_tabledata1\" nowrap align=\"right\">Feb 25, 2016</td><td class=\"yfnc_tabledata1\" align=\"right\">30.74</td><td class=\"yfnc_tabledata1\" align=\"right\">31.36</td><td class=\"yfnc_tabledata1\" align=\"right\">30.24</td><td class=\"yfnc_tabledata1\" align=\"right\">31.36</td><td class=\"yfnc_tabledata1\" align=\"right\">19,842,300</td><td class=\"yfnc_tabledata1\" align=\"right\">31.36</td></tr><tr><td class=\"yfnc_tabledata1\" nowrap align=\"right\">Feb 24, 2016</td><td class=\"yfnc_tabledata1\" align=\"right\">30.41</td><td class=\"yfnc_tabledata1\" align=\"right\">31.12</td><td class=\"yfnc_tabledata1\" align=\"right\">29.80</td><td class=\"yfnc_tabledata1\" align=\"right\">30.95</td><td class=\"yfnc_tabledata1\" align=\"right\">12,612,000</td><td class=\"yfnc_tabledata1\" align=\"right\">30.95</td></tr><tr><td class=\"yfnc_tabledata1\" nowrap align=\"right\">Feb 23, 2016</td><td class=\"yfnc_tabledata1\" align=\"right\">31.20</td><td class=\"yfnc_tabledata1\" align=\"right\">31.38</td><td class=\"yfnc_tabledata1\" align=\"right\">30.51</td><td class=\"yfnc_tabledata1\" align=\"right\">30.67</td><td class=\"yfnc_tabledata1\" align=\"right\">16,847,500</td><td class=\"yfnc_tabledata1\" align=\"right\">30.67</td></tr><tr><td class=\"yfnc_tabledata1\" nowrap align=\"right\">Feb 22, 2016</td><td class=\"yfnc_tabledata1\" align=\"right\">30.65</td><td class=\"yfnc_tabledata1\" align=\"right\">31.21</td><td class=\"yfnc_tabledata1\" align=\"right\">30.39</td><td class=\"yfnc_tabledata1\" align=\"right\">31.17</td><td class=\"yfnc_tabledata1\" align=\"right\">21,455,300</td><td class=\"yfnc_tabledata1\" align=\"right\">31.17</td></tr><tr><td class=\"yfnc_tabledata1\" nowrap align=\"right\">Feb 19, 2016</td><td class=\"yfnc_tabledata1\" align=\"right\">30.19</td><td class=\"yfnc_tabledata1\" align=\"right\">30.23</td><td class=\"yfnc_tabledata1\" align=\"right\">29.70</td><td class=\"yfnc_tabledata1\" align=\"right\">30.04</td><td class=\"yfnc_tabledata1\" align=\"right\">20,706,100</td><td class=\"yfnc_tabledata1\" align=\"right\">30.04</td></tr><tr><td class=\"yfnc_tabledata1\" nowrap align=\"right\">Feb 18, 2016</td><td class=\"yfnc_tabledata1\" align=\"right\">29.56</td><td class=\"yfnc_tabledata1\" align=\"right\">30.14</td><td class=\"yfnc_tabledata1\" align=\"right\">29.39</td><td class=\"yfnc_tabledata1\" align=\"right\">29.42</td><td class=\"yfnc_tabledata1\" align=\"right\">15,259,400</td><td class=\"yfnc_tabledata1\" align=\"right\">29.42</td></tr><tr><td class=\"yfnc_tabledata1\" nowrap align=\"right\">Feb 17, 2016</td><td class=\"yfnc_tabledata1\" align=\"right\">29.47</td><td class=\"yfnc_tabledata1\" align=\"right\">29.66</td><td class=\"yfnc_tabledata1\" align=\"right\">29.06</td><td class=\"yfnc_tabledata1\" align=\"right\">29.37</td><td class=\"yfnc_tabledata1\" align=\"right\">12,891,600</td><td class=\"yfnc_tabledata1\" align=\"right\">29.37</td></tr><tr><td class=\"yfnc_tabledata1\" nowrap align=\"right\">Feb 16, 2016</td><td class=\"yfnc_tabledata1\" align=\"right\">27.98</td><td class=\"yfnc_tabledata1\" align=\"right\">29.44</td><td class=\"yfnc_tabledata1\" align=\"right\">27.94</td><td class=\"yfnc_tabledata1\" align=\"right\">29.28</td><td class=\"yfnc_tabledata1\" align=\"right\">20,127,300</td><td class=\"yfnc_tabledata1\" align=\"right\">29.28</td></tr><tr><td class=\"yfnc_tabledata1\" nowrap align=\"right\">Feb 12, 2016</td><td class=\"yfnc_tabledata1\" align=\"right\">27.12</td><td class=\"yfnc_tabledata1\" align=\"right\">27.32</td><td class=\"yfnc_tabledata1\" align=\"right\">26.72</td><td class=\"yfnc_tabledata1\" align=\"right\">27.04</td><td class=\"yfnc_tabledata1\" align=\"right\">12,986,200</td><td class=\"yfnc_tabledata1\" align=\"right\">27.04</td></tr><tr><td class=\"yfnc_tabledata1\" nowrap align=\"right\">Feb 11, 2016</td><td class=\"yfnc_tabledata1\" align=\"right\">26.46</td><td class=\"yfnc_tabledata1\" align=\"right\">26.97</td><td class=\"yfnc_tabledata1\" align=\"right\">26.15</td><td class=\"yfnc_tabledata1\" align=\"right\">26.76</td><td class=\"yfnc_tabledata1\" align=\"right\">11,154,900</td><td class=\"yfnc_tabledata1\" align=\"right\">26.76</td></tr><tr><td class=\"yfnc_tabledata1\" nowrap align=\"right\">Feb 10, 2016</td><td class=\"yfnc_tabledata1\" align=\"right\">27.11</td><td class=\"yfnc_tabledata1\" align=\"right\">27.81</td><td class=\"yfnc_tabledata1\" align=\"right\">26.84</td><td class=\"yfnc_tabledata1\" align=\"right\">27.10</td><td class=\"yfnc_tabledata1\" align=\"right\">8,933,600</td><td class=\"yfnc_tabledata1\" align=\"right\">27.10</td></tr><tr><td class=\"yfnc_tabledata1\" nowrap align=\"right\">Feb 9, 2016</td><td class=\"yfnc_tabledata1\" align=\"right\">26.64</td><td class=\"yfnc_tabledata1\" align=\"right\">27.69</td><td class=\"yfnc_tabledata1\" align=\"right\">26.51</td><td class=\"yfnc_tabledata1\" align=\"right\">26.82</td><td class=\"yfnc_tabledata1\" align=\"right\">13,919,100</td><td class=\"yfnc_tabledata1\" align=\"right\">26.82</td></tr><tr><td class=\"yfnc_tabledata1\" nowrap align=\"right\">Feb 8, 2016</td><td class=\"yfnc_tabledata1\" align=\"right\">27.61</td><td class=\"yfnc_tabledata1\" align=\"right\">27.97</td><td class=\"yfnc_tabledata1\" align=\"right\">26.48</td><td class=\"yfnc_tabledata1\" align=\"right\">27.05</td><td class=\"yfnc_tabledata1\" align=\"right\">24,473,600</td><td class=\"yfnc_tabledata1\" align=\"right\">27.05</td></tr><tr><td class=\"yfnc_tabledata1\" nowrap align=\"right\">Feb 5, 2016</td><td class=\"yfnc_tabledata1\" align=\"right\">29.06</td><td class=\"yfnc_tabledata1\" align=\"right\">29.14</td><td class=\"yfnc_tabledata1\" align=\"right\">27.73</td><td class=\"yfnc_tabledata1\" align=\"right\">27.97</td><td class=\"yfnc_tabledata1\" align=\"right\">16,077,500</td><td class=\"yfnc_tabledata1\" align=\"right\">27.97</td></tr><tr><td class=\"yfnc_tabledata1\" nowrap align=\"right\">Feb 4, 2016</td><td class=\"yfnc_tabledata1\" align=\"right\">27.91</td><td class=\"yfnc_tabledata1\" align=\"right\">29.23</td><td class=\"yfnc_tabledata1\" align=\"right\">27.71</td><td class=\"yfnc_tabledata1\" align=\"right\">29.15</td><td class=\"yfnc_tabledata1\" align=\"right\">28,517,000</td><td class=\"yfnc_tabledata1\" align=\"right\">29.15</td></tr><tr><td class=\"yfnc_tabledata1\" nowrap align=\"right\">Feb 3, 2016</td><td class=\"yfnc_tabledata1\" align=\"right\">28.45</td><td class=\"yfnc_tabledata1\" align=\"right\">28.61</td><td class=\"yfnc_tabledata1\" align=\"right\">26.57</td><td class=\"yfnc_tabledata1\" align=\"right\">27.68</td><td class=\"yfnc_tabledata1\" align=\"right\">55,527,600</td><td class=\"yfnc_tabledata1\" align=\"right\">27.68</td></tr><tr><td class=\"yfnc_tabledata1\" nowrap align=\"right\">Feb 2, 2016</td><td class=\"yfnc_tabledata1\" align=\"right\">29.32</td><td class=\"yfnc_tabledata1\" align=\"right\">30.23</td><td class=\"yfnc_tabledata1\" align=\"right\">28.13</td><td class=\"yfnc_tabledata1\" align=\"right\">29.06</td><td class=\"yfnc_tabledata1\" align=\"right\">34,022,500</td><td class=\"yfnc_tabledata1\" align=\"right\">29.06</td></tr><tr><td class=\"yfnc_tabledata1\" nowrap align=\"right\">Feb 1, 2016</td><td class=\"yfnc_tabledata1\" align=\"right\">29.27</td><td class=\"yfnc_tabledata1\" align=\"right\">29.79</td><td class=\"yfnc_tabledata1\" align=\"right\">28.79</td><td class=\"yfnc_tabledata1\" align=\"right\">29.57</td><td class=\"yfnc_tabledata1\" align=\"right\">12,865,800</td><td class=\"yfnc_tabledata1\" align=\"right\">29.57</td></tr><tr><td class=\"yfnc_tabledata1\" nowrap align=\"right\">Jan 29, 2016</td><td class=\"yfnc_tabledata1\" align=\"right\">29.10</td><td class=\"yfnc_tabledata1\" align=\"right\">29.51</td><td class=\"yfnc_tabledata1\" align=\"right\">28.51</td><td class=\"yfnc_tabledata1\" align=\"right\">29.51</td><td class=\"yfnc_tabledata1\" align=\"right\">18,718,300</td><td class=\"yfnc_tabledata1\" align=\"right\">29.51</td></tr><tr><td class=\"yfnc_tabledata1\" nowrap align=\"right\">Jan 28, 2016</td><td class=\"yfnc_tabledata1\" align=\"right\">30.59</td><td class=\"yfnc_tabledata1\" align=\"right\">30.63</td><td class=\"yfnc_tabledata1\" align=\"right\">28.60</td><td class=\"yfnc_tabledata1\" align=\"right\">28.75</td><td class=\"yfnc_tabledata1\" align=\"right\">15,420,500</td><td class=\"yfnc_tabledata1\" align=\"right\">28.75</td></tr><tr><td class=\"yfnc_tabledata1\" nowrap align=\"right\">Jan 27, 2016</td><td class=\"yfnc_tabledata1\" align=\"right\">29.90</td><td class=\"yfnc_tabledata1\" align=\"right\">30.53</td><td class=\"yfnc_tabledata1\" align=\"right\">29.45</td><td class=\"yfnc_tabledata1\" align=\"right\">29.69</td><td class=\"yfnc_tabledata1\" align=\"right\">13,269,900</td><td class=\"yfnc_tabledata1\" align=\"right\">29.69</td></tr><tr><td class=\"yfnc_tabledata1\" nowrap align=\"right\">Jan 26, 2016</td><td class=\"yfnc_tabledata1\" align=\"right\">29.76</td><td class=\"yfnc_tabledata1\" align=\"right\">30.19</td><td class=\"yfnc_tabledata1\" align=\"right\">29.62</td><td class=\"yfnc_tabledata1\" align=\"right\">29.98</td><td class=\"yfnc_tabledata1\" align=\"right\">11,422,600</td><td class=\"yfnc_tabledata1\" align=\"right\">29.98</td></tr><tr><td class=\"yfnc_tabledata1\" nowrap align=\"right\">Jan 25, 2016</td><td class=\"yfnc_tabledata1\" align=\"right\">29.96</td><td class=\"yfnc_tabledata1\" align=\"right\">30.39</td><td class=\"yfnc_tabledata1\" align=\"right\">29.66</td><td class=\"yfnc_tabledata1\" align=\"right\">29.78</td><td class=\"yfnc_tabledata1\" align=\"right\">23,095,500</td><td class=\"yfnc_tabledata1\" align=\"right\">29.78</td></tr><tr><td class=\"yfnc_tabledata1\" nowrap align=\"right\">Jan 22, 2016</td><td class=\"yfnc_tabledata1\" align=\"right\">29.97</td><td class=\"yfnc_tabledata1\" align=\"right\">30.52</td><td class=\"yfnc_tabledata1\" align=\"right\">29.31</td><td class=\"yfnc_tabledata1\" align=\"right\">29.75</td><td class=\"yfnc_tabledata1\" align=\"right\">16,272,100</td><td class=\"yfnc_tabledata1\" align=\"right\">29.75</td></tr><tr><td class=\"yfnc_tabledata1\" nowrap align=\"right\">Jan 21, 2016</td><td class=\"yfnc_tabledata1\" align=\"right\">28.75</td><td class=\"yfnc_tabledata1\" align=\"right\">29.80</td><td class=\"yfnc_tabledata1\" align=\"right\">28.19</td><td class=\"yfnc_tabledata1\" align=\"right\">29.31</td><td class=\"yfnc_tabledata1\" align=\"right\">16,073,900</td><td class=\"yfnc_tabledata1\" align=\"right\">29.31</td></tr><tr><td class=\"yfnc_tabledata1\" nowrap align=\"right\">Jan 20, 2016</td><td class=\"yfnc_tabledata1\" align=\"right\">28.98</td><td class=\"yfnc_tabledata1\" align=\"right\">29.11</td><td class=\"yfnc_tabledata1\" align=\"right\">27.44</td><td class=\"yfnc_tabledata1\" align=\"right\">28.78</td><td class=\"yfnc_tabledata1\" align=\"right\">20,455,100</td><td class=\"yfnc_tabledata1\" align=\"right\">28.78</td></tr><tr><td class=\"yfnc_tabledata1\" nowrap align=\"right\">Jan 19, 2016</td><td class=\"yfnc_tabledata1\" align=\"right\">29.84</td><td class=\"yfnc_tabledata1\" align=\"right\">30.00</td><td class=\"yfnc_tabledata1\" align=\"right\">29.31</td><td class=\"yfnc_tabledata1\" align=\"right\">29.74</td><td class=\"yfnc_tabledata1\" align=\"right\">20,036,400</td><td class=\"yfnc_tabledata1\" align=\"right\">29.74</td></tr><tr><td class=\"yfnc_tabledata1\" nowrap align=\"right\">Jan 15, 2016</td><td class=\"yfnc_tabledata1\" align=\"right\">29.28</td><td class=\"yfnc_tabledata1\" align=\"right\">29.77</td><td class=\"yfnc_tabledata1\" align=\"right\">28.59</td><td class=\"yfnc_tabledata1\" align=\"right\">29.14</td><td class=\"yfnc_tabledata1\" align=\"right\">17,352,000</td><td class=\"yfnc_tabledata1\" align=\"right\">29.14</td></tr><tr><td class=\"yfnc_tabledata1\" nowrap align=\"right\">Jan 14, 2016</td><td class=\"yfnc_tabledata1\" align=\"right\">29.69</td><td class=\"yfnc_tabledata1\" align=\"right\">30.57</td><td class=\"yfnc_tabledata1\" align=\"right\">28.71</td><td class=\"yfnc_tabledata1\" align=\"right\">30.32</td><td class=\"yfnc_tabledata1\" align=\"right\">14,995,700</td><td class=\"yfnc_tabledata1\" align=\"right\">30.32</td></tr><tr><td class=\"yfnc_tabledata1\" nowrap align=\"right\">Jan 13, 2016</td><td class=\"yfnc_tabledata1\" align=\"right\">30.89</td><td class=\"yfnc_tabledata1\" align=\"right\">31.17</td><td class=\"yfnc_tabledata1\" align=\"right\">29.33</td><td class=\"yfnc_tabledata1\" align=\"right\">29.44</td><td class=\"yfnc_tabledata1\" align=\"right\">16,593,700</td><td class=\"yfnc_tabledata1\" align=\"right\">29.44</td></tr><tr><td class=\"yfnc_tabledata1\" nowrap align=\"right\">Jan 12, 2016</td><td class=\"yfnc_tabledata1\" align=\"right\">30.58</td><td class=\"yfnc_tabledata1\" align=\"right\">30.97</td><td class=\"yfnc_tabledata1\" align=\"right\">30.21</td><td class=\"yfnc_tabledata1\" align=\"right\">30.69</td><td class=\"yfnc_tabledata1\" align=\"right\">12,635,300</td><td class=\"yfnc_tabledata1\" align=\"right\">30.69</td></tr><tr><td class=\"yfnc_tabledata1\" nowrap align=\"right\">Jan 11, 2016</td><td class=\"yfnc_tabledata1\" align=\"right\">30.65</td><td class=\"yfnc_tabledata1\" align=\"right\">30.75</td><td class=\"yfnc_tabledata1\" align=\"right\">29.74</td><td class=\"yfnc_tabledata1\" align=\"right\">30.17</td><td class=\"yfnc_tabledata1\" align=\"right\">16,676,500</td><td class=\"yfnc_tabledata1\" align=\"right\">30.17</td></tr><tr><td class=\"yfnc_tabledata1\" nowrap align=\"right\">Jan 8, 2016</td><td class=\"yfnc_tabledata1\" align=\"right\">30.51</td><td class=\"yfnc_tabledata1\" align=\"right\">31.54</td><td class=\"yfnc_tabledata1\" align=\"right\">30.00</td><td class=\"yfnc_tabledata1\" align=\"right\">30.63</td><td class=\"yfnc_tabledata1\" align=\"right\">26,299,600</td><td class=\"yfnc_tabledata1\" align=\"right\">30.63</td></tr><tr><td class=\"yfnc_tabledata1\" nowrap align=\"right\">Jan 7, 2016</td><td class=\"yfnc_tabledata1\" align=\"right\">30.97</td><td class=\"yfnc_tabledata1\" align=\"right\">31.19</td><td class=\"yfnc_tabledata1\" align=\"right\">30.02</td><td class=\"yfnc_tabledata1\" align=\"right\">30.16</td><td class=\"yfnc_tabledata1\" align=\"right\">20,495,000</td><td class=\"yfnc_tabledata1\" align=\"right\">30.16</td></tr><tr><td class=\"yfnc_tabledata1\" nowrap align=\"right\">Jan 6, 2016</td><td class=\"yfnc_tabledata1\" align=\"right\">31.67</td><td class=\"yfnc_tabledata1\" align=\"right\">32.46</td><td class=\"yfnc_tabledata1\" align=\"right\">31.60</td><td class=\"yfnc_tabledata1\" align=\"right\">32.16</td><td class=\"yfnc_tabledata1\" align=\"right\">16,026,500</td><td class=\"yfnc_tabledata1\" align=\"right\">32.16</td></tr><tr><td class=\"yfnc_tabledata1\" nowrap align=\"right\">Jan 5, 2016</td><td class=\"yfnc_tabledata1\" align=\"right\">31.55</td><td class=\"yfnc_tabledata1\" align=\"right\">32.33</td><td class=\"yfnc_tabledata1\" align=\"right\">31.53</td><td class=\"yfnc_tabledata1\" align=\"right\">32.20</td><td class=\"yfnc_tabledata1\" align=\"right\">14,294,000</td><td class=\"yfnc_tabledata1\" align=\"right\">32.20</td></tr><tr><td class=\"yfnc_tabledata1\" colspan=\"7\" align=\"center\">"

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