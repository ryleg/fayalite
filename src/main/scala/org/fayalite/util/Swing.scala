package org.fayalite.util

import java.awt._
import java.awt.event._
import java.io.File
import javax.imageio.ImageIO
import javax.swing.{JFrame, JLabel, JPanel}

import com.github.sarxos.webcam.Webcam

class U {

  import Swing._

  val frame = mkFrame
  val pane = frame getContentPane

  val mainPanel = new JP()
  mainPanel.setVisible(true)
  mainPanel.setPreferredSize(new Dimension(500, 500))
  mainPanel.setBackground(Color.BLACK)

  val l = new KeyListener {

    def keyPressed(k: KeyEvent) = {
      println("key pressed " + k.getKeyChar)
    }

    def keyTyped(k: KeyEvent) = {
      println("Key typed " + k.getKeyChar)
    }

    def keyReleased(k: KeyEvent) = {
      println("Key released " + k.getKeyChar)
    }

  }

  frame.addKeyListener(l)

  def init(m: MouseListener) = {
    frame addMouseListener (m)

    pane add mainPanel

    frame.pack()

  //  frame.setLocationByPlatform(true)
    frame.setVisible(true)
  }

}



object Swing {

  def screenSize = Toolkit.getDefaultToolkit().getScreenSize()


/*
  val image = ImageIO.read(
    new File("lines.png"))
*/

  val webcam = Webcam.getDefault()
  webcam.open()

  var pc = (g: Graphics) => {


    val img = webcam.getImage

    g.drawImage(img, 0, 0, img.getWidth, img.getHeight, Color.BLACK, null)
  //  val byt = webcam.getImageBytes
    /*
    webcam.getWebcamListeners map {
      _.webcamImageObtained()
    }*/
   /* Webcam.getWebcams
    ImageIO.write(

      webcam.getImage(), "PNG", new File("hello-world.png"));*/

    /*  g.setColor(Color.DARK_GRAY)
      g.drawRect(15, 15, 200, 50)
      g.setColor(Color.RED)
      val f = new Font(Font.SANS_SERIF, Font.PLAIN, 25)
      g.setFont(f)
      g.drawString("Dump cookies", 25, 50) //(50, 50, 100, 100)*/
  }

  class JP extends JPanel {
    override protected def paintComponent(g : Graphics) = {
      super.paintComponent(g)

     // pc(g)
    }
    override def getPreferredSize = new Dimension(500, 500)
  }

  def mkFrame = {
    val frame = new JFrame("Selenium Control Panel") {

      override def processWindowEvent(we: WindowEvent): Unit = {
      //  println("windowevent" + we.getID)
       // if (we.getID == WindowEvent.WINDOW_DEACTIVATED) {
      //    println("window deactiveated")
          //   this.setState(Frame.NORMAL)
          //    this.requestFocus()
          setExtendedState(getExtendedState | Frame.MAXIMIZED_BOTH)
/*
          GraphicsEnvironment.getLocalGraphicsEnvironment.getScreenDevices.foreach {
            case q if q.getIDstring.contains("3") => q.setFullScreenWindow(this)
            case _ =>
          }*/
      /*  }
        else {
          println("sometfing else")
          super.processWindowEvent(we)*/
     //   }
      }
    }

    frame.setExtendedState(frame.getExtendedState())

    frame.setBackground(Color.BLACK)
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)
    frame.setAlwaysOnTop(true)
    frame.setEnabled(true)

    frame.setUndecorated(true)
   // frame.setPreferredSize(new Dimension(500, 500))
    GraphicsEnvironment.getLocalGraphicsEnvironment.getScreenDevices.foreach{
      case q if q.getIDstring.contains("3") => q.setFullScreenWindow(frame)
      case _ =>
    }

   // frame.set

    frame
  }

  def main(args: Array[String]) {

    val u = new U()
    val m = new MouseListener {
      override def mouseExited(e: MouseEvent): Unit = {

      }

      override def mouseClicked(e: MouseEvent): Unit = {
        val p: Point = e.getPoint

        println("msclck " + p.x + " " + p.y )
        println(p.x < 215)
        println(p.y < 100)
        if (p.x < 215 && p.y < 100) {
          println("yo saveding")
        //  c.svCookies()
        }

      }

      override def mouseEntered(e: MouseEvent): Unit = {

      }

      override def mousePressed(e: MouseEvent): Unit = {

      }

      override def mouseReleased(e: MouseEvent): Unit = {

      }
    }

    u.init(m)

    /*
    var vb: Array[Byte] = Array.fill(1e4 toInt)(0.toByte)
    scala.util.Random.nextBytes(vb)
*/
    /*
        val vi: VolatileImage = frame.createVolatileImage(100, 100)

        val ge = GraphicsEnvironment.getLocalGraphicsEnvironment()
        val gc = ge.getDefaultScreenDevice().getDefaultConfiguration()
        def createVolatileImage(width: Int, height: Int, transparency: Int): VolatileImage = {
          val image = gc.createCompatibleVolatileImage(width, height, transparency)
          val valid = image.validate(gc)
          println("valid " + valid)
          if (valid == VolatileImage.IMAGE_INCOMPATIBLE)
          createVolatileImage(width, height, transparency)
          else image
        }

        var vimage = createVolatileImage(800, 600, Transparency.OPAQUE)

        var g: Graphics2D = null

        vimage.createGraphics()


        val w = 800
        val h = 600
        val t = Transparency.OPAQUE

        do {
          if (vimage.validate(gc) == VolatileImage.IMAGE_INCOMPATIBLE) {
            vimage = createVolatileImage(w, h, t)
          }
          println("Contents lost")
          Thread.sleep(500)
          Try {
            g = vimage.createGraphics()
            g.drawBytes(vb, 0, 0, 0, 0)
          }
          g.dispose()
        } while (vimage.contentsLost())

        $

wget http://downloads.sourceforge.net/project/campagnol/campagnol/0.3.5/campagnol-0.3.5.tar.bz2?r=http%3A%2F%2Fsourceforge.net%2Fprojects%2Fcampagnol%2Ffiles%2F&ts=1454107875&use_mirror=vorboss
mv camp* camp
cd camp*
./configure
make
sudo make install


    */


  //  Thread.sleep(Long.MaxValue)

  }

}
