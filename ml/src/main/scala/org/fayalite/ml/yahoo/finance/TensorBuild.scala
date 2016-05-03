package org.fayalite.ml.yahoo.finance

import java.io.File
import java.text.SimpleDateFormat
import java.util.Date

import fa._



/**
  * Yahoo historical CSVs per company
  */
object YahooParse {

  val hidden = new File(".hidden")
  val yahooCompanyCSVs = new File(hidden, "yahoo")
  val dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd")

  /**
    * Fixes parsing errors due to missing rows or an
    * invalid header being processed
    * @param row : Yahoo historical CSV row
    * @return : Whether to discard
    */
  def rowExclusionCritera(row: Seq[String]) = {
    if (row.nonEmpty) {
      row.head != "" && row.head != "Date"
    } else false
  }

  case class HistoricalObservation(
                                  date: Date,
                                  open: Double,
                                  high: Double,
                                  low: Double,
                                  close: Double,
                                  volume: Int,
                                  adjustedClose: Double
                                  )

  def parseRow(row: Seq[String]) = {
    val t = dateTimeFormat.parse(row.head)
    val r = row.tail
    HistoricalObservation(
      t,
      r(0).toDouble, r(1).toDouble, r(2).toDouble,
      r(3).toDouble, r(4).toInt, r(5).toDouble
    )
  }

  case class CompanyHistory(company: String, history: Seq[HistoricalObservation])

  /**
    * Return image of stock data organized by company
    * and sequence of date observations (data is missing)
    * @return Local collection of historical observations by company
    */
  def getHistory = {
   yahooCompanyCSVs.listFiles.map{
     z =>
       val csv = readCSV(z.getAbsolutePath)
       val parsed = csv
         .tail
         .filter{rowExclusionCritera}
         .map{parseRow}
       CompanyHistory(z.getName, parsed)
   }
  }

}

object TensorBuild {

  import YahooParse._

  val numSigFigPerDouble = 1e6

  implicit class SeqOpTens[T](s: Seq[T]) {
    def index[B](f: T => B)(implicit ordr: Ordering[B]) = {
      s.map{f}.distinct.sorted.zipWithIndex.toMap
    }
    def flatIndex[B](f: T => Seq[B])(implicit ordr: Ordering[B]) = {
      s.flatMap{f}.distinct.sorted.zipWithIndex.toMap
    }
  }

  case class TimeDeltaActual(
                             seqDiff: Seq[Int],
                             deltaDays: Short,
                             deltaVolume: Int
                           )

  case class TimeDeltaPrepForm(
                                observesToTrackDeltaOn: Seq[Int],
                                volume: Int,
                                date: Date
                      ) {
    def compare(other: TimeDeltaPrepForm) = {
      val dx = observesToTrackDeltaOn
        .zip(other.observesToTrackDeltaOn)
        .map{
        case (x,y) => x - y
      }
      val dd = (date.getTime - other.date.getTime).toShort
      val dv = volume - other.volume
      TimeDeltaActual(dx, dd, dv)
    }
  }

  case class DeltaEncodedRowObservation(
                                         encodedCompany: Short,
                                         originalRow: TimeDeltaPrepForm,
                                         offsetDeltas: Seq[TimeDeltaActual]
  )

  case class PreDeltaFullEncodingCompanyObservation(
                                                   decRowObs: DeltaEncodedRowObservation
                                                   )

  case class CompanyTradeWindows(companySymbol: String )

  def buildTradeWindows(history: Seq[YahooParse.CompanyHistory]) = {

    history.map{
      q =>
        q.company

        q.history.map{
          x => x
        }


    }


  }

  def recodeObservations() = {

  }


