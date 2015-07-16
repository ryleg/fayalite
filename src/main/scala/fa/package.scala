import org.fayalite.{util, Fayalite}


import org.fayalite.util.dsl._

import scala.util.Random



package object fa  extends AkkaExt
with CommonMonadExt
with ScaryExt
with VeryCommon
with MethodShorteners
{

  def rport = Random.nextInt(50000) + 1500

}
