package org.fayalite.repl


import java.io.{InputStreamReader, BufferedReader, PipedOutputStream, PipedInputStream}

import scala.tools.nsc.interpreter.JPrintWriter


class REPLManagerLike extends java.io.Serializable {

  val replInputSource = new PipedInputStream()
  val replInputSink = new PipedOutputStream(replInputSource)
  val br = new BufferedReader(new InputStreamReader(replInputSource, "UTF-8"))
  val replOutputSink = new PipedOutputStream()
  val replOutputSource = new PipedInputStream(replOutputSink)
  val pw = new JPrintWriter(replOutputSink)

  var allHistory : String = ""
  def read() : String = {
    var output = ""
    var bytesRead = 0
    do {
      bytesRead = replOutputSource.available()
      val buffer = new Array[Byte](bytesRead)
      replOutputSource.read(buffer)
      output += buffer.map {
        _.toChar
      }.mkString("")
    } while (bytesRead > 0)
    allHistory += output
    output
  }
}