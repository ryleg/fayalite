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
        println("XHR response", text.toSeq)
        callback(text)
      } // else println("XHR response failure")
    }
    xhr.open("GET", "files")
    xhr.send()
  }

}
