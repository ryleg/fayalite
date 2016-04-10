import java.util.concurrent.Executors

import org.fayalite.agg.SeleniumHelp
import org.fayalite.util.dsl._
import org.fayalite.util.img.ImageHelp

import scala.concurrent.ExecutionContext


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
with SeleniumHelp
with ImageHelp
{

  // WARNING : This could cause you problems on import, that's why
  // its here at the top to make that clear.
  implicit val ecc = ExecutionContext.fromExecutor(Executors.newFixedThreadPool(300)) //scala.concurrent.ExecutionContext.Implicits.global

  // DO NOT PUT CASE CLASSES HERE
  // For some reason the 2.10.4 IntelliJ sbt throws ridiculous errors if
  // they are here

}
