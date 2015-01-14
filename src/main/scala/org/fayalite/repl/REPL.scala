package org.fayalite.repl



import impl._
import org.fayalite.util.RemoteAkkaUtils.RemoteActorPath

object REPL extends AkkaExt with FutureExt {

  trait Instruction

  case class Initialize(userId: Int, remoteActorPath: RemoteActorPath) extends Instruction

  case class Write(code: String) extends Instruction

  case class Output(evaluationResult: String ) extends Instruction

}
