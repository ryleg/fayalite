import java.text.SimpleDateFormat
import java.util.Calendar

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
  def ct = {
  val today = Calendar.getInstance().getTime()
  val minuteFormat = new SimpleDateFormat("YYYY_MM_dd_hh_mm_ss")
  minuteFormat.format(today)}
}
