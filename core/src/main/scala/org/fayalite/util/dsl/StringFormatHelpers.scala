package org.fayalite.util.dsl

import fa.Schema
import Schema.FixedPhrase

/**
  * Created by aa on 2/18/2016.
  */
trait StringFormatHelpers {

  /**
    * For simple string modifications
    * that don't qualify as performance intensive
    * for dirty replacements
 *
    * @param se : String to mess with
    */
  implicit class WithAdditions(se: String) {
    def withQuotes = {
      '"'.toString + se + '"'.toString
    }
      /**
        * Replace all occurrences of any pattern
        * in this collection within string
        *
        * @param out : Stuff to throw out, replace with
        *            emptyString
        */
    def withOut(out: List[String]) = {
        out.foldLeft(se) { case (x,y) => x.replaceAll(y, "") }
      }
  }



  /**
    * Performs standard Java/unicode normalization on the trimmed and lowercased form
    * of the input String and then adds a few extra tricks for dealing with special
    * characters.
    *
    * JVM/Unicode normalization references (warning: learning curve black hole, beware!):
    *
    * - http://docs.oracle.com/javase/7/docs/api/java/text/Normalizer.html
    * - http://stackoverflow.com/questions/5697171/regex-what-is-incombiningdiacriticalmarks
    * - http://stackoverflow.com/questions/1453171/%C5%84-%C7%B9-%C5%88-%C3%B1-%E1%B9%85-%C5%86-%E1%B9%87-%E1%B9%8B-%E1%B9%89-%CC%88-%C9%B2-%C6%9E-%E1%B6%87-%C9%B3-%C8%B5-n-or-remove-diacritical-marks-from-unicode-cha
    * - http://lipn.univ-paris13.fr/~cerin/BD/unicode.html
    * - http://www.unicode.org/reports/tr15/tr15-23.html
    * - http://www.unicode.org/reports/tr44/#Properties
    *
    * Some special cases, like "ø" and "ß" are not being stripped/replaced by the
    * Java/Unicode normalizer so we have to replace them ourselves.
    */

  import java.text.Normalizer.{normalize => jnormalize, _}

  //object NormalizeSupport extends NormalizeSupport

  def normalize(in: String): String = {
    val cleaned = in.trim.toLowerCase
    val normalized = jnormalize(cleaned, Form.NFD).replaceAll("[\\p{InCombiningDiacriticalMarks}\\p{IsM}\\p{IsLm}\\p{IsSk}]+", "")

    normalized.replaceAll("'s", "")
      .replaceAll("ß", "ss")
      .replaceAll("ø", "o")
      .replaceAll("[^a-zA-Z0-9-]+", "-")
      .replaceAll("-+", "-")
      .stripSuffix("-")
  }

  implicit class SuperClean(s: String) {

    val bad = Seq()

    def superClean = {
      bad.foldLeft(s.map{_.toLower}) { case (agg, nv) =>
        agg.replaceAll("\\." + nv, "")
      }
    }.replaceAll("  ", " ")

    def postClean = {
      s.replaceAll("  ", " ").replaceAll("   ", " ").replaceAll("    ", " ")
    }

    val BAD_CHARS = Array('"', '.', ',', '\'', ':', '+', '&', '-')

    def clean = {
      s.filterNot(BAD_CHARS.contains)
    }
  }

  def fix(q: String) = {
    val normalized = {
      import java.text.Normalizer.{normalize => jnormalize, _}
      jnormalize(q, Form.NFD).replaceAll(
        "[\\p{InCombiningDiacriticalMarks}\\p{IsM}\\p{IsLm}\\p{IsSk}]+", "")
    }
    val qq = normalized.superClean.clean.postClean
    FixedPhrase(qq, q,
      qq.split(" ").toList)
    //   .filterNot{IDENT.contains}
  }

}
