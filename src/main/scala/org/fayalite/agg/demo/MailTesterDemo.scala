package org.fayalite.agg.demo

import java.awt.BorderLayout
import java.io.File
import javax.swing.{JComboBox, JPanel}

import org.fayalite.agg.{MailTester, QuickFile, QuickPanel}

import scala.util.Try

import fa._



/**
  * Created by aa on 3/11/2016.
  */
class MailTesterDemo()(
  implicit parentPanel: JPanel
)
  extends QuickPanel(
  "MailTester Demo"
)(parentPanel, BorderLayout.SOUTH) {


   // val aa = new JComboBox[String](headers)
  //subPanel.add(aa)

  val lqf = new QuickFile(
    "MailTester CSV Input",
    (f: File) => {
      val cb = new MailTester()

      val (headers, res) = MailTester.processFile(f)

      val proc = res.map{MailTester.processLine}

      val newHeaders = headers ++ Seq(
        "VerifiedEmail", "PossibleEmail", "BadEmail", "MailTesterDebugResponse"
      )

      val newRes = proc.map{e =>
        val perm = MailTester.getPermutations(e.name)
        println("Guessing email " + e)
        println("Permutations " + perm)

        val emailsToTest = perm.map{
          p =>
            p + "@" + e.domain
        }

        println("EmailsToTest : " + emailsToTest)

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
      writeCSV("output.csv", newCSV)

    })

}
