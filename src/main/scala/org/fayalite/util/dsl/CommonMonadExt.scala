package org.fayalite.util.dsl

import java.io.FileWriter

import akka.util.Timeout
import org.fayalite.util.JSON
import scala.collection.TraversableLike
import scala.concurrent.duration._
import scala.concurrent._
import scala.reflect.ClassTag
import scala.util.{Success, Failure, Try}

/**
 * For when you're shocked that futures aren't gettable.
 */
trait CommonMonadExt {


  implicit val ec = scala.concurrent.ExecutionContext.Implicits.global

  def F[T](f: => T) = Future(f)
  def T[T](f: => T) = Try(f)
  def TPL[T](f: => T) = {
    val t = Try(f)
    t match {
      case Failure(e) => e.printStackTrace()
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

  implicit class getAsFutureT[T](some: Future[T]) {
    def get = Await.result(some, 15.seconds).asInstanceOf[T]
    def getAsTry(timeout: Int = 3) = Try {
      Await.result(some, timeout.seconds).asInstanceOf[T]
    }
  }

  def gbk[T,V, Q](t: Traversable[(T, V)]) = t.groupBy(_._1).map {
    case (k,v) => k -> v.map{_._2}
  }

  implicit class KVListExt[K, V](l: List[(K,V)]) {
    def gbk = l.groupBy{_._1}
      .map{case (x,y) => x -> y.map{_._2}}
  }

  implicit def getFutureAsString(some: Future[Any]): String = some.getAs[String]

  implicit class StringExt(str: String) {
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
