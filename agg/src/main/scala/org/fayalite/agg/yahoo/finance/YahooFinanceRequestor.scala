package org.fayalite.agg.yahoo.finance

import java.io.File

/**
  * Allows wrapping requests with symbols to
  * get sample data for experimental tests. DO NOT
  * USE FOR BULK REQUESTS MORE FREQUENTLY THAN 30s
  * Yahoo doesn't appreciate that and this is supposed
  * to be just for getting small! data samples
  */
trait YahooFinanceRequestor extends SymbolLookupRegistry
 with YahooTestUtils {

  def getSamples = getSymbols.grouped(199).map {
    g =>
      g -> mkYQLURL(formatSymbols(g))
  }

  val fToSave = ".yahoo1"
  val hidDir = new File(".hidden")
  val yahooSave = new File(hidDir, "yahoo")

}