package org.fayalite.util.dsl

import java.io.FileWriter

import fa._

import scala.concurrent.duration._
import scala.concurrent._
import scala.util.{Failure, Success, Try}

/**
 * For when you're shocked that futures aren't gettable.
 */
trait CommonMonadExt {

  implicit val ec = scala.concurrent.ExecutionContext.Implicits.global

  implicit class SeqExt[A](s: Seq[A]) {

    def saveAsJson(f: String) = {
      s.foreach{
        q => f app q.json
      }
    }
    // def reindex

    def failMap[T](f: A => T) = s.flatMap{z => Try{f(z)}.toOption}

    def mapHead(f: (A => A)) = {
      s.headOption.map{f}.map{
        ho =>
          Seq(ho) ++ s
      }.getOrElse(Seq())
    }
  }

  implicit class SimpleArrayExt[A](a: Array[A]) {
    def _2 = {
      a match {
        case Array(x,y) => (x, y)
      }
    }
    def copyTo(b: Array[A]) = {
      System.arraycopy(a, 0, b, 0, a.length)
    }
  }


  def F[T](f: => T) = Future(f)
  def T[T](f: => T) = Try(f)
  def TPL[T](f: => T) = {
    val t = Try(f)
    t match {
      case Failure(e) => e.printStackTrace()
      case _ =>
    }
    t
  }
  def getFuture[T](fut: Future[T], timeout: Int = 10) = Await.result(fut, timeout.seconds)

  implicit class TryExt[T](some: Try[T]) {
    def printOpt = some match {
      case Failure(e) => e.printStackTrace(); None
      case Success(x) => Some(x)
    }
  }

    implicit class getAsFuture(some: Future[Any]) {
    def getAs[T] = Await.result(some, 15.seconds).asInstanceOf[T]
    def getAs[T](timeout: Int = 3) = Await.result(some, timeout.seconds).asInstanceOf[T]
  //  def getAsTry[T](timeout: Int = 3) = Try{Await.result(some, timeout.seconds).asInstanceOf[T]}

  }

  def randBytes(len: Int) = {
    val vb = Array.fill(len)(0.toByte)
    scala.util.Random.nextBytes(vb)
    vb
  }

  implicit class getAsFutureT[T](some: Future[T]) {
    def get = Await.result(some, 15.seconds).asInstanceOf[T]
    def getAsTry(timeout: Int = 3) = Try {
      Await.result(some, timeout.seconds).asInstanceOf[T]
    }
  }

  def gbk[T,V, Q](t: Traversable[(T, V)]) = t.groupBy(_._1).map {
    case (k,v) => k -> v.map{_._2}
  }

  implicit class KVTupExt[K,V](kv: (K,V)) {
    def tabDelim = kv._1.toString + "\t" + kv._2.toString
  }


  implicit class KVListExt[K, V](l: Seq[(K,V)]) {
    def gbk = l.groupBy{_._1}
      .map{case (x,y) => x -> y.map{_._2}}
    def prettyTSVString = {
      l.map{_.tabDelim}.mkString("\n")
    }
  }

  implicit def getFutureAsString(some: Future[Any]): String = some.getAs[String]

  implicit class StringExt(str: String) {
    def jsonLines[T](implicit manifest: Manifest[T]) = readLines(str).map{
      _.json[T]
    }
    def append(toWrite: String) = {
      println("appending to " + str + "\n" + toWrite)
      val fw = new FileWriter(str, true)
      try {
        fw.write(toWrite + "\n")
      }
      finally fw.close()
    }
    //def appendHome(path: String) = append(Common.ubuntuProjectHome + "/" + path)
  }



}
