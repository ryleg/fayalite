package org.fayalite.agg.demo

import java.awt.BorderLayout
import java.io.File
import java.util.concurrent.Executors
import javax.swing.{JComboBox, JPanel}

import com.github.tototoshi.csv.CSVWriter
import org.fayalite.agg.MailTester.{Name, EmailGuessRequirements}
import org.fayalite.agg.demo.EmailTester.EmailTestResults
import org.fayalite.agg._
import rx.core.Var

import scala.collection.JavaConversions
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




//
//subPanel.add(aa)

val lqf = new QuickFile(
"MailTester CSV Input",
(f: File) => {
  val fnm = f.getName.split("\\.").head
  val outputFnm = fnm + "_output.csv"

  val (headers, res) = MailTester.processFile(f)

  val combos = Seq("First Name", "Last Name", "Domain/Email").map{
  q =>
  import JavaConversions._
  text(q)
  val aa = new JComboBox(headers.toArray)
  subPanel.add(aa)
  aa
}
  combos(1).setSelectedIndex(1)

  val domainEmIdx = headers.zipWithIndex.collectFirst{
    case (x,i) if x.toLowerCase.contains("email") || x.toLowerCase.contains("domain") => i
  }.getOrElse(2)

  combos(2).setSelectedIndex(domainEmIdx)


  button("Start MailTester PhantomJS Run", {

    println("Launching proxy drivers ")
    val pjs = PJS.launchProxyDrivers("http://www.mailtester.com")

    pjs.foreach{_.onComplete{_ => println("driver loaded")}}

    while (pjs.exists { q => !q.isCompleted }) {
      println(pjs.filter{_.isCompleted}.length.toString + " drivers loaded out of " + pjs.length)
      Thread.sleep(2000)
      println("waiting for drivers to launch and load page")
    }

    println("All pages loaded! Drivers ready")
    println("WARNING : Future operations depend on driver " +
      "process staying online. Any driver failures will lead to" +
      "skipped rows left as in original")

    val pjj = pjs.map {
      _.get
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
        line -> EmailGuessRequirements(Name(first, last), guardDot, preExi)
    }

    val newHeaders = headers ++ Seq(
      "SanitizedFirstName",
      "SanitizedLastName",
      "SanitizedDomain",
      "SanitizedPreExistingEmail",
      "isDomainValid",
      "domainAllowsVerification"
    ) ++ Seq(
      "GreenEmail",
      "YellowEmail",
      "RedEmail"
    ).flatMap {
      q =>
        Seq.tabulate(9) {
          i =>
            q + "_" + i
        }
    } :+ "ReportDebugString"


    val xx = Var(newHeaders)
    import rx.ops._

    val writ = CSVWriter.open(outputFnm, true)

    xx.foreach { q =>
      writ.writeRow(q)
    }

    val rdyGrp =  rdy.grouped(rdy.length / (pjj.length - 1)).toSeq

    println("rdyGrp " + rdyGrp.length)

  //  val ec = ExecutionContext.fromExecutor(Executors.newFixedThreadPool(rdyGrp.length))


   rdyGrp.zip(pjj).map {
      case (batch, driver) =>
        Future {
          val pjD = new PJSMailTester(driver)
          batch.foreach {
            case (line, egr) =>

              val preEm = egr.preExistingEmail
              val perm = MailTester.getPermutations(egr.name)
              val firstPerm = perm.head
              val firstEmailToTest = preEm.getOrElse(firstPerm + "@" + egr.domain)

              println("Testing first email " + firstEmailToTest)
              val (clr, rep) = pjD.testEmailDbg(firstEmailToTest)
              println("Tested first email " + firstEmailToTest + " with " + clr + " " + rep)

              import MailTester._

              val isDomainValid = !rep.contains(invalidMailDomain)
              val canMakeVerificationTests = !rep.contains("Server doesn't allow e-mail address verification")

              println("isDomainValid " + isDomainValid + " " + egr.domain)
              println("canMakeVerificationTests " + canMakeVerificationTests + " " + egr.domain)

              var reps = List((clr, rep, firstEmailToTest))

              var foundGreen = clr == "Green"

              if (!foundGreen && isDomainValid && canMakeVerificationTests) {
                perm.tail.foreach {
                  qq =>
                    val q = qq + "@" + egr.domain
                    if (!foundGreen) {
                      println("Waiting before testing next email 10s")
                      Thread.sleep(10000)
                      println("Testing permutation email : " + q)
                      val (cl, rp) = pjD.testEmailDbg(q)
                      println("Tested permutation email : " + q + " with color " + cl + " " + rep)
                      println("Waiting before testing next email 10s")
                      Thread.sleep(10000)
                      if (cl == "Green") foundGreen = true
                      reps :+=(cl, rp, q)
                    }
                }
              }

              val testedEmails = reps

              def getFormatted(c: String) = {
                val e = reps.filter {
                  _._1 == c
                }.map {
                  _._3
                }
                val ms = 9 - e.length
                e ++ List.fill(ms)("")
              }

              val repStr = testedEmails.map {
                case (x, y, z) =>
                  z + ":" + x + "  " + y
              }.mkString("         ")

              val startNewLine = line ++ List(
                egr.name.first, egr.name.last, egr.domain,
                egr.preExistingEmail.getOrElse(""),
                isDomainValid.toString,
                canMakeVerificationTests.toString
              ) ++ getFormatted("Green") ++
                getFormatted("Yellow") ++
                getFormatted("Red") :+ repStr

              println("newLine " + startNewLine)
              xx() = startNewLine

          }
        }//(ec)

    }
  })
})

/*

  if (lqf.lastSelectedFile != null) {
    println("loading last selected file " + lqf.lastSelectedFile.getCanonicalPath)
    lqf.fc.setSelectedFile(lqf.lastSelectedFile)
    lqf.slf.setText(lqf.lastSelectedFile.getName)
  }
*/



}
