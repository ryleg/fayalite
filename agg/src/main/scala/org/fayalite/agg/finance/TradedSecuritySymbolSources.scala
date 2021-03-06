package org.fayalite.agg.finance

import java.io.File

import scala.util.Try
import fa._

object Sources {

  // Enums for nasdaq.com company requests.

  val NASDAQ = "NASDAQ"
  val NYSE = "NYSE"
  val AMEX = "AMEX"

  /**
    * Gives URL to download CSV of sources from exchange supported above
    * @param source : NASDAQ, NYSE, AMEX
    * @return : CSV Link
    */
  def getSource(source: String) = {
    s"http://www.nasdaq.com/screening/companies-by-industry.aspx?exchange=$source&render=download"
  }

}



/**
  * For getting lists of company securities symbols
  * See : http://www.nasdaq.com/screening/company-list.aspx
  *
  * For complete list see:
  * http://stackoverflow.com/questions/5246843/how-to-get-a-complete-list-of-ticker-symbols-from-yahoo-finance
  // Stock research center
  // https://biz.yahoo.com/r/

  *
  */
trait TradedSecuritySymbolSources {

  val dataFolder = new File("data")
  val historicalFolder = new File(dataFolder, "historical")
  val securitiesFolder = new File(dataFolder, "securities")

  Try { securitiesFolder.mkdirs(); historicalFolder.mkdirs() }

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
