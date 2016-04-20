package org.apache.spark

import org.apache.spark.util.Utils

/**
  * Created by aa on 4/20/2016.
  */
object UtilsAux {

  def createTempDir(
                     rootDir: String,
                     namePrefix: String = "repl"
                   ) = Utils.createTempDir(
      root = rootDir,
      namePrefix = namePrefix
    )

}
