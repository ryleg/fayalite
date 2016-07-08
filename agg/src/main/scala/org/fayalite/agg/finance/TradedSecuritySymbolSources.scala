package org.fayalite.agg.finance

import java.io.File

import scala.util.Try
import fa._

/**
  * For getting lists of company securities symbols
  */
trait TradedSecuritySymbolSources {

  // http://www.nasdaq.com/screening/company-list.aspx

  val dataFolder = new File("data")
  val historicalFolder = new File(dataFolder, "historical")
  val securitiesFolder = new File(dataFolder, "securities")

  Try { securitiesFolder.mkdirs(); historicalFolder.mkdirs() }

  object Sources {
    val NASDAQ = "NASDAQ"
    val NYSE = "NYSE"
    val AMEX = "AMEX"
  }

  def getSource(source: String) = {
    s"http://www.nasdaq.com/screening/companies-by-industry.aspx?exchange=$source&render=download"
  }

  import Sources._

  case class SecuritySource(
                             exchange: String,
                             url: String
                           ) {
    def localPath = new File(securitiesFolder, exchange + ".csv").getCanonicalPath
  }

  val securitiesSources = Seq(NASDAQ, NYSE, AMEX)
    .map{z => SecuritySource(z, getSource(z))}

  def cleanSymbol(symbol: String) = symbol.noSpaces

  def isValidSymbol(symbol: String) = {
    Seq("$", ".", "^").forall{symbol.containsNot} && symbol.length < 5
  }

  def getSymbolsFromLocal = securitiesSources.flatMap{
    ss =>
      val csv = readCSV(ss.localPath)
      println(ss.exchange, "CSV length", csv.length)
      assert(csv.length > 370)
      val symbolCol = csv
        .map{_(0)}
        .map{cleanSymbol}
        .filter{isValidSymbol}
      println(symbolCol.slice(0, 10))
      symbolCol
  }

}
