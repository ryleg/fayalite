package org.fayalite.util

import java.awt.GraphicsEnvironment
import java.io.File
import java.util
import javax.imageio.ImageIO
import javax.swing.JFrame

import com.github.sarxos.webcam.{WebcamEvent, WebcamListener, Webcam}

import scala.collection.JavaConversions
import scala.collection.JavaConversions._

/**
 * Created by aa on 11/28/2015.
 */
object MutaCam {

  // lets attempt to draw a graph of numbers
  // and there operative relations to one another using
  // mutable image, then feedback harmonics of it using
  // cam. -- for text editing.

  // can we make aliases automatic?
  def get = Webcam.getWebcams.toList

  def imageBytes = get map {_.getImageBytes}

  def onImage[T](f: WebcamEvent => T) =
    get map { q => q.addWebcamListener {
      new WebcamListener {
        override def webcamOpen(we: WebcamEvent): Unit = ()

        override def webcamImageObtained(we: WebcamEvent): Unit = f(we)

        override def webcamClosed(we: WebcamEvent): Unit = {}

        override def webcamDisposed(we: WebcamEvent): Unit = {}
      }
    }}

  //need hotkey for jump to right of declaration. on line.
  def graphics = { GraphicsEnvironment getLocalGraphicsEnvironment }


  import JavaConversions._
  def devices = { graphics getScreenDevices }.toList



  def main(args: Array[String]) {


    val frame = new JFrame("wtf")
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
   // frame.getContentPane().add(mainPanel);
    frame.pack();
    frame.setLocationByPlatform(true);
    frame.setVisible(true);

  //  frame.
  //  import com.github.sarxos.webcam.

  val webcam = Webcam.getDefault();
  webcam.open();

    val img = webcam.getImage
    val byt = webcam.getImageBytes
    /*
    webcam.getWebcamListeners map {
      _.webcamImageObtained()
    }*/
   Webcam.getWebcams
  ImageIO.write(

    webcam.getImage(), "PNG", new File("hello-world.png"));

    Thread.sleep(Long.MaxValue)
}}
