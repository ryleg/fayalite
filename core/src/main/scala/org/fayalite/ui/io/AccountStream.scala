/*
package org.fayalite.ui.io

import org.fayalite.util.SparkRef
import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD
import org.apache.spark.streaming.{Time, Seconds, StreamingContext}
import org.apache.spark.util.IntParam
import org.apache.spark.sql.SQLContext
import org.apache.spark.storage.StorageLevel

/**
 * I don't know what's going on here. We may never know.
 */

object AccountStream {
/*
    val sc = SparkReference.getSC
    val ssc = new StreamingContext(SparkReference.sparkConf, Seconds(2))

    // Create a socket stream on target ip:port and count the
    // words in input stream of \n delimited text (eg. generated by 'nc')
    // Note that no duplication in storage level only for running locally.
    // Replication necessary in distributed scenario for fault tolerance.
    val lines = ssc.socketTextStream(args(0), args(1).toInt, StorageLevel.MEMORY_AND_DISK_SER)
    val words = lines.flatMap(_.split(" "))

    // Convert RDDs of the words DStream to DataFrame and run SQL query
    words.foreachRDD((rdd: RDD[String], time: Time) => {
      // Get the singleton instance of SQLContext
      val sqlContext = SQLContextSingleton.getInstance(rdd.sparkContext)
      import sqlContext.implicits._

      // Convert RDD[String] to RDD[case class] to DataFrame
      val wordsDataFrame = rdd.map(w => Record(w)).toDF()

      // Register as table
      wordsDataFrame.registerTempTable("words")

      // Do word count on table using SQL and print it
      val wordCountsDataFrame =
        sqlContext.sql("select word, count(*) as total from words group by word")
      println(s"========= $time =========")
      wordCountsDataFrame.show()
    })

    ssc.start()
    ssc.awaitTermination()
  }*/
}


/** Case class for converting RDD to DataFrame */
case class Record(word: String)


/** Lazily instantiated singleton instance of SQLContext */
object SQLContextSingleton {

  @transient  private var instance: SQLContext = _

  def getInstance(sparkContext: SparkContext): SQLContext = {
    if (instance == null) {
      instance = new SQLContext(sparkContext)
    }
    instance
  }
}*/