package org.fayalite.util.dsl

/**
  * Created by aa on 7/2/2016.
  */
trait CollectLike {

  implicit class SeqHelpSimpleMethod[T](s: Seq[T]) {
    def allContainsNot(f: T => Boolean) = {
    //  s.forall()
    }
  }
}
