package org.fayalite.repl.impl

import akka.util.Timeout
import scala.concurrent.duration._
import scala.concurrent._


trait FutureExt {

  implicit val ec = scala.concurrent.ExecutionContext.Implicits.global

  def getFuture[T](fut: Future[T], timeout: Int = 10) = Await.result(fut, timeout.seconds)

  implicit class getAsFuture(some: Future[Any]) {
    def getAs[T] = Await.result(some, 15.seconds).asInstanceOf[T]
    def getAs[T](timeout: Int = 3) = Await.result(some, timeout.seconds).asInstanceOf[T]

  }

  implicit class getAsFutureT[T](some: Future[T]) {
    def get = Await.result(some, 15.seconds).asInstanceOf[T]
  }


  implicit def getFutureAsString(some: Future[Any]): String = some.getAs[String]

}
