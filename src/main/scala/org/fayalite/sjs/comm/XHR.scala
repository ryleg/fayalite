package org.fayalite.sjs.comm

import org.scalajs.dom._
import org.scalajs.dom.raw.XMLHttpRequest


/**
  * REST API request testing / formatting.
  */
object XHR {

  /**
    * Placeholder for testing REST requests
    * @param callback : How to process result of
    *                 file request.
    */
  def requestTopLevelFiles(callback: Array[String] => Unit) = {
    val xhr = new XMLHttpRequest()
    xhr.onreadystatechange = (e: Event) => {
      if (xhr.readyState == 4 && xhr.status == 200) {
        val text = {
          import upickle._
          read[Array[String]](xhr.responseText)
        }
      //  println("XHR response", text.toSeq)
        callback(text)
      } // else println("XHR response failure")
    }
    xhr.open("GET", "files")
    xhr.send()
  }

  def post[W: upickle.Writer, R: upickle.Reader]
  (
    payload: W,
    callback: R => Unit,
    path: String
  ) = {
    val xhr = new XMLHttpRequest()
    xhr.onreadystatechange = (e: Event) => {
      if (xhr.readyState == 4 && xhr.status == 200) {
        val text = {
          import upickle._
       //   println("Response text", xhr.responseText)
          read[R](xhr.responseText)
        }
        callback(text)
      }
    }
    xhr.open("POST", path)
    xhr.setRequestHeader("Content-Type", "application/json")
    val out = {
      import upickle._
      write(payload)
    }
    println("POST", out)
    xhr.send(out)
  }


  /*
  var xmlhttp = new XMLHttpRequest();   // new HttpRequest instance
xmlhttp.open("POST", "/json-handler");
xmlhttp.setRequestHeader("Content-Type", "application/json;charset=UTF-8");
xmlhttp.send(JSON.stringify({name:"John Rambo", time:"2pm"}));
   */


}
