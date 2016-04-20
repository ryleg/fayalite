package org.fayalite.util.img

import java.awt.{Color, Graphics, GraphicsEnvironment}
import java.io.File
import javax.imageio.ImageIO

import com.github.sarxos.webcam.{Webcam, WebcamEvent, WebcamListener}
import scala.collection.JavaConversions._

/**
  * Created by aa on 4/19/2016.
  */
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
