package org.fayalite.repl



import impl._
import org.fayalite.util.RemoteAkkaUtils.RemoteActorPath

object REPL extends AkkaExt with FutureExt {

  trait Instruction

  case class Start(clientPort: Int, replId: Int)

  case class Evaluate(code: String, replId: Int, clientResponsePort: Int) extends Instruction

  case class Output(evaluationResult: String ) extends Instruction

}
