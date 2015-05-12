package org.fayalite.ui.app

import scala.collection.GenTraversableOnce
import scala.collection.generic.CanBuildFrom
import scala.collection.immutable.List

object App {

  //  List(1,3,3).map
  //  @noinline // TODO - fix optimizer bug that requires noinline (see SI-8334)
 /* implicit class TraverseExt[A](t: Traversable[A]) {
    def m[B, That](f: A => B)(implicit bf: CanBuildFrom[Traversable[A], B, That]): That = {
    t.map(f)
    }
    def t[B, That](f: A => GenTraversableOnce[B])(
      implicit bf: CanBuildFrom[Traversable[A], B, That]): That = t.flatMap(f)
    def f(p: A => Boolean): Traversable[A] = t filter p
    def r[A1 >: A](op: (A1, A1) => A1): A1 = t.reduce(op)
    def p(pa: A => Boolean): (Traversable[A], Traversable[A]) = t.partition(pa)
    def g[K](f: A => K): Map[K, Traversable[A]] = t.groupBy(f)
    def e[U](f: A =>  U): Unit = t foreach f
    def a(p: A => Boolean): Boolean = t forall p
    def x(p: A => Boolean): Boolean = t exists p
    def c(p: A => Boolean): Int = t count p
    def d(p: A => Boolean): Option[A] = t find p
  }*/
/*
 override def map[B, That](f: A => B)(implicit bf: CanBuildFrom[Traversable[A], B, That]): That
  def flatMap[B, That](f: A => GenTraversableOnce[B])(implicit bf: CanBuildFrom[Traversable[A], B, That]): That
  def filter(p: A => Boolean): Traversable[A]
  def remove(p: A => Boolean): Traversable[A]
  def partition(p: A => Boolean): (Traversable[A], Traversable[A])
  def groupBy[K](f: A => K): Map[K, Traversable[A]]
  def foreach[U](f: A =>  U): Unit
  def forall(p: A => Boolean): Boolean
  def exists(p: A => Boolean): Boolean
  def count(p: A => Boolean): Int
  def find(p: A => Boolean): Option[A]
  def foldLeft[B](z: B)(op: (B, A) => B): B
  def /: [B](z: B)(op: (B, A) => B): B
  def foldRight[B](z: B)(op: (A, B) => B): B
  def :\ [B](z: B)(op: (A, B) => B): B
  def reduceLeft[B >: A](op: (B, A) => B): B
  def reduceLeftOption[B >: A](op: (B, A) => B): Option[B]
  def reduceRight[B >: A](op: (A, B) => B): B
  def reduceRightOption[B >: A](op: (A, B) => B): Option[B]
  def head: A
  def headOption: Option[A]
  def tail: Traversable[A]
  def last: A
  def lastOption: Option[A]
  def init: Traversable[A]
  def take(n: Int): Traversable[A]
  def drop(n: Int): Traversable[A]
  def slice(from: Int, until: Int): Traversable[A]
  def takeWhile(p: A => Boolean): Traversable[A]
  def dropWhile(p: A => Boolean): Traversable[A]
  def span(p: A => Boolean): (Traversable[A], Traversable[A])
  def splitAt(n: Int): (Traversable[A], Traversable[A])
  def copyToBuffer[B >: A](dest: Buffer[B])
  def copyToArray[B >: A](xs: Array[B], start: Int, len: Int)
  def copyToArray[B >: A](xs: Array[B], start: Int)
  def toArray[B >: A : ClassTag]: Array[B]
  def toList: List[A]
  def toIterable: Iterable[A]
  def toSeq: Seq[A]
  def toStream: Stream[A]
  def sortWith(lt : (A,A) => Boolean): Traversable[A]
  def mkString(start: String, sep: String, end: String): String
  def mkString(sep: String): String
  def mkString: String
  def addString(b: StringBuilder, start: String, sep: String, end: String): StringBuilder
  def addString(b: StringBuilder, sep: String): StringBuilder
  def addString(b: StringBuilder): StringBuilder
  def toString
  def stringPrefix : String
  def view
  def view(from: Int, until: Int): TraversableView[A, Traversable[A]]
 */
}
