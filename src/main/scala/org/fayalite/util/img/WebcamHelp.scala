package org.fayalite.util.img

import java.awt.{Color, Graphics, GraphicsEnvironment}
import java.io.File
import javax.imageio.ImageIO
import javax.swing.JFrame

import com.github.sarxos.webcam.{Webcam, WebcamEvent, WebcamListener}

import scala.collection.JavaConversions._

trait WebCamHelp {

  implicit class WebCamOps(wc: Webcam) {
    def savePNG(out: String) = {
      ImageIO.write(
        wc.getImage(), "PNG", new File(out))
    }
    def drawTo(g: Graphics) = {
      val img = wc.getImage
      g.drawImage(img, 0, 0, img.getWidth, img.getHeight, Color.BLACK, null)
    }
  }

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

  def graphics = { GraphicsEnvironment getLocalGraphicsEnvironment }
  def devices = { graphics getScreenDevices }.toList

}


object WebCamHelp {


  def main(args: Array[String]) {


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
