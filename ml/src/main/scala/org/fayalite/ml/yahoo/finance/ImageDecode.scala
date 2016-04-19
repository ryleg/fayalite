package org.fayalite.ml.yahoo.finance


import java.nio.ByteBuffer

import com.twitter.algebird.MinHasher32
import fa._

/**
  * Created by aa on 4/18/2016.
  */
object ImageDecode {

  implicit def bytesToLong(bytes: Array[Byte]): Long = {
    val buffer = ByteBuffer.allocate(8)
    buffer.put(bytes)
    buffer.flip();//need flip
    buffer.getLong()
  }

  implicit def longToBytes(l: Long): Array[Byte] = {
    val buffer = ByteBuffer.allocate(8)
    buffer.putLong(0, l)
    buffer.array()
  }



  def main(args: Array[String]): Unit = {
    val img = readImg(".tesyahoo.png")
    val data = img.getAllData



  }
  def minHash(): Unit = {
    import com.twitter.algebird
    val img = readImg(".tesyahoo.png")
    val data = img.getAllData // just for demonstrating conversion
    // you can also just get the bytes from the file directly.

    println("data length" + data.length)
    val h = img.getHeight
    println("h w " + h + " " + img.getWidth)

    val size: Int = 4 * img.getWidth
    val m = data.grouped(size).toArray

    val sizeP = m.length

    println(m.length)

    import algebird.MinHasher

    val numNewCol = 10

    val img2 = createImage(3000, 3000).black
    m.zipWithIndex.foreach{
      case (r, i) =>
        val lv = r.grouped(8).map{bytesToLong}.toSeq
        val numHashes = lv.size
        val mh = new MinHasher32(numHashes, numNewCol)
        val h = lv.map{mh.init}.reduce{mh.plus}
        val b = mh.buckets(h)
        //  val bp = b.flatMap{longToBytes}
        b.zipWithIndex.foreach{
          case (bb, bi) =>
            img2.setRGB(i, bi, bb.toInt)
        }
        println(i + " " + (i.toDouble / m.size)*100 + "%" + b.size + " " + r.size )

    }

    img2.save(".yahoobuckets.png")

  }
}
