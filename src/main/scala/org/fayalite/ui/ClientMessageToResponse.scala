package org.fayalite.ui

import java.awt.image.RenderedImage
import javax.imageio.ImageIO;

import rx._

import scala.util.{Failure, Success, Try}

object ClientMessageToResponse {


  var allMsgs = ""

  val parsedMessage = Var("")

  import sun.awt.image.codec.JPEGImageEncoderImpl

  import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream
  import java.awt.image.BufferedImage
  def bi2db(bi: BufferedImage) = {
    //val fos = new ByteOutputStream()
   // val j = new JPEGImageEncoderImpl(fos)
  //  j.encode(bi)
    //fos.close()
   // fos.getBytes
  }
  import java.awt.Color
//  val rgbArray = Array.fill(300*300)(0)

  def moveToResponse(x: Int, y: Int) = {
/*
    var imgData=ctx.getImageData(0,0,c.width,c.height);
    // invert colors
    for (var i=0;i<imgData.data.length;i+=4)
    {
      imgData.data[i]=255-imgData.data[i];
      imgData.data[i+1]=255-imgData.data[i+1];
      imgData.data[i+2]=255-imgData.data[i+2];
      imgData.data[i+3]=255;
    }
    ctx.putImageData(imgData,0,0);*/
    import java.awt.image.BufferedImage
    val image = new BufferedImage(460, 495, BufferedImage.TYPE_INT_ARGB);
    val g = image.createGraphics()
    g.setBackground(Color.red)
    //g.drawImage(image, null, 0, 0);
    g.setColor(Color.white)
    g.drawString("yo", x, y) // (x/300, y/300)
    val w = image.getWidth
    val h = image.getHeight
    val argb =     image.getRGB(0, 0, w, h, null, 0, w);

    val rgba = argb.flatMap{ b =>
        val c = new Color(b, true);
        Seq(c.getRed(),
   c.getGreen(),
 c.getBlue(),
 c.getAlpha())
    }.mkString(",")

    val ri = image.asInstanceOf[RenderedImage]
    val fi = new java.io.File("/Users/ryle/adfsf.jpg")
    ImageIO.write(ri, "JPEG", fi)

    rgba
  }

  def main(args: Array[String]) {
    println(moveToResponse(10, 10).toList.length)
  }

  def parse(msg: String) : String = {
    val atrep = Try {
      val sm = msg.split(",").toList
      println("parse " + sm)
//      val cod = sm.headOption
      if (sm.length > 1) {
        sm.head match {
          case "move" =>
            val x = sm(1).toInt
            val y = sm(2).toInt
            //    println("move PARSE" + x + " " + y)
            "putpixel," + moveToResponse(x, y)
          case _ =>
            //println(" not move")
            "failed parse"
        }
      }
      else "fail"
    } match {
      case Success(x) => x
      case Failure(e) => e.printStackTrace(); "Failedtrypasre"
    }
    atrep
  }

  def startWatch() = {
    Obs(Duplex.a) {
      println("startWatch watch")
      val curMsg = Duplex.a()
      //allMsgs += curMsg
      val pm = parse(curMsg)
      println(pm)
     // pm.map { p => parsedMessage() = p

    }
  }

}
