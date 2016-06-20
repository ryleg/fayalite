package org.fayalite.agg.yahoo.finance

import java.io.File

import com.github.tototoshi.csv.CSVReader

import scala.collection.mutable
import scala.util.Try
import fa._


/**
  * Quick converter for taking save output and building
  * a per-day per-file index.
  */
trait YahooTestUtils {

  val yahooSave : java.io.File
  val gbtime : java.io.File = null

  def convertHistoricalCSVsToGroupByTimeTempIndex = {
    val storM = mutable.HashMap[String, scala.collection.mutable.HashMap[String, Float]]()
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
    storM
    //storM.map{_._2.size}.toSeq.sorted.reverse.slice(0,100).foreach{println}
  }


  def getGroupByTimeIndexed = {
    val gbf = gbtime.listFiles()
    val r2 = gbf.map {
      q =>
        val f = q.getName
        val qts = readLines(q).map {
          q =>
            val a = q.split("\t")
            a(0) -> a(1).toDouble
        }.toMap
        f -> qts
    }
    r2
  }


}
