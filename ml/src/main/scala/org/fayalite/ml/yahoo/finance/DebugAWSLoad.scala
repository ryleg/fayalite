package org.fayalite.ml.yahoo.finance


import java.util.GregorianCalendar

import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD

/**
  * Created by aa on 7/16/2016.
  */
object DebugAWSLoad {

  val sc: SparkContext = null


  def main(args: Array[String]): Unit = {

    sc.setLogLevel("INFO")

    val hadoopConf = sc.hadoopConfiguration
    val s3Secret =  System.getenv("AWS_SECRET_ACCESS_KEY")
    val s3Key =  System.getenv("AWS_ACCESS_KEY_ID")
    hadoopConf.set("fs.s3.impl", "org.apache.hadoop.fs.s3native.NativeS3FileSystem")
    hadoopConf.set("fs.s3.awsAccessKeyId", s3Key)
    hadoopConf.set("fs.s3.awsSecretAccessKey", s3Secret)

    import org.apache.spark.storage.StorageLevel

    val input = sc.wholeTextFiles("s3://fayalite/data/stock/historical/*").persist(StorageLevel.MEMORY_AND_DISK_SER)
    val mapZero = input.map {
      case (path, contents) =>
        import java.text.SimpleDateFormat
        val yahooDateFormat = "yyyy-MM-dd"
        val dateFormat = new SimpleDateFormat(yahooDateFormat)
        contents.split("\n").toList.tail.map {
          z =>
            val zz = z.split(",").toList
            val date = dateFormat.parse(zz.head)
            val dAdj = date.getTime / (1000 * 60 * 60 * 24)
            dAdj -> zz(1).toDouble
        }
    }.persist(StorageLevel.MEMORY_AND_DISK_SER)
    val mins = mapZero.flatMap {_.map {_._1}}.min
    val adj = mapZero.map{
      _.map{
        case (d, o) => d - mins -> o
      }
    }.persist(StorageLevel.MEMORY_AND_DISK_SER)

    val maxs = adj.map{_.map{_._1}.max}.max
    val eighty = (maxs*.8).toInt

    val train = adj.map{_.filter{_._1 < eighty}}.filter{_.nonEmpty}.persist(StorageLevel.MEMORY_AND_DISK_SER)
    train.count()

    val test = adj.map{
      z => val canBeTrainedOn = z.exists{_._1 < eighty}
        if (canBeTrainedOn) z.filter{_._1 > eighty} else List()
    }.filter{_.nonEmpty}.persist(StorageLevel.MEMORY_AND_DISK_SER)


    test.map{
      z =>
        z.map{ j => j
          //case
        }
    }

    test.count()

    def createImage(width: Int, height: Int) = {
      import java.awt.image.BufferedImage
      val image = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR)
      image
    }



    adj.flatMap{_.map{_._1}}.takeSample(false, 30).foreach{println}

      //.first())

    /*
bin/spark-shell --jars /home/ubuntu/hadoop-2.6.4/share/hadoop/tools/lib/aws-java-sdk-1.7.4.jar,/home/ubuntu/hadoop-2.6.4/share/hadoop/tools/lib/hadoop-aws-2.6.4.jar,/home/ubuntu/hadoop-2.6.4/share/hadoop/tools/lib/guava-11.0.2.jar
     */
  }

}
