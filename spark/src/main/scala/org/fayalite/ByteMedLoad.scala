package org.fayalite

import java.nio.ByteBuffer
import java.nio.file.{Files, Paths}

import org.apache.spark.sql.SQLContext
import org.apache.spark.{SparkConf, SparkContext}

/**
  * Created by aa on 6/19/2016.
  */
object ByteMedLoad {
/*
  val cnf = new SparkConf()
  cnf.setMaster("local[8]")
  cnf.setAppName("fayalite")
  cnf.set("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
  val sc = new SparkContext(cnf)

  def doStuff(stuff: Seq[Long]) = {

    val l = sc.makeRDD(stuff, 9).cache().setName("stuff")

    println(l.countByValue())

  }*/
  def main(args: Array[String]) {

/*  */
    println("args", args(0))
   // val t = scala.io.Source.fromFile(args(0)).map{_.toByte}.toArray
    val byteArray = Files.readAllBytes(Paths.get(args(0)))

  println(byteArray.size)

  val dv9o = byteArray.filter{_ % 9 == 0}
  println(dv9o.size)

   val lngs = byteArray.drop(6).grouped(8).map{
      q => ByteBuffer.wrap(q).getLong
    }.toArray

  println(lngs.size)
  val dv9 = lngs.filter{_ % 9 == 0}

  println(dv9.size)
   // doStuff(lngs)

   // println(byteArray.size % 8)

    //val b2 = Array.fill(byteArray.length){0.toByte}
/*
    def byte2Bools(b: Byte): Seq[Boolean] =
      0 to 7 map isBitSet(b)

    def isBitSet(byte: Byte)(bit: Int): Boolean =
      ((byte >> bit) & 1) == 1

    val bls = byteArray.flatMap{byte2Bools}
*/

 //   println(bls.size)
    //val ba = sc.parallelize(byteArray, 20).cache().setName("yo")
    // go up in curvature -- go down in curvature by rule s.t. any
    // array must follow rule ordering[T]



    //println(ba.countByValue.toSeq.sortBy(_._2))

    /*
    val s = byteArray.head
    val d = byteArray.tail.zipWithIndex.map{
      case (b,i) =>
        val p = byteArray(i)
        if (i % 10000 == 0) println(i, b-p)
        (b - p)

    }
    println(d.size)
*/
    /*
        val div9 = byteArray.map{q => (q % 9).toByte}

        println(byteArray.size, div9.size)*/
    /*
        val dydx = byteArray.tail.sliding(2).map{
          case Array(bx, by) =>
            by.toInt - bx.toInt
        }.toSeq

        println(dydx.size, byteArray.size)
    */



/*
    val sql = new SQLContext(sc)
    val bq = sql.createDataset(byteArray.toSeq).cache()
    import sql._
    println(bq.dtypes)
   // bq.agg()
*/


    /*    println(byteArray.size)
        val sum = byteArray.sum
        val sumD9 = sum % 9
        println("sum", sum, "div9", sumD9)
        val frs = byteArray.head
        println(frs)
        val frsNonZero = byteArray.collectFirst{case x if x != 0 => x}
        val frsNons = byteArray.filter{_ != 0}.take(100).toSeq
        println(frsNons)
        println(frsNonZero)
        byteArray.map{x => x -> 1L}
        */
    // val byteArray2 = byteArray
  //  Files.write(Paths.get(args(0).stripSuffix(".mp4") + "_out.mp4"), byteArray2)

  }

}
