package org.fayalite.ui.app.state

import org.fayalite.ui.app.canvas.Canvas
import org.fayalite.ui.app.canvas.Schema
import org.fayalite.ui.app.canvas.{Canvas, Schema}
import org.fayalite.ui.app.comm.Disposable
import org.fayalite.ui.app.comm.PersistentWebSocket
import org.fayalite.ui.app.comm.{Disposable, PersistentWebSocket}
import org.fayalite.ui.app.manager.Editor
import org.fayalite.ui.app.manager.Editor
import org.fayalite.ui.app.state.StateSync.ParseRequest
import org.fayalite.ui.app.state.auth.OAuth
import org.fayalite.ui.app.state.auth.OAuth
import org.fayalite.ui.app.text.CellManager
import org.fayalite.ui.app.text.CellManager
import org.scalajs.dom
import org.scalajs.dom.Event
import org.scalajs.dom._
import org.scalajs.dom._
import org.scalajs.dom.raw.DataTransfer
import org.scalajs.dom.raw.Element
import org.scalajs.dom.raw.Element
import org.scalajs.dom.raw.File
import org.scalajs.dom.raw.FileList
import org.scalajs.dom.raw.FileReader
import org.scalajs.dom.raw.HTMLCanvasElement
import org.scalajs.dom.raw.HTMLElement
import org.scalajs.dom.raw.MouseEvent
import org.scalajs.dom.raw._
import rx._

import scala.scalajs.js.{JSON, Dynamic}
import scala.util.{Random, Try}

import rx.ops._

object UIExt {

  def byClass(c: String) = document.getElementsByClassName(c)

  implicit class HTE2(ht: HTMLElement) {
    def click(f: MouseEvent => Unit) = {
      ht.onclick = f
      ht
    }
    def withClass(c: String) = {
      ht.setAttribute("class", c)
      ht
    }
    def code(e: => String, f: => Unit = ()) = this.click { me: MouseEvent =>
      mkRequest(e)
      f
    }

    def clickEach(f : () => Unit) = click{me: MouseEvent => f}
  }


  def button(s: String) = {
    import org.scalajs.dom
    import scalajs.js._
    // dom.document.body
    val obj: dom.Element = dom.document.createElement("button")
    val tn = dom.document.createTextNode(s)
    obj.appendChild(tn)
    obj.asInstanceOf[HTMLElement]
  }

  implicit class jf (j: HTMLElement) {
    def wa(a: String, v: String) = {
      j.setAttribute(a, v)
      j
    }
    def wt(s: String) = {
      j.textContent = s
      j
    }
  }


  val ce =  (e : String) => {
    dom.document.createElement(e)
  }.asInstanceOf[HTMLElement]
  val cet =  (e : String, ttt: String) => {
    val eee =ce(e)
    eee.appendChild(dom.document.createTextNode(ttt))
    eee
  }.asInstanceOf[HTMLElement].withStyle("color:" + methodGold)

  val ceb =  (e : String) => {
    val ce1: Element = ce(e)
    appendBody(ce1);
    ce1
  }


  def div = ce("div")

  implicit class ela(e: Element) {
    def withChild(c: Element) = {
      e.appendChild(c)
      c
    }
    def wc(c: Element) = withChild(c)
    def wc(c: List[Element]) = {
      c.foreach{e.ac}
      e
    }
    def ac(c: Element) = e.appendChild(c)
  }
  val txRes = Var("")

  val methodOrange = "color:#FFC66D;"
  val methodGold = "#FFC66D;"
  val gold = "#FFC66D"
  val burntGold = "#CC7832"

  def bigMO = "color:#FFC66D;font-size:200%;"
  def bigM2O = "color:#CC7832;font-size:200%;"

  def appendBody(s: Element) = {
    dom.document.body.appendChild(s)
  }
  def ab(s: Element) = {
    dom.document.body.appendChild(s)
  }

  implicit class htex(hte: HTMLElement) {
    def style = hte.getAttribute("style")
    def plusStyle(s: String) = hte.withStyle(style + s)
    def withStyle(s: String) = {
      hte.setAttribute("style", s)
      hte // Change to mutex in-place @mutex ; i.e. its a block side effect
      // returning the same node in vertex pipeline.
    }
    def setLT(l: Int, t: Int) = {
      hte.setAttribute("style", hte.style + ";font-size:20px;position:absolute" +
        ";left:" + l + "%;top:" + t + "%")
    }
  }

