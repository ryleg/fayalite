/*
package org.fayalite.layer
import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD
import org.fayalite.Fayalite

import scala.concurrent.{Promise, Future}
import scala.reflect.ClassTag
import language.experimental.macros
import Fayalite._


class ASDF(
            ) extends Serializable
{
  val x = 1
  val p = Promise[RDD[Int]]()

}

object TestPromiseTrans {


  def main(args: Array[String]) {

    val sc = new SparkContext("local[*]", "asdf")

    val a = new ASDF()
    sc.makeRDD(1 to 100).map{
      i => i / a.x
    }.count()

  }
}
*/
