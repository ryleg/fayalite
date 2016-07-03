package org.fayalite.fs

import java.io.File

import scala.collection.JavaConversions._

object FSCodePull {

  def main(args: Array[String]) {
    {
      getTopLevelFiles
    }
  }

  val commonExcludes = Seq(".git", ".idea")

  /**
    * Quick and dirty way to get top level files/folders
    * in the current project directory.
    *
    * This assumes that you are running locally and
    * (probably) executing this from IntelliJ considering
    * we're just grabbing everything in "."
    *
    * @return All top level files/folders
    */
  def getTopLevelFiles: Seq[File] = {
    new File(".").listFiles().toSeq.filterNot(
      z => commonExcludes.contains{z.getName}).filter{_.isDirectory}
  }
}