  def plainButtonStyle = """background-color: Transparent;
                           |    background-repeat:no-repeat;
                           |    border: none;
                           |    cursor:pointer;
                           |    overflow: hidden;
                           |    outline:none;""".stripMargin

  val bgGrey = "#2B2B2B"
  val lightBlue = "#A9B7C6"
  val commentGrey = "#808080"
  val ansiGrey = "#555555"
  val ansiDarkGrey = "#1F1F1F"

  case class ParseRequest (
                            code: String,
                            cookies: String,
                            requestId: String
                            )

  def mkRequest(code: String) = {
    import upickle._
    val request = ParseRequest(code=code, cookies = dom.document.cookie,
      requestId = scala.util.Random.nextLong().toString)
    val write1: String = write(request)
    //  println("sending parserequest" + write1)
    PersistentWebSocket
      .send(write1)

  }


  def hb(s: String) = {
    button(s)
      .wa("class", "login")
      .wa("align", "center")
      .withStyle("font-size:18px;" + plainButtonStyle + methodOrange)
      .clickEach { () => {
        //  println("butn") // OAuth redirect()
      }
      }
  }
}

object StateSync2 {

  import UIExt._

  Input.heartBeat.foreach{
    e =>
      mkRequest("@fa")
  }

  case class ParseResponse(kvp: Array[String])

  val ms = PersistentWebSocket.pws.messageStr

  val response = ms.map{
    q =>
      if (q != null) {
        import upickle._
        val ret = read[ParseResponse](q)
        Some(ret)
      } else None
  }

  val kvp = response.map {
    _.map {
      _.kvp.toList
    }
  }

  val active = Var("manage")

