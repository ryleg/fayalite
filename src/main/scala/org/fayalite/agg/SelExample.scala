package org.fayalite.agg

import java.awt.{Dimension, BorderLayout}
import java.awt.event.{ActionEvent, ActionListener}
import java.io.File
import javax.swing._
import javax.swing.event.{ListSelectionEvent, ListSelectionListener}

import fa.Schema.Cookie
import fa._
import org.fayalite.agg.demo.{MailTesterDemo, LinkedInCrawlDemo}
import org.fayalite.repl.REPLFrame
import org.fayalite.ui.j2d.{ToyFrame, FButton}
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
    subPanel.add(new FButton(s, () => t).jButton)
  }
  def text(s: String) = {
    val jl = new JLabel(s)
    subPanel.add(jl)
    jl
  }
}

class QuickFile(title: String, val proc: File => Unit)
               (implicit parentPanel: JPanel)
 extends QuickPanel(title)(parentPanel, BorderLayout.CENTER) {
  val slf = new JLabel("no file selected")
  val fc = new JFileChooser()

  var lastSelectedFile : File = Try{
    new File(read[String]("lastSelectedFile" + title) )}.getOrElse(null)




  val but = new FButton("Select File",
    () => fc.showOpenDialog(parentPanel)
  ).jButton
  fc.addActionListener(new ActionListener {
    override def actionPerformed(e: ActionEvent): Unit = {
      slf.setText(fc.getSelectedFile.getName)
      lastSelectedFile = fc.getSelectedFile
      store("lastSelectedFile" + title, lastSelectedFile.getCanonicalPath)
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





class SelExample extends KVStore {

  val te = new ToyFrame

  implicit val parPanel = te.jp

  new LinkedInCrawlDemo()

  new MailTesterDemo()

  te.finish()

/*
  te.jp.add(new JLabel("Native Scala REPL"))
  val tb = new JTextField("val x = 1")
  val co = new JTextArea(10, 10)
  co.setEditable(false)
  val sp = new JScrollPane(co)
  sp.setMaximumSize(  new Dimension(500, 500))
  parPanel.add(sp)
  parPanel.add(tb)
  val rr = new REPLFrame()
  tb.addActionListener(new ActionListener{
    override def actionPerformed(e: ActionEvent): Unit = {
      println("new action event " + e.getActionCommand)
       val out = rr.nr.interpret(tb.getText)
      co.setText(co.getText + "\n" + out)
    }
  })


*/



}
