import scala.tools.nsc.doc.{DocFactory, Settings}
import scala.tools.nsc.doc.model.DocTemplateEntity
import scala.tools.nsc.reporters.ConsoleReporter

/*

 def getTypeTag[T: ru.TypeTag](obj: T) = ru.typeTag[T]
 val theType = getTypeTag(l).tpe
val t = theType
 t.
 t.declarations.foreach{
  q =>
       q.
  //q.annotations.map{_.toString}.foreach{println}
 }
 /*theType: ru.Type = List[Int]
 import scala.reflect.runtime.{universe=>ru}
 l: List[Int] = List(1, 2, 3)
 getTypeTag: [T](obj: T)(implicit evidence$1: ru.TypeTag[T])ru.TypeTag[T]
*/*/
import scala.reflect.runtime.{universe => ru}
import ammonite.ops._
val l = cwd

def extractScaladoc(
                     scalaSourceFiles: scala.List[String]):
Option[DocTemplateEntity] = {
 val settings = new Settings(error => print(error))
 settings.usejavacp.value = true
 val reporter = new ConsoleReporter(settings)
 val docFactory = new DocFactory(reporter, settings)
 val universe = docFactory.makeUniverse(Left{scalaSourceFiles})
 universe.map(_.rootPackage.asInstanceOf[DocTemplateEntity])
}
val dte = extractScaladoc(List("/Users/ryle/Documents/wtf/ammonite/ops/Model.scala"))

def findAtPath(docTemplate: DocTemplateEntity, pathToEntity: List[String]): Option[DocTemplateEntity] = {
 pathToEntity match {
  case Nil => None
  case pathPart :: Nil => docTemplate.templates.find(_.name == pathPart)
  case pathPart:: remainingPathPart => {
   docTemplate.templates.find(_.name == pathPart) match {
    case Some(childDocTemplate) => findAtPath(childDocTemplate, remainingPathPart)
    case None => None
   }
  }
 }
}