package org.fayalite.agg.demo

import java.awt.BorderLayout
import java.io.File
import javax.swing.{JComboBox, JPanel}

import org.fayalite.agg.MailTester.EmailGuessRequirements
import org.fayalite.agg.demo.EmailTester.EmailTestResults
import org.fayalite.agg.{MailTester, QuickFile, QuickPanel}

import scala.collection.JavaConversions
import scala.concurrent.Future
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
  def testEmail(email: String) : Future[EmailTestResults]
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


  def processOne(e: EmailGuessRequirements, emailTester: EmailTester) = {

    val perm = MailTester.getPermutations(e.name)
    println("Guessing email " + e)
    println("Permutations " + perm)

    val emailsToTest = perm.map{
      p =>
        p + "@" + e.domain
    }
    println("EmailsToTest : " + emailsToTest)

    emailTester.testEmail(emailsToTest.head).onComplete{
      case Success(x) =>

      case Failure(e) =>
        "ERR"
    }


  }

/*  def process(
               res: List[List[String]],
               headers: List[String]
             ) = {

    val proc = res.map{MailTester.processLine}

    val newHeaders = headers ++ Seq(
      "VerifiedEmail", "PossibleEmail", "BadEmail", "MailTesterDebugResponse"
    )

    val newRes = proc.map{e =>

      val emailToColor = emailsToTest.map{e =>
        println("Testing Email : " + e)
        val cl = Try { cb.testEmail(e) }

        println("Email color : " + cl)
        println("Waiting 10 seconds to test another email")
        Thread.sleep(10*1000)
        e -> cl.getOrElse("ERROR")
      }

      def getMaxEmail(c: String) = {
        emailToColor.filter{
          _._2 == c
        }.sortBy{_._1.length}.lastOption.map{_._1}.getOrElse("")
      }

      val newLine = Seq(e.name.first, e.name.last, e.domain) ++
        Seq("Green", "Yellow", "Red").map{getMaxEmail} :+ {
        emailToColor.map{
          case (x,y) => x + " " + y
        }.mkString("|")
      }
      println("New CSV Line " + newLine)
      newLine
    }

    val newCSV = newHeaders :: newRes
    writeCSV("output.csv", newCSV)*/


   //
  //subPanel.add(aa)

  val lqf = new QuickFile(
    "MailTester CSV Input",
    (f: File) => {
      val cb = new MailTester()

      val (headers, res) = MailTester.processFile(f)

      Seq("First Name", "Last Name", "Domain/Email").map{
        q =>
          import JavaConversions._
          text(q)
          val aa = new JComboBox(headers.toArray)
          subPanel.add(aa)
          aa
      }

      button("Start MailTester PhantomJS Run", {

      })

    })

}
