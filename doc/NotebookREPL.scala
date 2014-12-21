package org.fayalite.repl

/*
 * Copyright (c) 2013  Bridgewater Associates, LP
 *
 * Distributed under the terms of the Modified BSD License.  The full license is in
 * the file COPYING, distributed as part of this software.
 */

import java.io.{PipedOutputStream, StringWriter, PrintWriter, ByteArrayOutputStream}
import org.apache.spark.repl.{SparkJLineCompletion, HackSparkILoop, SparkILoop}
import scala.tools
import tools.nsc.Settings
import _root_.scala.tools.nsc.interpreter._
import tools.nsc.interpreter.Completion.{Candidates, ScalaCompleter}
import tools.jline.console.completer.{ArgumentCompleter, Completer}
import tools.nsc.interpreter.Completion.Candidates
import tools.nsc.interpreter.Results.{Incomplete => ReplIncomplete, Success => ReplSuccess, Error}
import scala.Either
import java.util.ArrayList
import collection.JavaConversions._
import xml.{NodeSeq, Text}
import collection.JavaConversions
import java.net.{URLDecoder, JarURLConnection}
import scala.util.control.NonFatal
import scala.tools.nsc.interpreter.{JPrintWriter}

class NotebookREPL(stdout: JPrintWriter, stdoutBytes: PipedOutputStream, compilerOpts: List[String] = List(), val jars:List[String]=Nil) {

   var loop:SparkILoop = _

  var classServerUri:Option[String] = None

  val interp:org.apache.spark.repl.SparkIMain = {
    val settings = new Settings
    settings.embeddedDefaults[NotebookREPL]
    if (!compilerOpts.isEmpty) settings.processArguments(compilerOpts, false)

    // TODO: This causes tests to fail in SBT, but work in IntelliJ
    // The java CP in SBT contains only a few SBT entries (no project entries), while
    // in intellij it has the full module classpath + some intellij stuff.
    settings.usejavacp.value = true
    // println(System.getProperty("java.class.path"))
    //val i = new HackIMain(settings, stdout)
    loop = new HackSparkILoop(stdout)
    jars.foreach { jar =>
      import scala.tools.nsc.util.ClassPath
      val f = scala.tools.nsc.io.File(jar).normalize
      loop.addedClasspath = ClassPath.join(loop.addedClasspath, f.path)
    }

    loop.process(settings)
    val i = loop.intp
    //i.initializeSynchronous()
    classServerUri = Some(i.classServer.uri)
    i
  }

/*  private lazy val completion = {
    //new JLineCompletion(interp)
    new SparkJLineCompletion(interp)
  }

  private def scalaToJline(tc: ScalaCompleter): Completer = new Completer {
    def complete(_buf: String, cursor: Int, candidates: JList[CharSequence]): Int = {
      val buf   = if (_buf == null) "" else _buf
      val Candidates(newCursor, newCandidates) = tc.complete(buf, cursor)
      newCandidates foreach (candidates add _)
      newCursor
    }
  }*/

/*  private lazy val argCompletor = {
    val arg = new ArgumentCompleter(new JLineDelimiter, scalaToJline(completion.completer()))
    // turns out this is super important a line
    arg.setStrict(false)
    arg
  }

  private lazy val stringCompletor = StringCompletorResolver.completor

  private def getCompletions(line: String, cursorPosition: Int) = {
    val candidates = new ArrayList[CharSequence]()
    argCompletor.complete(line, cursorPosition, candidates)
    candidates map { _.toString } toList
  }*/

  /**
   * Evaluates the given code.  Swaps out the `println` OutputStream with a version that
   * invokes the given `onPrintln` callback everytime the given code somehow invokes a
   * `println`.
   *
   * Uses compile-time implicits to choose a renderer.  If a renderer cannot be found,
   * then just uses `toString` on result.
   *
   * I don't think this is thread-safe (largely because I don't think the underlying
   * IMain is thread-safe), it certainly isn't designed that way.
   *
   * @param code
   * @param onPrintln
   * @return result and a copy of the stdout buffer during the duration of the execution
   */
  def evaluate(code: String, onPrintln: String => Unit = _ => ()) = {
    stdout.flush()
        val res = Console.withOut(stdoutBytes) {
          interp.interpret(code)
        }
    stdout.flush()
    res
  }

/*  def addCp(newJars:List[String]) = {
    val prevCode = interp.prevRequestList.map(_.originalLine)
    val r = new NotebookREPL(compilerOpts, newJars:::jars)
    (r, () => prevCode.drop(7/*init scripts... → UNSAFE*/) foreach (c => r.evaluate(c, _ => ())))
  }*/

/*  def complete(line: String, cursorPosition: Int): (String, Seq[Match]) = {
    def literalCompletion(arg: String) = {
      val LiteralReg = """.*"([\w/]+)""".r
      arg match {
        case LiteralReg(literal) => Some(literal)
        case _ => None
      }
    }

    // CY: Don't ask to explain why this works. Look at JLineCompletion.JLineTabCompletion.complete.mkDotted
    // The "regularCompletion" path is the only path that is (likely) to succeed
    // so we want access to that parsed version to pull out the part that was "matched"...
    // ...just...trust me.
    val delim = argCompletor.getDelimiter
    val list = delim.delimit(line, cursorPosition)
    val bufferPassedToCompletion = list.getCursorArgument
    val actCursorPosition = list.getArgumentPosition
    val parsed = Parsed.dotted(bufferPassedToCompletion, actCursorPosition) // withVerbosity verbosity
    val matchedText = bufferPassedToCompletion.takeRight(actCursorPosition - parsed.position)

    literalCompletion(bufferPassedToCompletion) match {
      case Some(literal) =>
        // strip any leading quotes
        stringCompletor.complete(literal)
      case None =>
        val candidates = getCompletions(line, cursorPosition)

        (matchedText, if (candidates.size > 0 && candidates.head.isEmpty) {
          List()
        } else {
          candidates.map(Match(_))
        })
    }
  }*/

/*  def objectInfo(line: String): Seq[String] = {
    // CY: The REPL is stateful -- it isn't until you ask to complete
    // the thing twice does it give you the method signature (i.e. you
    // hit tab twice).  So we simulate that here... (nutty, I know)
    getCompletions(line, line.length)
    val candidates = getCompletions(line, line.length)

    if (candidates.size >= 2 && candidates.head.isEmpty) {
      candidates.tail
    } else {
      Seq.empty
    }
  }*/
}
