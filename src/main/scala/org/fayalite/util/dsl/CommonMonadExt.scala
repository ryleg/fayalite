package org.fayalite.util.dsl

import java.io.FileWriter

import akka.util.Timeout
import org.fayalite.util.{SparkReference, JSON, Common}
import scala.concurrent.duration._
import scala.concurrent._
import scala.reflect.ClassTag
import scala.util.{Success, Failure, Try}


trait CommonMonadExt {

  implicit val ec = scala.concurrent.ExecutionContext.Implicits.global

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
    def appendHome(path: String) = append(Common.home + "/" + path)
  }



}