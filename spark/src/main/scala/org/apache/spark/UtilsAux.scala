package org.apache.spark

import org.apache.spark.util.Utils

/**
  * Created by aa on 4/20/2016.
  */
object UtilsAux {

  def createTempDir(conf: SparkConf, namePrefix: String = "repl") = {
    val rootDir = conf
      .getOption("spark.repl.classdir")
      .getOrElse(Utils.getLocalDir(conf))

    val outputDir = Utils.createTempDir(
      root = rootDir,
      namePrefix = namePrefix
    )
    outputDir
  }

}