  def main(args: Array[String]) {

    val h = YahooParse.getHistory.toSeq

    val tradeWindows = buildTradeWindows(h)


    val cIdx = h.index{_.company}.mv{_.toShort}.toMap

    val deltaEncoded = h.map{
      c =>

        val cs = cIdx(c.company)

        val seqOb = c.history.map{
          ho =>
            val dbls = Seq(ho.open, ho.close, ho.high, ho.low, ho.adjustedClose)
            val intEncFeat = dbls.map{z => (z*numSigFigPerDouble).toInt}
            TimeDeltaPrepForm(intEncFeat, ho.volume, ho.date)
        }

        // Here we are only keeping track of changes from the
        // original row observation to reduce redundancy
        val recodeDeltaNeighbor = seqOb.tail.zipWithIndex.map{
          case (m, i) =>
            val neighborPrevious = seqOb(i)
            val deltaNeighbor = m.compare(neighborPrevious)
            deltaNeighbor
        }

        val originalObserve = seqOb.head

        DeltaEncodedRowObservation(cs, originalObserve, recodeDeltaNeighbor)
    }

    val offsetIdx = deltaEncoded.flatIndex{q =>
      q.offsetDeltas.flatMap{_.seqDiff}
    }

/*
    deltaEncoded.map{
      d =>
        d.offsetDeltas.map{_ offsetIdx.get}

    }
*/






  }

/*  case class TimeIdentifiedRow(date: Date, unparsedDat: Seq[Int])

  val h = new File(".hidden")

  val hqp = new File(h, "yahoo2")
  val hqp3 = new File(h, "yahoo3")
  hqp3.mkdir()

  def main(args: Array[String]): Unit = {

    val j = hqp.listFiles().map{
      z =>
        val jq = readLines(z).toSeq.map{_.split("\t").toSeq.map{_.toInt}}
        jq
    }

    val all = j.flatten.flatten

    val gb = all.groupBy{
      z =>
        z
    }.map{case (x,y) => x -> y.length}
      .toSeq.sortBy{_._2}.reverse.map{_._1}.zipWithIndex.toMap
 // need to eliminate -1 parities later
    val gbp = j.map{
      _.map{
        _.map{
          i =>
            gb.get(i).get
        }
      }
    }

    gbp.zip(hqp.listFiles()).foreach{
      case (c, f) =>
        val newlns = c.map{
          _.mkString(",")
        }
        writeLines(new File(hqp3, f.getName).getCanonicalPath, newlns)
    }


  }
  def build = {
      val dt = new SimpleDateFormat("yyyy-MM-dd")
    val hp = new File(h, "yahoo")
//    val nt = hqp.listFiles().toSet
    hqp.mkdir()
    val faa = hp.listFiles().map {
     f =>
       val cmp = f.getName
     //  println("Trying " + cmp)
       val ztz = T {
         val hd = readCSV(f.getAbsolutePath)
         val stuff = hd.tail.filter { z => z.nonEmpty }.filter {
           j => j(0) != "" && j(0) != "Date"
         }.map {
           q =>
             //  println(cmp, q)
             val at = T {
               dt.parse(q(0))
             }
             if (at.isFailure) {
               println(cmp, q, hd)
             }
             val tp = at.get
             val strangeCase = q(q.length-2)
             TimeIdentifiedRow(tp, q.tail.map {
               ss =>
                 if (ss == strangeCase) ss.toInt else
                 (ss.toDouble * 1e6).toInt
             })
         }

         val firstRow = stuff.head
         val t0 = (firstRow.date.getTime / (1e5).toLong).toString
         val r0 = firstRow.unparsedDat.map {
           _.toString
         }
         val zro = Seq(t0) ++ r0
         val newstuff = stuff.tail.zipWithIndex.map {
           case (rr, ridx) =>
             val neighbor = stuff(ridx)
             val thisDate = rr.date.getTime
             val previousDate = neighbor.date.getTime
             val dtz = -1 * (thisDate - previousDate) / 1e5.toLong
         //    println(thisDate, previousDate, dtz)
             val dvs = rr.unparsedDat.zip(neighbor.unparsedDat).map { case (x, y) =>

          val nez =  x - y
      //  println(x,y,nez)
         nez
         }
             Seq(dtz.toString) ++ dvs.map {
               _.toString
             }
         }.toSeq
         val outp = new File(hqp, cmp).getCanonicalPath
         val cnt = Seq(zro) ++ newstuff
         writeLines(outp, cnt.map {
           _.mkString("\t")
         })
         println("wrote " + cmp)
         true
       }

       if (ztz.isFailure) println(
         "Failed on compnay " + cmp
       )
       true
   }
  }
  */

}
