package org.fayalite.util

import java.io.File

import dispatch.{Req, RequestHandlerTupleBuilder}

/**
 * Created by aa on 11/27/2015.
 */
object Yahoo {

  implicit class ap(f: String)  {
   def app(cnt: String) = scala.tools.nsc.io.File(
      f)
      .appendAll(cnt + "\n")
  }

  def main(args: Array[String]) {

    import dispatch._

    val r = url("https://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20yahoo.finance.quotes%20where%20symbol%20in%20(%22YHOO%22%2C%22AAPL%22%2C%22GOOG%22%2C%22MSFT%22)%0A%09%09&diagnostics=true&env=http%3A%2F%2Fdatatables.org%2Falltables.env")

    val t: Req = r.GET

    while ( true) {
      import dispatch._, Defaults._
      val svc = r
      val country = Http(svc OK as.String)
    country.onComplete{
      e =>
        import fa._
        val f = "fnc"
        if (e.isSuccess) f app e.get
    }
      Thread.sleep(30*1000)
    }

  }
}
