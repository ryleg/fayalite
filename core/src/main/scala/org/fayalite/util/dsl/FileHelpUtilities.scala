package org.fayalite.util.dsl

import java.io.File
import ammonite.ops.{Path, read, write}
import com.github.tototoshi.csv.CSVWriter
import fa._
import org.fayalite.layer.MessageParser
import Schema.CodeUpdate
import org.fayalite.util.JSON

// TODO : Revise

/**
  * For any junk methods required to interact with OS layer
  * Feel free to throw / edit / modify these, I wish
  * ammonite.ops was cross platform but it isn't and I don't
  * want to force usage of it everywhere, so you'll see a little
  * bit of many file access patterns here.
  */
trait FileHelpUtilities {

  /**
    * Pretty self explanatory, uses java API and works
    * cross platform.
 *
    * @param f: File to start from
    * @return : Everything except directory references
    */
  def recursiveListFiles(f: File): Array[File] = {
    val these = f.listFiles
    these ++ these.filter(_.isDirectory).flatMap(recursiveListFiles)
  }

  val commonIgnoreQualifiers = List("target", ".git",".DS_Store", ".idea")

  def fileQualifier(z: File, t: String): Boolean = {
    z.getAbsolutePath.split("\\\\").contains(t) ||
      z.getAbsolutePath.contains(".dll") ||
      z.isDirectory
  }

  def getCode: Array[CodeUpdate] = {
    recursiveListFiles(new File(".")).filterNot {
      z => commonIgnoreQualifiers.exists { t => fileQualifier(z, t)}
    }
  } map { q =>
    CodeUpdate(
      q.getCanonicalPath.replaceAll("""\""", "/"),
      scala.io.Source.fromFile(q).getLines().mkString("\n"))
  }


  def writeToCSVFile(f: String, cnt: String) = {
    val fnm = MessageParser.target.toString + "/" + f
    scala.tools.nsc.io.File(
      fnm)
      .writeAll(cnt)
  }

  def scalaFile(f: String) = scala.tools.nsc.io.File(f)

  def writeToFile(f: String, cnt: String) = {
    val fnm = f
    scala.tools.nsc.io.File(
      fnm)
      .writeAll(cnt)
  }

  def writeLines(f: String, cnt: Seq[String]) = {
    val fnm = f
    scala.tools.nsc.io.File(
      fnm)
      .writeAll(cnt.mkString("\n"))
  }

  def writeToFile(f: File, cnt: String) = {
    scala.tools.nsc.io.File(
      f)
      .writeAll(cnt)
  }

  def fromFile(f: File) = scala.io.Source.fromFile(f)

  def readFromFile(f: String) = scala.io.Source.fromFile(f).mkString

  def readLines(f: File) = scala.io.Source.fromFile(f).getLines()

  def readLines(f: String) = scala.io.Source.fromFile(f).getLines()


  type KVC = Map[String, Array[String]]

  implicit class FileAppendQuickener(f: String)  {
    def app(cnt: String) = scala.tools.nsc.io.File(
      f)
      .appendAll(cnt + "\n")
  }

  implicit class JavaFileHelp(f: java.io.File) {
    def read = scala.io.Source.fromFile(f)
    def readI = read.getLines()
    def readL = readI toList
  }


  def readCSV(cv: String): Seq[Seq[String]] = {
    import com.github.tototoshi.csv.CSVReader
    val c = CSVReader.open(new File(cv))
    val a = c.all
    c.close()
    a
  }
  def readCSVFromFile(cv: File): Seq[Seq[String]] = {
    import com.github.tototoshi.csv.CSVReader
    val c = CSVReader.open(cv)
    val a = c.all
    c.close()
    a
  }

  def writeCSV(output: String, cv: Seq[Seq[String]]) = {
    val f2 = new File(output)
    val writer = CSVWriter.open(f2)
    writer.writeAll(cv)
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

  def asLines(x: String) = scala.io.Source.fromFile(x)
    .getLines

  implicit class LocalIOHelp(p: Path) {
/*

    def lsr = {
      import ammonite.ops._
      ls.rec.!!(p)
        .filter {
          _.segments.last != ".DS_Store"
        }
    }.toIterable
*/

    def jsa(j: Any) = {
     // write.append(p, j.json + "\n")
    }


    def text = read(p).split("\n")
      .toList
/*

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
*/
    def jser(a: List[Any]) = {
   //   write(p, a.map{_.json}.mkString("\n"))
    }
  }

}
