package org.fayalite.util.dsl

import java.io.File

import ammonite.ops.{Path, read, write}
import com.github.tototoshi.csv.CSVWriter
import org.fayalite.layer.MessageParser
import org.fayalite.util.JSON

/**
  * Created by aa on 2/18/2016.
  */
trait FileHelpUtilities {

  def writeToCSVFile(f: String, cnt: String) = {
    val fnm = MessageParser.target.toString + "/" + f
    scala.tools.nsc.io.File(
      fnm)
      .writeAll(cnt)
  }
  def writeToFile(f: String, cnt: String) = {
    val fnm = f
    scala.tools.nsc.io.File(
      fnm)
      .writeAll(cnt)
  }
  def readFromFile(f: String) = scala.io.Source.fromFile(f).mkString

  def readLines(f: String) = scala.io.Source.fromFile(f).getLines()


  type KVC = Map[String, Array[String]]

  implicit class ap(f: String)  {
    def app(cnt: String) = scala.tools.nsc.io.File(
      f)
      .appendAll(cnt + "\n")
  }

  def loadCSV(cv: String) = {
    import com.github.tototoshi.csv.CSVReader
    val c = CSVReader.open(new File(cv))
    val a = c.all
    c.close()
    a
  }

  implicit class DirtyCSVOps[T](
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

  implicit class LocalIOHelp(p: Path) {

    def lsr = {
      import ammonite.ops._
      ls.rec.!!(p)
        .filter {
          _.segments.last != ".DS_Store"
        }
    }.toIterable

    def jsa(j: Any) = {
     // write.append(p, j.json + "\n")
    }


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
        import org.json4s._
        JSON.parse4s(q).extract[T]
      }
    }
    def jser(a: List[Any]) = {
   //   write(p, a.map{_.json}.mkString("\n"))
    }
  }

}
