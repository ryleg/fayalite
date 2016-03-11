package org.fayalite.repl

import rx.core.Var

import scala.reflect._
import scala.tools.nsc.interpreter._
import scala.tools.nsc.io
import scala.tools.nsc.util.ScalaClassLoader._
import scala.tools.reflect.StdRuntimeTags._

import fa._

/**
  * Created by aa on 3/4/2016.
  */
class NativeREPL {

  def interpret(s: String) = {
    i.intp.interpret(s)
    rml.read()
  }


  val rml = new REPLManagerLike()
  val i = new ILoop(Some(rml.iLoopBufferedReader), rml.iLoopOutputCatch) {
    settings = new CommandLine(List[String]("-usejavacp"), echo).settings
    def betterProcess = {
      savingContextLoader {
        intp = new ILoopInterpreter
        val in0 = SimpleReader(rml.iLoopBufferedReader, rml.iLoopOutputCatch, true)
        // Bind intp somewhere out of the regular namespace where
        // we can get at it in generated code.
        addThunk(intp.quietBind(NamedParam[IMain]("$intp", intp)(tagOfIMain, classTag[IMain])))
        addThunk({
          val autorun = replProps.replAutorunCode.option flatMap (f => io.File(f).safeSlurp())
          if (autorun.isDefined) intp.quietRun(autorun.get)
        })
        // it is broken on startup; go ahead and exit
        if (intp.reporter.hasErrors) println("has errors")
        intp.initializeSynchronous()
        postInitialization()
      }
    }
    betterProcess // THIS STARTS EVERYTHING
  }
}
