import org.fayalite.util.dsl._
import org.jsoup.nodes.Document


/**
 * This is for quick declarations of implicits that
 * you might need to shuffle down into a trait later
 *
 * Use this to declare common lib-wide extensions
 * that are probably useful but you don't really know where to
 * put. When they hit a critical mass organize them or port
 * them over to some other trait to prevent clutter in here.
 */
package object fa  extends AkkaExt
with JSONLikeHelpers
with CommonMonadExt
with MethodShorteners
with HTMLParsingHelpers
with StringFormatHelpers
with FileHelpUtilities
with CommonJunk
{

  // WARNING : This could cause you problems on import, that's why
  // its here at the top to make that clear.
  implicit val ecc = scala.concurrent.ExecutionContext.Implicits.global

  implicit class LO[K, V](l: List[(K,V)]) { def gbk = l.groupBy{_._1}
    .map{case (x,y) => x -> y.map{_._2}}}

  // DO NOT PUT CASE CLASSES HERE
  // For some reason the 2.10.4 IntelliJ sbt throws ridiculous errors if
  // they are here

}
