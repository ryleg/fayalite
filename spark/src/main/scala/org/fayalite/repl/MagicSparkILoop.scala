package org.fayalite.repl


import java.io.{BufferedReader, File}

import org.apache.spark.{SparkConf, UtilsAux}
import org.apache.spark.repl.SparkILoop

import scala.concurrent._
import scala.reflect._
import scala.tools.nsc.{GenericRunnerSettings, Properties, Settings, io}
import scala.tools.nsc.interpreter._
import scala.reflect.internal.util.ScalaClassLoader.savingContextLoader
import scala.tools.nsc.interpreter.StdReplTags._

/**
  * Created by aa on 4/19/2016.
  */
class MagicSparkILoop(in0: Option[BufferedReader], out: JPrintWriter)
  extends SparkILoop(in0, out) {

  // Removed due to requirement of rebinding SC
  // into repl dynamically
  override def initializeSpark() = {}
  override def printWelcome() = {}

  private var globalFuture: Future[Boolean] = _


  private def pathToPhaseWrapper = intp.originalPath("$r") + ".phased.atCurrent"

  private def phaseCommand(name: String): Result = {
    val phased: Phased = power.phased
    import phased.NoPhaseName

    if (name == "clear") {
      phased.set(NoPhaseName)
      intp.clearExecutionWrapper()
      "Cleared active phase."
    }
    else if (name == "") phased.get match {
      case NoPhaseName => "Usage: :phase <expr> (e.g. typer, erasure.next, erasure+3)"
      case ph          => "Active phase is '%s'.  (To clear, :phase clear)".format(phased.get)
    }
    else {
      val what = phased.parse(name)
      if (what.isEmpty || !phased.set(what))
        "'" + name + "' does not appear to represent a valid phase."
      else {
        intp.setExecutionWrapper(pathToPhaseWrapper)
        val activeMessage =
          if (what.toString.length == name.length) "" + what
          else "%s (%s)".format(what, name)

        "Active phase is now: " + activeMessage
      }
    }
  }

  private def unleashAndSetPhase() {
    if (isReplPower) {
      power.unleash()
      // Set the phase to "typer"
      intp beSilentDuring phaseCommand("typer")
    }
  }

  private def magicLoopPostInit() {
    // Bind intp somewhere out of the regular namespace where
    // we can get at it in generated code.
    intp.quietBind(NamedParam[IMain]("$intp", intp)(tagOfIMain, classTag[IMain]))
    // Auto-run code via some setting.
    ( replProps.replAutorunCode.option
      flatMap (f => io.File(f).safeSlurp())
      foreach (intp quietRun _)
      )
    // classloader and power mode setup
    intp.setContextClassLoader()
    if (isReplPower) {
      replProps.power setValue true
      unleashAndSetPhase()
      asyncMessage(power.banner)
    }
    // SI-7418 Now, and only now, can we enable TAB completion.
    in.postInit()
  }

  def magicProcess(settings: Settings) = savingContextLoader {
    this.settings = settings
    createInterpreter()
    in = in0.fold(chooseReader(settings))(r =>
      SimpleReader(r, out, interactive = true))

    intp.initializeSynchronous()
    magicLoopPostInit()
    val intpErr = intp.reporter.hasErrors
    loadFiles(settings)

    try loop() match {
      case LineResults.EOF => out print Properties.shellInterruptedString
      case _               =>
    }
    catch AbstractOrMissingHandler()
    finally closeInterpreter()

    true
  }


}

class SparkREPLManager extends REPLManagerLike {

  def start(
             namePrefix: String = "repl",
             args: Array[String] = Array[String](),
             rootDir: String = ".",
             jars: String = "",
             settingsErrorHandler: (String => Unit) = println
           ) = {

    val outputDir = UtilsAux.createTempDir(rootDir, namePrefix)

    val interpArguments = List(
      "-Yrepl-class-based",
      "-Yrepl-outdir", s"${outputDir.getAbsolutePath}",
      "-classpath", jars
    ) ++ args.toList

    val settings = new GenericRunnerSettings(settingsErrorHandler)
    settings.processArguments(interpArguments, true)

    val iLoop = new MagicSparkILoop(Some(iLoopBufferedReader),
      iLoopOutputCatch)

  }

  start()




}