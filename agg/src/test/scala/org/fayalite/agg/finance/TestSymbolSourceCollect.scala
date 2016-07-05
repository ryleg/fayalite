package org.fayalite.agg.finance

import java.io.File
import java.net.URLEncoder

import org.fayalite.agg.Dispatch
import org.fayalite.agg.yahoo.finance.Yahoo
import org.scalatest.FunSuite

/**
  * Step by step to grab historical stock data
  */
class TestSymbolSourceCollect
  extends FunSuite
    with TradedSecuritySymbolSources {
  //  implicit val ec = scala.concurrent.ExecutionContext.Implicits.global
  import fa._

  implicit class URLStringExt(s: String) {
    def encoded = URLEncoder.encode(s, "UTF-8")
  }

  ignore( // Uncomment on first run
    //test(
      "Grab security list CSV") {
      securitiesSources.foreach {
        case ss @ SecuritySource(exchange, url) =>
          val fut = Dispatch.getRequest(url)
          val r = fut.get
          assert(r.length > 50000)
          println(exchange, "Response length str", r.length)
          writeToFile(ss.localPath, r)
      }
    }

  println("Parsing from saved data")

  ignore("Parse securities") {

    val symbols = getSymbolsFromLocal
    println("numSymbols", symbols.length)
    assert(symbols.length > 5000)

    symbols.filter{_.exists{z => !z.isLetter || !z.isUpper}}
      .foreach{ z =>
        println("Bad character (nonLetter) detected on symbol ", z)
      }

    assert(symbols.forall{_.forall{q => q.isLetter && q.isUpper}})

  }

  test("Request historical securities") {

    getSymbolsFromLocal.map { s =>
      val hr = Yahoo.formatHistoricalRequest(s.encoded)
      T {
        Dispatch.getRequest(hr).get
      }.foreach{
        z =>
          writeToFile(new File(historicalFolder, s), z)
      }
    }
  }

}
