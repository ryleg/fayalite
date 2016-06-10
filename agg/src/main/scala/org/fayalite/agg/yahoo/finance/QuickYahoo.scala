package org.fayalite.agg.yahoo.finance

import java.io.File

import dispatch.{Http, as, url}

object QuickYahoo {

  // For complete list see
  // http://stackoverflow.com/questions/5246843/how-to-get-a-complete-list-of-ticker-symbols-from-yahoo-finance

  // Stock research center
  // https://biz.yahoo.com/r/

  // NASDAQ company list CSV -- USE THIS!!! It's easy, but it contains
  // a few securities with strange stuff in them, use the filter func
  // to get rid of those since YQL doesn't like strange symbols in the
  // url encoded request.

  // http://www.nasdaq.com/screening/company-list.aspx

  def isValidCompanyStockSymbol(companySymbolNameRaw: String) = {
    val q = companySymbolNameRaw
    !q.contains("""^""") && !q.contains(""".""") &&
      q.length < 5 && !q.contains(" ")
  }

  def mkYQLURL(htmlEncodedSymbols: String) = "https://query.yahooapis.com/v1/public/yql?q=select%20Ask%2C%20Bid%20from%20yahoo.finance.quotes%20where%20symbol%20in%20" + htmlEncodedSymbols + "%0A%09%09&format=json&diagnostics=false&env=http%3A%2F%2Fdatatables.org%2Falltables.env"

  // This is dumb, use urlencoder somewhere else if you're not lazy.
  def formatSymbols(s: Seq[String]) = {
    "(" + s.map { q => "%22" + q + "%22" }.mkString("%2C") +
      ")"
  }

  def symbolsToQuery(sym: Seq[String]) = {
    mkYQLURL(formatSymbols(sym))
  }

  // Implement a readCSV function according to your dependencies if you
  // desire other CSV reader lib.
  def readCSV(cv: String) = {
    import com.github.tototoshi.csv.CSVReader
    val c = CSVReader.open(new File(cv))
    val a = c.all
    c.close()
    a
  }

  def parseStockSymbolsCSV(path: String) = {
    readCSV(path).tail.map {
      _ (0)
    }.filter {isValidCompanyStockSymbol }
  }

  case class ProxyDescr(
                         hostPort: String,
                         user: String,
                         pass: String
                       ) {
    def host = hostPort.split(":").head
    def port = hostPort.split(":").last.toInt
  }

  // HERE IS WHERE YOU MAKE REQUESTS! You could also use a native
  // Scala function for doing the request but dispatch is pretty small
  // dependency

  def getRequest(x: String)(implicit proxyD: Option[ProxyDescr] = None) = {
    val rq = url(x).GET
    proxyD.foreach { p =>
      rq.setProxyServer(// make implicit conv
        new com.ning.http.client.ProxyServer(p.host, p.port, p.user, p.pass)
      )
    }
    Http(rq OK as.String)
  }


}
