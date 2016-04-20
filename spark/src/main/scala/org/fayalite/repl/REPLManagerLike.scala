package org.fayalite.repl

import java.io.{BufferedReader, InputStreamReader, PipedInputStream, PipedOutputStream}

import scala.tools.nsc.interpreter.JPrintWriter

/**
 * In theory this is for a really low level wrapper around the REPL
 * IOStream for elegant patching of the spark REPL.
 * In practice I'm not even sure I still need this? It was mostly for
 * stupid tests.
 */
class REPLManagerLike extends java.io.Serializable {

  val replInputSource = new PipedInputStream()
  val replInputSink = new PipedOutputStream(replInputSource)
  val iLoopBufferedReader = new BufferedReader(new InputStreamReader(replInputSource, "UTF-8"))

  val replOutputSink = new PipedOutputStream()
  val replOutputSource = new PipedInputStream(replOutputSink)
  val iLoopOutputCatch = new JPrintWriter(replOutputSink)

  var allHistory : String = ""

  var onRead = (s: String) => {
    println("REPL Readout: " + s)
  }

  def read() : String = {
    var output = ""
    var bytesRead = 0
    do {
      bytesRead = replOutputSource.available()
      val buffer = new Array[Byte](bytesRead)
      replOutputSource.read(buffer)
      val newRead = buffer.map {
        _.toChar
      }.mkString("")
      if (newRead.nonEmpty) {
        onRead(newRead)
      }

      output += newRead
    } while (bytesRead > 0)
    allHistory += output
    output
  }


}