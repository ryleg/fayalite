package org.fayalite.agg.demo

import java.awt.BorderLayout
import java.io.File
import java.util.concurrent.Executors
import javax.swing.{JComboBox, JPanel}

import com.github.tototoshi.csv.CSVWriter
import org.fayalite.agg.MailTester.{Name, EmailGuessRequirements}
import org.fayalite.agg.demo.EmailTester.{ProcessLine, EmailTestResults}
import org.fayalite.agg._
import org.openqa.selenium.phantomjs.PhantomJSDriver
import rx.core.Var

import scala.collection.{mutable, JavaConversions}
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}

import fa._

object EmailTester {
  case class EmailTestResults(
                               domainInvalid: Boolean,
                               domainRefusesRequests: Boolean,
                               emailUserColor: String,
                               emailDomainColor: String,
                               debugReport: String
                             )

  case class ProcessLine(line: List[String], emailGuessRequirements: EmailGuessRequirements)
}

trait EmailTester {
  def testEmail(email: String) : EmailTestResults
}

/**
  * Created by aa on 3/11/2016.
  */
class MailTesterDemo()(
  implicit parentPanel: JPanel
)
  extends QuickPanel(
    "MailTester Demo"
  )(parentPanel, BorderLayout.SOUTH) {




  val debugHeaders = Seq(
    "SanitizedFirstName",
    "SanitizedLastName",
    "SanitizedDomain",
    "SanitizedPreExistingEmail",
    "isDomainValid",
    "domainAllowsVerification"
  )
//
//subPanel.add(aa)

val lqf = new QuickFile(
"MailTester CSV Input",
(f: File) => {
  val fnm = f.getName.split("\\.").head
  val outputFnm = fnm + "_output.csv"

  val (headers, res) = MailTester.processFile(f)

  val combos = Seq("First Name", "Last Name", "Domain/Email").map {
    q =>
      import JavaConversions._
      text(q)
      val aa = new JComboBox(headers.toArray)
      subPanel.add(aa)
      aa
  }
  combos(1).setSelectedIndex(1)

  val domainEmIdx = headers.zipWithIndex.collectFirst {
    case (x, i) if x.toLowerCase.contains("email") || x.toLowerCase.contains("domain") => i
  }.getOrElse(2)

  combos(2).setSelectedIndex(domainEmIdx)

  val lineProcessQ = new mutable.Queue[ProcessLine]()

  val newHeaders = headers ++ Seq(
    "isDomainValid",
    "canMakeVerifications",
    "acceptsConnections",
    "GreenEmailLongest",
    "GreenOrYellowAlternateEmails")
  /*   "RedEmail"
  ).flatMap {
    q =>
      Seq.tabulate(9) {
        i =>
          q + "_" + i
      }
  }// :+ "ReportDebugString"
  */
  println("New headers " + newHeaders)
  import rx.ops._
  val writ = CSVWriter.open(outputFnm, true)
  writ.writeRow(newHeaders)



  val threadsToEmails = mutable.Map[Int, Int]()

  F {
    while (true) {
      Thread.sleep(5000)
      println(synchronized(threadsToEmails))
    }
  }

  def onDriverReady(pjm: PJSMailTester) = {

    //val cap = pjsd.getCapabilities
   // val pjm = new PJSMailTester(pjsd)
    while (synchronized(lineProcessQ.nonEmpty)) {
     // println("Driver is ready on Thread-ID: " + Thread.currentThread().getId)
      val sample = synchronized {
        val dq = lineProcessQ.dequeue()
      //  println("Synchronized line process dq " + dq)
        dq
      }

      var success = false

      var newLine = sample.line

      while (!success) {
        val newLinse = T {
          pjm.processLineActual(sample.line, sample.emailGuessRequirements)
        }
        if (newLinse.isSuccess) {
          synchronized{threadsToEmails(pjm.id) = pjm.numEmailsTested()}
          newLine = newLinse.get
          success = true
        }
        else {
          println("restartDriverThread-ID: " + Thread.currentThread().getId)
          pjm.restartDriver()
        }
      }

      synchronized {
        println("Saving newline " + newLine)
        writ.writeRow(newLine)
      }
    }
  }

  button("Start MailTester PhantomJS Run", {

    println("Launching proxy drivers ")
    val pjs = PJS.launchProxyDrivers("http://www.mailtester.com", numDrivers = 60)
    pjs.zipWithIndex.foreach { case (x,y) =>
      x.onComplete {
        _.foreach { z =>
          println("ON DRIVER READY START " + Thread.currentThread().getId)
          Future {
            onDriverReady(new PJSMailTester(z, id=y))
          }
        }
      }
    }

    val idxs = combos.map {
      _.getSelectedIndex
    }

    val rdy = res.map {
      line =>
        val first = line(idxs(0)).withOut(List(" "))
        val last = line(idxs(1)).withOut(List(" "))
        val domain = line(idxs(2)).withOut(List(" "))
        val preExi = if (domain.contains("@")) Some(domain) else None

        val strippd = domain.withOut(
          List("http://", "https://", "www\\.", "/")
        )
        val guardEmail = strippd.split("@").tail.mkString

        val guardDot = guardEmail match {
          case z if z.contains("\\.") => z.split("\\.").tail.mkString
          case z => z
        }
        ProcessLine(line, EmailGuessRequirements(Name(first, last), guardDot, preExi))
    }

    rdy.foreach { r =>
      lineProcessQ += r
    }

    println("Line process queue ready with # elements: " + lineProcessQ.size)

  })
})

}
