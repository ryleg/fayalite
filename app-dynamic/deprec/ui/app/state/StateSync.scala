package org.fayalite.ui.app.state

import org.fayalite.ui.app.canvas.{Canvas, Schema}
import org.fayalite.ui.app.comm.{Disposable, PersistentWebSocket}
import org.fayalite.ui.app.manager.Editor
import org.fayalite.ui.app.state.auth.OAuth
import org.fayalite.ui.app.text.CellManager
import org.scalajs.dom
import org.scalajs.dom.Event
import org.scalajs.dom._
import org.scalajs.dom.raw.DataTransfer
import org.scalajs.dom.raw.Element
import org.scalajs.dom.raw.File
import org.scalajs.dom.raw.FileList
import org.scalajs.dom.raw.FileReader
import org.scalajs.dom.raw._
import rx._

import scala.scalajs.js.{JSON, Dynamic}
import scala.util.{Random, Try}

class StateSync {

}

object StateSync {

  val parsedMessage = Var(null.asInstanceOf[Dynamic])
  val meta = Var(null.asInstanceOf[Response])

  case class FileIO(name: String, contents: String)
  case class IdIO(id: Int, io: String)
  case class RIO(asyncOutputs: Array[String], asyncInputs: Array[IdIO])
  case class Response(
                       classRefs: Option[Array[String]],
                       files: Option[Array[FileIO]],
                       replIO: Option[RIO]
                       )

  case class ParseRequest (
                          code: String,
                          cookies: String,
                          requestId: String
                      //    pollOutput: Boolean = true
                            )

  def initializeApp() = {

    CellManager.onLoad()
    Canvas.initCanvas()
    println(Input.t)
    println(Editor.editor)
    // EXPERIMENTAL BELOW
     val resp = Disposable.send("yo")
    import rx.ops._
    resp.foreach{q =>
      println("yo response: " + q)
      Try{
        import upickle._
        meta() = read[Response](q)
      }
      parsedMessage() = JSON.parse(q)
    }
  }
/*

  def code(s: String) = {
    val kvs = ParseRequest(
      s, dom.document.cookie, scala.util.Random.nextInt()
    )
    import upickle._
    val ser = write(kvs)
    import Disposable.send
    println("sent : " + ser)
    send(ser)
  }
*/

  /*
  Wow, theres an un-compilable state here, not sure which
  variation it is, but I tried pulling above into sendCase(a: Any)
  and it never compiled. That or flashRate heartbeats caused it
  to lock forever on fastOptJS
   */

  var codeBuf = ""

  Input.flashRate.foreach{
    e =>
     import upickle._
      val request = ParseRequest(
        code = "@fa" + codeBuf, cookies = dom.document.cookie,
      requestId = scala.util.Random.nextLong().toString
      )
      codeBuf = ""
      val write1: String = write(request)

   //   println("sending parserequest" + write1)
   //   println("sending parserequest" + request)
    //  println("sending parserequest" + dom.document.cookie)
      PersistentWebSocket
        .send(write1)

  }
/*

  def req(codeBf: String) = {
    import upickle._
    val request = ParseRequest(
      code = "@fa" + codeBf, cookies = dom.document.cookie
    , scala.util.Random.nextInt()
    )
    val write1: String = write(request)

   println("sending parserequest" + write1)
    //   println("sending parserequest" + request)
    //  println("sending parserequest" + dom.document.cookie)
    PersistentWebSocket
      .send(write1)

  }
*/

  import rx.ops._


  case class ParseResponse(kvp: scala.Array[String])

  /*

  Make the scala.js code execution flow into a serializable
  dag object using vertices and edges to determine code
  updates according to rules for DAg.

   */

/*
  val listener = Obs(PersistentWebSocket.pws.messageStr, skipInitial = true) {
    val m = PersistentWebSocket.pws.messageStr()
    import upickle._
    response() = read[ParseResponse](m)
  }*/

  val ms = PersistentWebSocket.pws.messageStr


  val responsekvp: Rx[List[(String, String)]] =
    Rx { List[(String, String)]() }

  /*ms.map{
    m =>
    //  println("msgstr" + m )
/*      if (m.contains("meta|")) {
        println(m)
      }*/
      if (m != null && m != "") Try {
        println(m)
        import upickle._
        val ret = read[ParseResponse](m)

        ret.kvp.toList.map{
          q =>
            val jj = q.split('|')
            val valj = if (jj.length > 1) jj.tail.mkString("\\|") else ""
            val valk = if (jj.nonEmpty) jj.head else ""
            valk -> valj
        }
      }.toOption.getOrElse(scala.List[(String, String)]())
      else List()
  }
