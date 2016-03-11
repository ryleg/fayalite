package org.fayalite.agg

import java.io.File

import fa._
import org.fayalite.util.JSON
import rx._

/**
  * Created by aa on 3/5/2016.
  */
object Store {

}

class JSONFileBack[T](fileBacking: String = ".jsonback")(
                     implicit manifest: Manifest[T]
)
{
  def getStore: T = readFromFile(fileBacking).json[T]
  val store : Var[T] = Var(getStore)
  def writeStore() = writeToFile(fileBacking, store().json)
  import rx.ops._
  val storeChange = store.foreach{_ => writeStore()}
}


class FileBackedKVStore[T](dirBack: String = ".hidden")(
  implicit manifest: Manifest[T]
)
{
  val dirbk = new File(dirBack)
  if (!dirbk.isDirectory && !dirbk.isFile) dirbk.mkdir()
  def storeKV(kv: Seq[(String, T)]) = {
    kv.foreach {
      case (k, v) =>
        val f = new File(dirbk, "k")
        writeToFile(f.getCanonicalPath, v.json)
    }
  }
  def readKV = {
    dirbk.listFiles().toList.map { q =>
      q.getName -> fromFile(q).mkString.json[T]
    }
  }
}

trait KVStore {

  val dirBack: String = ".hidden"
  val dirbk = new File(dirBack)
  if (!dirbk.isDirectory && !dirbk.isFile) dirbk.mkdir()

  def store[T](relFnm: String, contents: T)
       //       (implicit manifest: Manifest[T])
  = {
    val f = new File(dirbk, relFnm)
    writeToFile(f.getCanonicalPath, contents.json)
  }
  def read[T](fnm: String)(
    implicit manifest: Manifest[T]
  ) = {
    val f = new File(dirbk, fnm)
    fromFile(f).mkString.json[T]
  }
}
