package org.fayalite.agg.demo

import java.awt.BorderLayout
import java.io.File
import javax.swing.JPanel

import fa.Schema._
import fa._
import org.fayalite.agg.{QuickFile, QuickPanel, SeleniumChrome}


/**
  * Created by aa on 3/10/2016.
  */
class LinkedInCrawlDemo()(implicit parentPanel: JPanel)
  extends QuickPanel("LinkedIn Demo")(parentPanel, BorderLayout.NORTH) {

  store("test", Map("yo" -> 1))

  val back = read[Map[String, Int]]("test")

  println(back)

  var cb : SeleniumChrome = null

  button("Open browser for cookie saving", {
    cb = new SeleniumChrome(Some("http://www.linkedin.com"))
  }
  )
  button("After logging in, click here to save your cookies "  +
    "for future sessions", {
    store("cookies", cb.extractCookies)
    println("saved cookies!")
  }
  )

  var numVisitsByDay = T{read[Map[String, Int]]("numVisitsByDay")}
    .getOrElse(Map())
  val cur = currentDay
  var prv = numVisitsByDay.getOrElse(cur, 0)
  val tx = text("Num visits today: " + prv)

  val lqf = new QuickFile(
    "LinkedIn URL Run (assumes cookies have been saved)",
    (f: File) => {
    val urls = fromFile(f).getLines.toSeq
    cb = new SeleniumChrome(Some("http://www.linkedin.com"))
    println("attempting to set cookies")
    read[List[Cookie]]("cookies").foreach{cb.setCookieAsActive}
    urls.foreach{
      q =>
        println("Num vistis today " + prv)
        if (prv > 500) {
          println("SKIPPING URL due to exceeded maximum num attempts per day of 500")
        } else {
          println("going to url " + q)
          cb.navigateToURL(q)
          prv = numVisitsByDay.getOrElse(cur, 0)
          tx.setText("Num visits today: " + prv)
          numVisitsByDay = numVisitsByDay
            .updated(cur, prv + 1)
          println("waiting 15s before next load")
          Thread.sleep(15 * 1000)
        }

    }
  })

}
