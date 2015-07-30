import java.io.File
import java.text.SimpleDateFormat
import java.util.Calendar

import ammonite.ops.{read, write, Path}
import com.github.tototoshi.csv.CSVWriter
import org.fayalite.util.JSON
import org.fayalite.{util, Fayalite}


import org.fayalite.util.dsl._
import org.json4s.Extraction
import org.jsoup.Jsoup
import org.jsoup.nodes.{Document, Element}

import scala.collection.{TraversableLike, JavaConversions}
import scala.reflect.ClassTag
import scala.util.Random


/**
 * This is for quick declarations of implicits that
 * you might need to shuffle down into a trait later
 *
 * Use this to declare common lib-wide extensions
 * that are probably useful but you don't really know where to
 * put. When they hit a critical mass organize them or port
 * them over to some other trait to prevent clutter in here.
 */
package object fa  extends AkkaExt
with CommonMonadExt
with ScaryExt
with VeryCommon
with MethodShorteners
{



  implicit class Cleanliness(s: String) {


  }
/*
  implicit class TLEXT[A, B, +Repr](t: TraversableLike[(A,B), Repr]) {

    def gbk = t.groupBy{_._1}.map{
      case (x,y) => x -> y.
    }

  }

  implicit class TLEXT2[+A, +B, +Repr](
                                        t: TraversableLike[(A,B), Repr]) {

    def gbk = t.groupBy{case (x,y) => x}.map{
      case (x,y) => x -> y.map
    }

  }*/

  def rport = Random.nextInt(50000) + 1500
  def ct = {
    val today = Calendar.getInstance().getTime()
    val minuteFormat = new SimpleDateFormat("YYYY_MM_dd_hh_mm_ss")
    minuteFormat.format(today)}



  implicit class csvOps[T](
                            asmap: List[Map[String, String]]
                            ) {
    def save(output: String) = {
      val f2 = new File(output)
      val writer = CSVWriter.open(f2)
      val htrans = asmap.head.keys.toList.sorted.zipWithIndex.toMap
      writer.writeRow(
        htrans.toList.sortBy {
          _._2
        }.map {
          _._1
        }
      )
      asmap.foreach {
        a =>
          val rrow = a.toList.sortBy { q => htrans.get(q._1).get }.map {
            _._2
          }
          writer.writeRow(rrow)
      }
    }
  }

  implicit class localIO(p: Path) {

    def lsr = {
      import ammonite.ops._
      ls.rec.!!(p)
        .filter {
        _.segments.last != ".DS_Store"
      }
    }.toIterable


    def text = read(p).split("\n")
      .toList

    def textRec = lsr.filterNot{_.isDir}.flatMap {
      jj =>
        println ("textRec reading " + jj)
        read(jj).split("\n")
      .toList }

    def jsonRec[T]()(implicit m: Manifest[T]) = {
      textRec.map { q =>
        import JSON._
        JSON.parse4s(q).extract[T]
      }
    }
    def jser(a: List[Any]) = {
      write(p, a.map{_.json}.mkString("\n"))
    }
  }
  import JavaConversions._

  implicit class selOps(e: Element) {
    def sel(sll: String) = e.select(sll).iterator.toList
  }

  implicit class DocOps(d: Document) {
    def sel(sll: String) = d.select(sll).iterator.toList
  }

  implicit class strHTML(s: String) {
    val doc = Jsoup.parse(s)
    def selText(elem: Element, sll: String) = elem.sel(sll)
      .map {_.text}
    def json[T]()(implicit m: Manifest[T]) = {
      import JSON._
      JSON.parse4s(s).extract[T]
    }
  }

  implicit class CaseJsonCSVCol(jl: List[Any]) {
    def csv(path: String) = {
      jl.map{_.toKV}.save(path)
    }
  }

  implicit class CaseJsonCSV(jl: Any) {
    def toKV = {
      import JSON.formats
      val dc = Extraction.decompose(jl)
      val mp = dc.extract[Map[String, String]]
      mp
    }
  }

}
