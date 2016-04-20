package org.fayalite.repl



import org.fayalite.util.dsl._
import org.fayalite.util.RemoteAkkaUtils.RemoteActorPath

/**
 * Try and guess which of these is being used.
 * Honestly don't know. Sorry, not gonna lie.
 */
object REPL extends AkkaExt with CommonMonadExt {

  trait Instruction

  case class Start(clientPort: Int, replId: Int)

  case class Evaluate(code: String, replId: Int) extends Instruction

  case class Output(evaluationResult: String, originalInstruction: SuperInstruction) extends Instruction

  case class SuperInstruction(code: String, replId: Int, userId: Int, notebookId: Int, clientPort: Int) extends Instruction

  case class ClientRequest(superInstruction: SuperInstruction)

  case class NotebookParams(replId: Int, userId: Int, notebookId: Int)

  case class Heartbeat(clientPort: Int)

  val defaultNotebookParams = NotebookParams(1,1,1)

}