package org.fayalite.agg

/**
  * Created by aa on 3/1/2016.
  */
object SelRunner {

  class Quick extends SelExample("http://www.google.com")

  def main(args: Array[String]) {
    new Quick()
  }
}
