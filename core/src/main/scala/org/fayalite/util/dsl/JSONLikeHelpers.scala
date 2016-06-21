package org.fayalite.util.dsl

import org.fayalite.util.JSON
import org.json4s.Extraction

import scala.util.Try

/**
  * Makes working with JSON / CSV garbage a lot friendler
  */
trait JSONLikeHelpers {

  /**
    * Quick converter for passing objects
    * into areas expecting JSON
    *
    * @param any : Anything, you better
    *            hope it serializes, otherwise
    *            throws
    * @return : Serialized version of the
    *         json object according
    *         to json4s's idea of how
    *         to serialize, if you need
    *         to register custom serializer
    *         implicits this is where they would
    *         be enacted
    */
  implicit def AnyJSON(any: Any) : String = any.json

  /**
    * Translators for any's to JSON to get around
    * json4s / scala language dynamics patches.
    *
    * @param jsonSerializable: Something you better
    *                        hope serializes
    */
  implicit class SerExt(jsonSerializable: Any) {
    def json = JSON.caseClassToJson(jsonSerializable)
    def tryJson = Try{JSON.caseClassToJson(jsonSerializable)}
    def j = json
    def jsonSave(f: String) = scala.tools.nsc.io.File(f).writeAll(json)
  }

  implicit class CaseJsonCSVCol(jl: List[Any]) {
    def csv(path: String) = {
     // jl.map{_.toKV}.save(path)
    }
  }

  implicit class CaseJsonCSV(jl: Any) {
    def toKV = {
      import JSON.formats
      val dc = Extraction.decompose(jl)
      val mp = dc.extract[Map[String, String]]
      mp : Map[String,String]
    }
  }

}
