package org.fayalite.agg.yahoo.finance

import fa._

/**
  * Pretty simple way to get stock simples
  * I just grabbed the company list from Yahoo Finance
  * main page from NASDAQ? I think or something.
  */
trait SymbolLookupRegistry {

  /**
    * See YQL examples for additional information
    * this just generates a URL which when visited returns real
    * time quote information for whatever symbols are encoded
    * into the request. MAXIMUM LIMIT of 199 SYMBOLS PER REQUEST
    * MAXIMUM REQUESTS = 1 per 30 seconds
    * VIOLATING MAX REQUESTS WILL GET YOU BANNED FROM YAHOO!
    * @param htmlEncodedSymbols : See below def, a series of
    *                           symbols encoded the way Yahoo expects
    * @return : URL https for visiting, use a GET request to grab it
    */
  def mkYQLURL(htmlEncodedSymbols: String) = "https://query.yahooapis.com/v1/public/yql?q=select%20Ask%2C%20Bid%20from%20yahoo.finance.quotes%20where%20symbol%20in%20" + htmlEncodedSymbols + "%0A%09%09&format=json&diagnostics=false&env=http%3A%2F%2Fdatatables.org%2Falltables.env"

  // TODO: Switch to URLEncode, quick and dirty for now since all symbols are letters
  /**
    * HTML encode a series of company stock symbols into the format
    * Yahoo's YQL expects
    * @param s : Sequence of sanitized company symbols
    * @return : Chunk for embedding into YQL url
    */
  def formatSymbols(s: Seq[String]) = {
    "(" + s.map { q => "%22" + q + "%22" }.mkString("%2C") +
      ")"
  }

  /**
    * Generate a URL for a GET query for a given list of company symbols
    * @param sym : List of company symbols MAXIMUM LENGTH 199
    * @return URL to GET later
    */
  def symbolsToQuery(sym: Seq[String]) = {
    mkYQLURL(formatSymbols(sym))
  }

  /**
    * Yahoo's own company stock symbol listing includes a bunch of
    * garbage that their finanace quote request API doesn't like.
    *
    * This just excludes those errata completely to make sure the
    * request won't throw some invalid response
    * @param companySymbolNameRaw : As picked up from any CSV of stock symbols
    * @return : Whether that symbol looks valid enough to make a request
    *         off of.
    */
  def isValidCompanyStockSymbol(companySymbolNameRaw: String) = {
    val q = companySymbolNameRaw
    !q.contains("""^""") && !q.contains(""".""") &&
      q.length < 5 && !q.contains(" ")
  }

  /**
    * Specific to Yahoo CSVs with a stock symbol in first column.
    * @param path : Path to CSV downloaded from Yahoo website
    * @return : List of valid company stock symbols
    */
  def parseStockSymbolsCSV(path: String) = {
    readCSV(path).tail.map {
      _ (0)
    }.filter {isValidCompanyStockSymbol }
  }

  /**
    * Quick accessor for testing, use a conf for more precision
    * @return : List of symbols of stocks
    */
  def getSymbols = parseStockSymbolsCSV("data\\companylist.csv")

  // TODO : Group with other examples
  def formatNumber(b: String) =
    T {
      b.replaceAll(""""""", "").toDouble
    }.toOption.filter {
      _ > 0
    }
}