  def debugInit() = {

    document.body.withStyle("background-color:#2B2B2B;z-index:0;margin:0;padding:0;")
    import org.scalajs.dom

    import scalajs.js._
    // dom.docume

    val b = hb("OAuth")
    b.setAttribute("class", "login")
    b.setAttribute("align", "center")
    //   b.setAttribute("style", "font-size:20px;" + plainButtonStyle + methodOrange)
    b.onclick = (_: Event) => {
      OAuth redirect()
    }
    val headr = div.withStyle(
      s"z-index:2;padding:14px;position:fixed;width:100%;background-color:$ansiDarkGrey")


    ab(headr)
    val subBody = div.withStyle(s"width:100%;background-color:$ansiDarkGrey")
    ab(subBody)

    val subLeft = div.withStyle(
      s"height:100%;display:block;z-index:1;padding-left:3px;" +
        s"padding-top:25px;position:fixed;" +
        s"width:80px;background-color:$ansiDarkGrey"
    )

    val subRight = div.withStyle(
      s"position:absolute;left:98px;top:70px;z-index:1;" +
        s"right:10px;")

    val blue = "#6897BB"


    subBody ac subLeft
    subBody ac subRight // multiple screen sbt processes with DAG dependencies.

    val bot = div
    document.body ac bot
    bot.style.position = "fixed"
    bot.style.height = "48%"; bot.style.width="100%"; bot.style.bottom = "0px"; //bot.style.{height = ..., width = ...}
    bot.style.backgroundColor = ansiDarkGrey
    bot.style.zIndex = "5"

    val cvd = div; bot ac cvd;

    val cnv = ce("canvas").asInstanceOf[HTMLCanvasElement]
    cvd ac cnv
    cnv.height = bot.clientHeight
    cnv.width = bot.clientWidth
    val ctx = cnv.getContext("2d").asInstanceOf[CanvasRenderingContext2D]
ctx.fillStyle = gold
    ctx.font = "18px monospace"

    ctx.save()

    val latestCode = Var("scala.js code")
    val loaded = Var(false)
    Obs(loaded, skipInitial = true) {
      latestCode().split("\n")
      ctx.fillText(latestCode(), 15, 15)
    //  sjs.textContent = latestCode()
    }
    def getCode = "" //sjs.textContent
/*
    val sjs = ce("textarea")
      .wa("rows", "20").wa("cols", "40")
      .withStyle(s"color:$methodGold;background-color:$ansiDarkGrey")
      .asInstanceOf[HTMLTextAreaElement]

    val dbot = div
    dbot.ac(sjs)
    bot ac dbot

    /*  document.body.ac {
        dbot.withStyle("position:fixed;bottom:0%;width:100%;z-index:4")
      }*/
    dbot.ac {
      button("Save").click { _ : MouseEvent =>
        //     println("sjs value + " + sjs.value)
        mkRequest("save" + sjs.value)
        ()
      }
    }

    dbot ac button("Load").code("load", sjs.value = latestCode())
    dbot ac button("Compile").code("compile" + sjs.value)
    dbot ac button("Execute").code("exec")

    val clazz: HTMLElement = cet("p", "Compile output").withStyle(methodOrange).withClass("compile")
    dbot ac clazz //.code("compile" + sjs.value)

*/

    val buttonS = "font-size:18px;" + plainButtonStyle + methodOrange
    //+ "background-color:" + ansiDarkGrey
    val textS = "font-size:18px;" + methodOrange + "background-color:" + ansiDarkGrey
    def mkFileInput = ce("input") wa("type", "file") withStyle buttonS
    // infer all strings.

    class El {
      val name = Var("zero")
      val on = Var(true)
    }

    def mkSubmit = button("Upload Chosen File") withStyle buttonS //{
    /*  val sub = ce("submit") wa ("type", "submit") withStyle
        (textS + "z-index:4;") //change this to mkSubmit ce submit wa type submit
      sub.textContent = "Upload"
      sub}*/
    object S { def apply[T](a: T*) = {Seq(a : _ *)}}

    //subRight ac cet("p", "placeholder")
    // change Seq to S and remove the parens.
    val fi = mkFileInput
    val fulldiv = div
    val upload = div withStyle("padding:15px;")
    fulldiv ac {
      div.withStyle("padding:15px;width:100%").wc {
        ce("a")
      }
    }

    fulldiv ac space
    fulldiv ac hb("  ").plusStyle("width:70px;")



    def mkText(s: String) = cet("p", s) withStyle (methodOrange + "font-size:16px" +
      "background-color:" + ansiDarkGrey)

    val subm = mkSubmit

    val log = div
    implicit class nld(nl: NodeList){
      def toList = (0 until nl.length).map{i => (nl.item(i))}
      def map[B](f: Node => B) = {
      (0 until nl.length).map{i => f(nl.item(i))}
    }
    }

    subm.asInstanceOf[HTMLElement].onclick = (_ : Event) => {
      val files: dom.FileList = fi.asInstanceOf[dom.DataTransfer].files
      log.childNodes.map{n => log.removeChild(n)}
      if (files.length == 0 ) {
        log ac mkText("Select a file; required for upload.")
      } else {
        //   println("numfiles " + files.length)
        log ac mkText("Reading file into memory; ")
        (0 until files.length) foreach {
          it =>
            val i: dom.File = files.item(it)
            val tz = i.slice(0, i.size, "text/plain")
            val tx = new dom.FileReader()
            tx.readAsText(tz)
            tx.onload = (_: Event) => {
              log ac mkText("Loaded file into memory; uploading to server; ")
              //       println(tx.result)
              val s3: String = tx.result.asInstanceOf[String]
              mkRequest("file" + i.name + "|" + s3)
              log ac mkText("Finished upload")
            }
        }
      }
    }

    Seq(fi, subm) foreach upload.ac
    upload ac log

    fulldiv ac upload

    val files = Var(scala.Array[String]())

    val filev = div

    fulldiv ac filev

    subRight ac fulldiv
    implicit class hteee(h: HTMLElement) {
      def withClick(f: MouseEvent => Unit) = {
        h.onclick = f
        h //how can we generalize this idea
        // of returning the original object while
        // still add a withClick handler for something else?
        // its really a monad of any sort of edge on the original?
      }
      def withHover(
                     f: MouseEvent => Unit, g: HTMLElement => Unit,
                     ff: MouseEvent => Unit, gg: HTMLElement => Unit) = {
        h.onmouseenter = (m : MouseEvent ) => {
          f(m)
          g(h)
        }
        h.onmouseleave = (m : MouseEvent ) => {
          ff(m)
          gg(h)
        }
        h
      }
      def withBlue = { // change to rx
        withHover(
          (m: MouseEvent) => (),
          (g: HTMLElement) => g.style.color = blue,
          (mm: MouseEvent) => (),
          (gg: HTMLElement) => gg.style.color = gold
        )
        h
      }
      def childs = {
        List.tabulate(h.childNodes.length){
          h.childNodes.apply
        }
      }
      def clearAll = {
        h.childs.foreach {
          c =>
            c.parentNode.removeChild(c)
        }
        h
      }
      def withActive(d: HTMLElement) = {
        h.withClick{me: MouseEvent =>
          subRight.clearAll
          subRight.appendChild(d)
        }
      }
    }



    val datadiv = div



    var opts = List[HTMLElement]()

    def getSel = opts.collectFirst{
      case jj if jj.asInstanceOf[HTMLOptionElement].selected
      => "req:" + jj.textContent
    }

    def getSelText(opts: Seq[Element]) = opts.collectFirst{
      case jj if jj.asInstanceOf[HTMLOptionElement].selected
      => jj.textContent
    }


    datadiv ac {
      button("Re").withClick({ e: MouseEvent =>
        val req = "run" + getSel.getOrElse("empty")
        mkRequest(req)
        ()
      }
      )
    }

    val seldata = div

    datadiv ac seldata

    val props = div

    datadiv ac props


    Obs(files, skipInitial = true) {
      val f = files()
      val se = ce("select").wa("size", f.length.toString)
      //  println("creating selections" + f.toList)
      seldata.clearAll
      opts = List[HTMLElement]()
      seldata ac se
      f.foreach { p => se.appendChild {
        val qz = ce("option")
        opts = opts :+ qz
        qz.textContent = p
        qz
      }
      }

      se.asInstanceOf[HTMLSelectElement].onclick =
        (_ : MouseEvent) => {
          //     println("selecting el. " + getSel.getOrElse("nonegotten"))
          getSel map mkRequest
        }


    }

    subLeft ac hb("  ").plusStyle("width:70px;")
    subLeft ac hb("  ").plusStyle("width:70px;")
    subLeft ac hb("  ").plusStyle("width:70px;")
    subLeft ac hb("DAQ").plusStyle("width:70px;").withBlue.withClick{
      m: MouseEvent =>
        subRight.clearAll
        subRight ac fulldiv
    }
    subLeft ac hb("  ").plusStyle("width:70px;")

    subLeft ac hb("Index").plusStyle("width:70px;").withBlue.withActive{
      datadiv
    }
    subLeft ac hb("  ").plusStyle("width:70px;")

    val dagr = div
    subLeft ac hb("DAGR").plusStyle("width:70px;").withBlue.withActive{
      dagr
    }

    //  subLeft ac hb("FAQ").plusStyle("width:70px;")

    def spacer() = {
      headr.ac(space)
    }


    headr ac hb("")

    spacer() ; headr.ac(hb("Manage"))
    spacer() ; spacer() ;
    headr.appendChild(b)
    spacer() ; spacer() ;

    val email = hb("Welcome: anon")
    spacer() ; headr.ac(email)


    /*
        dbot.ac {
          button("Load").code {
            "@fa"
          }
        }
    */

    def mkDrop(opts: List[String]) =
      ce("select") wc opts.map{q =>
        ce("option").wa("value", q).wt(q)}

    def di = {
      val d = div
      d.style.color = gold
      d.style.fontSize = "16px"
      d.style.paddingRight = "15px"
      d
    }

    def mkFields = mkDrop(List())

    val sep = "\n___\n"
    response.foreach{
      _.foreach{
        z =>

      }
    }

    val propHeaders = Var(List[String]())
    val hdrEl = scala.collection.mutable.MutableList[HTMLElement]()

    val dhead = div
    appendBody(dhead)
  }


  def space: HTMLElement = {
    hb("                    ")
  }

  def processBridge(bridge: String) = {
    OAuth catchRedirect()
    debugInit()
    bridge
  }

}