package org.fayalite.agg

import java.awt.Component
import java.awt.event.{ActionEvent, ActionListener}
import java.io.File
import javax.swing._
import javax.swing.event.{ListSelectionEvent, ListSelectionListener}

import fa.Schema.Cookie
import fa._
import org.fayalite.agg.SelExample.MetaStore
import org.fayalite.util.{Button, ToyFrame}
import rx._

import scala.util.Try


object SelExample {

  case class MetaStore(
                        cookies: List[Cookie],
                        pageVistsByDomainTime: Map[String, Int]
                      )
  def main(args: Array[String]) {
  }

}

abstract class QuickPanel(title: String)(implicit parentPanel: JPanel) {
  val subPanel = new JPanel()
  val descr = new JLabel(title)
  subPanel.add(descr)
  parentPanel.add(subPanel)
}

class QuickFile(title: String, proc: => Unit)(implicit parentPanel: JPanel)
 extends QuickPanel(title)(parentPanel){
  val slf = new JLabel("no file selected")
  val fc = new JFileChooser()
  val but = new Button("Select File",
    () => fc.showOpenDialog(parentPanel)
  ).jButton
  fc.addActionListener(new ActionListener {
    override def actionPerformed(e: ActionEvent): Unit = {
      slf.setText(fc.getSelectedFile.getName)
      proc
    }
  })
  subPanel.add(slf)
  subPanel.add(fc)
  subPanel.add(but)
  }


class MicroList(
                 title: String,
                 values: Seq[String]
               )
               (implicit parentPanel: JPanel)
  extends QuickPanel(title)(parentPanel){
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
  import te.{ad, addButton => button}
  implicit val parPanel = te.jp

  new QuickFile("LinkedIn URL Run", {

  }
  )

  // Cookies by domain map.
  /*

  */
  //ad { new JLabel("store" + store().pageVistsByDomainTime.toList.toString)}
  /*
    button("Run .urls.txt", {
      readLines(".urls.txt").foreach{
        q =>
          println("going to url " + q)
          val domain = q.domain
          println("url domain " + domain)
          cee.navigateToURL(q)
          val cur = currentDay + " " + domain
          val prvMap = store().pageVistsByDomainTime
          val prvV = prvMap.getOrElse(cur, 0)
          val newMap = prvMap.updated(cur, prvV + 1)
          println("newmap ? " + newMap)
          store() = store().copy(pageVistsByDomainTime=prvMap.updated(cur, prvV + 1))
          Thread.sleep(15*1000)
      }
    })
  */

  te.finish()

}
