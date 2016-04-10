package org.fayalite.agg.yahoo.finance

/**
  * Quick converter for taking save output and building
  * a per-day per-file index.
  */
trait YahooTestUtils {

  val yahooSave : java.io.File
  val gbtime : java.io.File

  def convertHistoricalCSVsToGroupByTimeTempIndex = {
    val storM = mutable.HashMap[String, mutable.HashMap[String, Float]]()
    yahooSave.listFiles().par.foreach{f =>
      val nm = f.getName
      println("Reading csv " + f.getCanonicalPath)
      CSVReader.open(f).toStream().tail
        .withFilter{_.nonEmpty}
        .withFilter{_(0) != ""}
        .foreach{
          q =>
            val time = q.head
            Try {
              val open = q(1).toFloat
              synchronized {
                if (storM.contains(time)) {
                  storM(time)(nm) = open
                } else {
                  storM(time) = mutable.HashMap(nm -> open)
                }
              }
            }
        }
    }
    storM.par.foreach{
      case (datetime, quotes) =>
        val f = new File(gbtime, datetime.replaceAll("\\-", "_"))
        writeToFile(f, quotes.toSeq.sortBy{_._1}.prettyTSVString)
    }

    //storM.map{_._2.size}.toSeq.sorted.reverse.slice(0,100).foreach{println}
  }


}
