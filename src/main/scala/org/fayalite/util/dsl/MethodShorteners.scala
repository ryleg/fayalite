package org.fayalite.util.dsl

import scala.collection.TraversableLike
import scala.collection.generic.FilterMonadic

trait MethodShorteners {

  /*
  implicit class SeqFix[T](s: Seq[T]) {
    def m[Q](f: T => Q) = s map f
  }
*/

  implicit class TLAbbrv[+A, +Repr](t: TraversableLike[A, Repr]) {
    def fe[U](f: scala.Function1[A, U]): scala.Unit = t.foreach(f)

    def ie: scala.Boolean = t isEmpty

    def m[ B, That](f: scala.Function1[A, B])(implicit bf: scala.collection.generic.CanBuildFrom[Repr, B, That]): That = t map f

    def fm[B, That](f: scala.Function1[A, scala.collection.GenTraversableOnce[B]])(implicit bf: scala.collection.generic.CanBuildFrom[Repr, B, That]): That = t flatMap f

    def ft(p: scala.Function1[A, scala.Boolean]): Repr = t filter p

    def fn(p: scala.Function1[A, scala.Boolean]): Repr = t filterNot p

    def c[B, That](pf: scala.PartialFunction[A, B])(implicit bf: scala.collection.generic.CanBuildFrom[Repr, B, That]): That = t collect pf

    def p(p: scala.Function1[A, scala.Boolean]): scala.Tuple2[Repr, Repr] = t partition p

    def g[K](f: scala.Function1[A, K]): scala.collection.immutable.Map[K, Repr] = t groupBy f

    def fl(p: scala.Function1[A, scala.Boolean]): scala.Boolean = t.forall(p)

    def e(p: scala.Function1[A, scala.Boolean]): scala.Boolean = t exists p

    def fi(p: scala.Function1[A, scala.Boolean]): scala.Option[A] = t find p

    def s[B >: A, That](z: B)(op: scala.Function2[B, B, B])(implicit cbf: scala.collection.generic.CanBuildFrom[Repr, B, That]): That = t.scan(z)(op)

    def sl[B, That](z: B)(op: scala.Function2[B, A, B])(implicit bf: scala.collection.generic.CanBuildFrom[Repr, B, That]): That = t.scanLeft(z)(op)

    def sr[B, That](z: B)(op: scala.Function2[A, B, B])(implicit bf: scala.collection.generic.CanBuildFrom[Repr, B, That]): That = t.scanRight(z)(op)

    def h: A = t head

    def ho: scala.Option[A] = t headOption

    def ta: Repr = t tail

    def l: A = t last

    def lo: scala.Option[A] = t lastOption

    def i: Repr = t init

    def t(n: scala.Int): Repr = t take n

    def d(n: scala.Int): Repr = t drop n

    def sl(from: scala.Int, until: scala.Int): Repr = t.slice(from, until)

    def tw(p: scala.Function1[A, scala.Boolean]): Repr = t takeWhile p

    def dw(p: scala.Function1[A, scala.Boolean]): Repr = t dropWhile p

    type f1[a,b] = scala.Function1[a,b]

    def sp(p: scala.Function1[A, scala.Boolean]): scala.Tuple2[Repr, Repr] = t span p

    def spa(n: scala.Int): scala.Tuple2[Repr, Repr] = t splitAt n

    def tas: scala.collection.Iterator[Repr] = t tails

    def is: scala.collection.Iterator[Repr] = t inits


    type ite[q] = scala.collection.Iterator[q]

    def ca[B >: A](xs: scala.Array[B], start: scala.Int, len: scala.Int): scala.Unit = t copyToArray(xs, start, len)

    def tt: scala.collection.Traversable[A] = t toTraversable

    def ti: scala.collection.Iterator[A] = t toIterator

    def ts: scala.Stream[A] = t toStream

    def str: scala.Predef.String = t toString()

    def strp: scala.Predef.String = t stringPrefix

    def v: scala.AnyRef with scala.collection.TraversableView[A, Repr] = t view

    def v(from: scala.Int, until: scala.Int): scala.collection.TraversableView[A, Repr] = t.view(from, until)

    def w(p: scala.Function1[A, scala.Boolean]): scala.collection.generic.FilterMonadic[A, Repr] = t withFilter (p)
  }

}