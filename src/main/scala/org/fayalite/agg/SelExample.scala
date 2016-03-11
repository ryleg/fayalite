package org.fayalite.agg

import java.awt.BorderLayout
import java.awt.event.{ActionEvent, ActionListener}
import java.io.File
import javax.swing._
import javax.swing.event.{ListSelectionEvent, ListSelectionListener}

import fa.Schema.Cookie
import fa._
import org.fayalite.util.{Button, ToyFrame}
import rx._

import scala.util.Try


object SelExample {

  case class MetaStore(
                        cookies: List[Cookie],
                        pageVistsByDomainTime: Map[String, Int]
                      )
  def main(args: Array[String]): Unit = {
    new SelExample()
  }

}

abstract class QuickPanel
(title: String)
(implicit parentPanel: JPanel,
 childToParentLayout: String
) extends KVStore {
  val subPanel = new JPanel()
  val descr = new JLabel(title)
  subPanel.add(descr)
  parentPanel.add(subPanel, childToParentLayout)
  def button(s: String, t: => Unit) = {
    subPanel.add(new Button(s, () => t).jButton)
  }
  def text(s: String) = {
    val jl = new JLabel(s)
    subPanel.add(jl)
    jl
  }
}

class QuickFile(title: String, proc: File => Unit)
               (implicit parentPanel: JPanel)
 extends QuickPanel(title)(parentPanel, BorderLayout.CENTER) {
  val slf = new JLabel("no file selected")
  val fc = new JFileChooser()
  val but = new Button("Select File",
    () => fc.showOpenDialog(parentPanel)
  ).jButton
  fc.addActionListener(new ActionListener {
    override def actionPerformed(e: ActionEvent): Unit = {
      slf.setText(fc.getSelectedFile.getName)
      proc(fc.getSelectedFile)
    }
  })
  subPanel.add(slf)
 // subPanel.add(fc)
  subPanel.add(but)
  }


class MicroList(
                 title: String,
                 values: Seq[String]
               )
               (implicit parentPanel: JPanel)
  extends QuickPanel(title)(parentPanel, BorderLayout.CENTER)
{
  val jls = new JList(values.toArray)
  val jscp = new JScrollPane(jls)
  jls.addListSelectionListener(new ListSelectionListener {
    override def valueChanged(e: ListSelectionEvent): Unit = {

    }
  })
  subPanel add jls
  subPanel add jscp
}

class MailTesterDemo()(
  implicit parentPanel: JPanel
)
  extends QuickPanel(
  "MailTester Demo"
)(parentPanel, BorderLayout.SOUTH) {

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

class SelExample extends KVStore {

  val te = new ToyFrame

  implicit val parPanel = te.jp

  new LinkedInCrawlDemo()

  new MailTesterDemo()

  te.finish()

}
