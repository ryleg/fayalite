package org.fayalite.layer


import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD
import org.fayalite.Fayalite

import scala.concurrent.{Promise, Future}
import scala.reflect.ClassTag
import language.experimental.macros
import Fayalite._
import experimental.macros.info.DebugMacros
/**
 * Copyright 2015, Radius Intelligence, Inc.
 * All Rights Reserved
 */
class FRDD[T: ClassTag](inputRDD : Future[RDD[T]]) extends Serializable {

  def map[U: ClassTag](f: T => U): FRDD[U] = {
    new FRDD(inputRDD.map(_.map(f)))
  }

  def map[U: ClassTag](f: Future[T => U]) = {
    val q = f.map { func =>
        inputRDD.map(_.map(
        func
      ))
    }
    new FRDD(q.flatMap{x => x})
  }

  def flatten = {
    inputRDD.map{
      actualRDD =>
        actualRDD.map{
          a => Future{a}
        }
    }
  }

  // def map FAct[K]


  // this returns a FAct[Long]
  def count() = {
    inputRDD.map(rdd => {println("taking count"); rdd.count()})
  }

  def take(n : Int) = {
    val promise = Promise[Array[T]]()

    inputRDD.map(rdd => promise.success(rdd.take(n)))
    promise.future
  }
}

object FRDD{


  def main(args: Array[String]) {
     // val yo = "yoooo"

      val actionFuture = 5
      val f = (i : Int) => i + actionFuture

      import DebugMacros._
     // printparam(f)
  //    println(new ToStringAdder1().add( (i: Int) => i / actionFuture))
/*
class ToStringAdder1 extends ToStringAdder {
        def add(param: Any, toStringBasedOnAST: String): Any = s"param: $param \ntoStringBasedOnAST: $toStringBasedOnAST"
      }
 */
    //  DebugMacros.getName(yo).p
  }
    def test() = {
    val sc = new SparkContext("local[*]", "???")


    val rddInstantiation = Promise[RDD[Int]]()

    val initialRDD = rddInstantiation.future

    val nums = new FRDD(initialRDD).map(_.toDouble)

    val a = nums.count()

    val f = a.map(res => (n: Double) => n/res)

    val div = nums.map(f)

   // val div = a.map(res => nums.map(_ / res))

    println("defined")
  //  div.onComplete(_.get.take(5).onComplete(_.get.foreach(println)))
    println("complete")

    div.count().onComplete(_ => println("whoa"))

    rddInstantiation.success(sc.parallelize(1 to 50))


    while(true){
      Thread.sleep(1000)
      println("tick")
    }
  }
}