package org.fayalite.agg.yahoo.finance

import fa._

/**
  * Pretty simple way to get stock simples
  * I just grabbed the company list from Yahoo Finance
  * main page from NASDAQ? I think or something.
  */
trait SymbolLookupRegistry {

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
    T {
      b.replaceAll(""""""", "").toDouble
    }.toOption.filter {
      _ > 0
    }
}
