package org.fayalite.agg

import java.awt.Component
import java.awt.event.{ActionEvent, ActionListener}
import java.io.File
import javax.swing.{JFileChooser, JLabel, JList, JScrollPane}

import ammonite.ops._
import fa._
import org.fayalite.util.ToyFrame
import org.jsoup.nodes.Document
import rx._
import scala.util.Try
import Schema._


object SelCtrl {

  def main(args: Array[String]) {
    new SelExample()
  }


}

