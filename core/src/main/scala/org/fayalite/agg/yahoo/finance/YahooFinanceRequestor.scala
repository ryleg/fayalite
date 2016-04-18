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

  def sampleQuery(sym: String) = "https://query.yahooapis.com/v1/public/yql?q=select%20Ask%2C%20Bid%20from%20yahoo.finance.quotes%20where%20symbol%20in%20" + sym + "%0A%09%09&format=json&diagnostics=false&env=http%3A%2F%2Fdatatables.org%2Falltables.env"

  def getSamples = symbols.grouped(199).map {
    g =>
      g -> sampleQuery(formatSymbols(g))
  }

  val fToSave = ".yahoo1"
  val hidDir = new File(".hidden")
  val yahooSave = new File(hidDir, "yahoo")
  val gbtime = new File(hidDir, "gbtime")

}

/*

/*  implicit def dataBytesToIntBuffer(db: Array[Byte]) = {
    ByteBuffer.wrap(db)
      .order(ByteOrder.nativeOrder())
      .asIntBuffer()
  }*/

 */