package org.fayalite.agg.yahoo.finance

/**
  * For handling JSON / CSV output response to
  * YQL requests
  */
object YahooRelatedSchema {

  // Response parsing

  case class BA(Bid: String, Ask: String)

  case class Res(quote: Array[BA])

  case class Qry(results: Res)

  case class Response(query: Qry)

  case class Price(ask: Double, bid: Double)

  case class SymbolPrice(symbol: String, price: Price)

  case class Observe(time: Int, symPrice: List[(String, Price)])

  case class Observe2(time: Int, symPrice: List[SymbolPrice])


  // Transform schema


  case class CompanyPrice(company: String, price: Double)

  case class Day(encoded: String)

  case class DayQuotes(day: Day, quotes: Seq[CompanyPrice])

  case class EncodedCompanyPrice(company: Int, price: Int)

  case class EncodedDayQuotes(day: Int, quotes: Seq[EncodedCompanyPrice])

}
