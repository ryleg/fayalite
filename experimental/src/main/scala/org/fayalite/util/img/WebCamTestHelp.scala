package org.fayalite.util.img

import java.io.File
import javax.imageio.ImageIO

import com.github.sarxos.webcam.Webcam




object WebCamTestHelp {


  def main(args: Array[String]) {


    val webcam = Webcam.getDefault()
    webcam.open()

    val img = webcam.getImage
    val byt = webcam.getImageBytes
    /*
    webcam.getWebcamListeners map {
      _.webcamImageObtained()
    }*/
    Webcam.getWebcams
    ImageIO.write(

      webcam.getImage(), "PNG", new File("hello-world.png"))

    Thread.sleep(Long.MaxValue)
  }}