*/
  def button(s: String) = {
    import org.scalajs.dom
    import scalajs.js._
    // dom.document.body
    val obj: dom.Element = dom.document.createElement("button")
    val tn = dom.document.createTextNode(s)
    obj.appendChild(tn)
    obj.asInstanceOf[HTMLElement]
  }

  implicit class jf (j: Element) {
    def wa(a: String, v: String) = {
      j.setAttribute(a, v)
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
  }.asInstanceOf[HTMLElement].withStyle("color:" + methodOrange)

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
    def ac(c: Element) = e.appendChild(c)
  }
  val txRes = Var("")

  val methodOrange = "color:#FFC66D;"

  def bigMO = "color:#FFC66D;font-size:200%;"
  def bigM2O = "color:#CC7832;font-size:200%;"

  def pairInput(s: (String, String), d: Element) = {
    import org.scalajs.dom
    val (qj, tj) = s
    println("pairinput " + qj + " " + tj)
    import scalajs.js._
    // dom.document.body
    val obj = dom.document.createElement("div")
    val s1 = dom.document.createElement("span")
    val s2 = dom.document.createElement("span")
    obj.appendChild(s1)
    obj.appendChild(s2)
    s1.setAttribute("style", bigMO)
    def mktn: dom.Node = {
      val tn = dom.document.createTextNode(qj)
      s1.appendChild(tn)
    }
    mktn
    val ac = s2 appendChild  _


    obj.wa("style", "padding-bottom:20px;")

    qj match {
      case x if x.contains("Upload") =>
        val aaa: dom.Element = ce("input")
          .wa("type", "file").wa("class", "csv")
        ac(aaa)
        ac(
        {val ee =  ce("input").wa("type", "submit")
          .wa("class", "submit")
          ee.asInstanceOf[HTMLElement].onclick = (_ : Event) => {
            println("ONCLICK")
            val files: dom.FileList = aaa.asInstanceOf[dom.DataTransfer].files
            println("numfiles " + files.length)
            (0 until files.length).foreach{
              it =>
            val i: dom.File = files.item(it)
                     val tz =  i.slice(0, i.size, "text/plain")
           val tx = new dom.FileReader()

             tx.readAsText(tz)
                tx.onload = ( _ : Event)=> {
                  println(tx.result)
                  val s3: String = tx.result.asInstanceOf[String]
                  val h = s3.split("\n").map {
                    _.split(",").toList
                  }.toList
                  println(h)
                  val hh = h.head
                  println(hh)

                  //code(i.name + "|" + s3)
                  val tab = ce("table").wa("style", "width:100%")
                  d.appendChild(cet("div", "-"*20))
                  d.appendChild(cet("div", "First lines in CSV file"))
                  d.appendChild(cet("div", "-"*20))
                  d.appendChild(tab )
                    h.foreach {
                    q =>
                      println(q)
                      val row = tab.withChild(ce("tr"))
                      q.foreach {
                        z =>
                          println(z)
                          row.withChild(cet("td", z))
                      }
                  }
                }
            }
          }
          ee}
        )
/*
      case x if x.contains("Input") =>
        ac (ce("input")
          .wa("type", "text").wa("class", x)
        )
*/

      case _ =>

    }

    //obj.appendChild(tn)
    obj
  }

  def appendBody(s: Element) = {
    dom.document.body.appendChild(s)
  }

  var spawned = false

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
  /*
  But <button> feels better anyway
Even if a <button> doesn't do anything outside of a form without the help of JavaScript, it still feels better for things you can click that do stuff other than change pages. A bogus href link definitely doesn't feel right.
   */

  def debugInit() = {

    document.body.withStyle("background-color:#2B2B2B;z-index:0;margin:0;padding:0;")
    import org.scalajs.dom

    import scalajs.js._
    // dom.docume
    val b = button("Google OAuth Login")
    b.setAttribute("class", "login")
    b.setAttribute("align", "center")
    b.setAttribute("style", "font-size:20px;" + plainButtonStyle + methodOrange)
    b.onclick = (_: Event) => {
      OAuth redirect()
    }

    val headr = div.withStyle(
      s"position:fixed;width:100%;background-color:$ansiDarkGrey")

    headr.appendChild(b)


    appendBody(headr)
   /* responsekvp.foreach { q =>
      //println(q.toList)
      /*
colors


make it so you can do request + server side logic + client side response
all in a single code flow

button -> request -> server -> process it -> send back response ->
interpret and evaluate response
 */

      if (q.length != 0 && !spawned) {
        spawned = true

        b.setAttribute("style", "display: none;")
        val d = dom.document.createElement("div")
        val dd = dom.document.createElement("div")

        dd.appendChild(d)
        d.setAttribute("style", "position:absolute" +
          ";left:5%;top:14%")
        dd.setAttribute("style", "position:relative")
        appendBody(dd)
        //        b.tagName = "input"
        //  b.textContent = "Upload CSV"
        //    b.onclick = (_ : Event) => {

        //    }



        q.foreach {
          j =>
            if (j._1.contains("Upload"))
              d.appendChild(pairInput(j, d))
        }



        val pcc = q.filter {
          _._1.startsWith("CSVs")
        }.headOption.map {
          h =>
            val csvs = h._2
            println(csvs)
            val pc = csvs.split(",")
            pc.toSeq
        }.getOrElse(Seq())

        val se = ce("select").wa("size", pcc.length.toString)
          var opts = List[HTMLElement]()
        d.appendChild(se)
        pcc.foreach { p => se.appendChild {
          val qz = ce("option")
          opts = opts :+ qz
          qz.textContent = p
          qz
        }
        }

        def getSel = opts.collectFirst{
          case jj if jj.asInstanceOf[HTMLOptionElement].selected
        => "req:" + jj.textContent
        }

        se.asInstanceOf[HTMLSelectElement].onclick =
          (_ : MouseEvent) => {
          getSel map req
        }

          //se.asInstanceOf[HTMLSelectElement].value

/*
        d.ac {
          button("Request Metadata Properties")
            .code("CSV|" + getsel)
        }*/



      }


    }*/
    val sjs = ce("input")
      .wa("type", "text")


    val dbot = div
    dbot.ac(sjs)
    document.body.ac {
      dbot.withStyle("position:fixed;bottom:0%;width:100%")
    }
    dbot.ac {
      button("Sync").code {
        "sjs" + sjs.textContent
      }
    }

    val propHeaders = Var(List[String]())
    val hdrEl = scala.collection.mutable.MutableList[HTMLElement]()

    val dhead = div
    appendBody(dhead)
    /*




 */

    Try {
      propHeaders.foreach {
        z => // this is flat foreach really.. monadic join
          z.foreach {
            zz =>
              if (zz.startsWith("req:")) {
                hdrEl.foreach { cc =>
                  dhead.removeChild(cc)
                }
                zz.drop(4).split(",").foreach {
                  q => appendBody {
                    val cc = cet("p", q).withClass("header")
                    hdrEl.+=(cc)
                    cc
                  }
                }
              }
          }
      }

/*
      responsekvp.foreach {
        kvp =>

          if (kvp.length != 0) kvp
            .foreach {
              case (k, v) =>
                println("response kvp " + k + " " + v)
                v match {
                  case q if q.startsWith("meta|") =>
                    q.drop(5) match {
                      case z if z.startsWith("req:") =>
                        propHeaders() = z.drop(4).split(",").toList
                      case _ =>
                    }
                  case _ =>
                }
            }
        /*
        try print ; =alias try print
        tp ; show -aliases - nonaliases -full comment
         */
      }*/
    }

  }
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
    def code(e: String) = this.click { me: MouseEvent =>
      //req(e)
    }
  }




  def processBridge(bridge: String) = {
    OAuth catchRedirect()

   // wtf


    debugInit()


   // initializeApp()
   // code("println(150)")
    bridge
  }

  def wtf: Unit = {
    val stylingb = //"background-color:#A9B7C6;" +
      "position:absolute;left:" +
        "0;top:0;z-index:2;"
    //  document.body.setAttribute("style", stylingb)
    val elem = document.body.getElementsByTagName("canvas")
    val canvas = {
      // {if (elem.length != 0) elem(0) else {
      val obj = dom.document.createElement("canvas")
      val sa = obj.setAttribute("style", stylingb)
      document.body.appendChild(obj)
      obj
    }.asInstanceOf[HTMLCanvasElement]
    val ctx =
      canvas.getContext("2d").asInstanceOf[dom.CanvasRenderingContext2D]

    canvas.width = 100
    canvas.height = 100
    //  ctx.globalAlpha = .3
  }
}