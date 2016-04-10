package org.fayalite.agg

/**
  * Created by aa on 3/5/2016.
  */
object SeleniumDemo {

  case class Name(first: String, last: String)

  def getPermutations(n: Name) = {
    val f = n.first
    val l = n.last
    List(
      f,
      l,
      f(0).toString + l,
      f(0).toString + "." + l,
      f + l,
      f + "." + l,
      f + "." + l(0),
      f(0) + l(0)
    )
  }

}
