package org.fayalite.repl



import org.json4s.jackson.JsonMethods._
import org.json4s.{Extraction, JsonAST, DefaultFormats}
import org.json4s.JsonDSL._
import org.json4s._


object JSON {

  implicit val formats = DefaultFormats

  implicit def replMessageToActualMessage(replMessage: REPLMessage) : REPLInstruction = {
    replMessage.instruction match {
      case "read" => REPLRead()
      case "write" => REPLWrite(replMessage.text)
      case "history" => REPLHistory(replMessage.depth)
      case "writeHistory" => REPLWriteHistory(replMessage.depth)
    }
  }


  trait REPLInstruction
  case class REPLOutput(text: String)
  case class REPLWrite(text: String) extends REPLInstruction
  case class REPLRead() extends REPLInstruction
  case class REPLHistory(depth: Int = 1000000) extends REPLInstruction
  case class REPLWriteHistory(depth: Int = 1000000) extends REPLInstruction

  case class REPLMessage(
                          instruction: String,
                          text: String = "",
                          depth: Int = Int.MaxValue
                          )

  def messageParse(message: String) = {
    val msg : REPLInstruction = parse(message).extract[REPLMessage]
    msg
  }

  def caseClassToJson(message: Any) = {
    implicit val formats = DefaultFormats

    compact(render(Extraction.decompose(message)))
  }

  def main(args: Array[String]) {
    val rw = REPLMessage(instruction="read", text="asdf", depth=100)
    val rws = caseClassToJson(rw)
    println(rws)
    println(messageParse(rws))

  }

}
