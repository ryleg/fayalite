package org.fayalite.util.dsl

import org.fayalite.util.{JSON, SparkReference}

import scala.reflect.ClassTag
import scala.reflect.runtime._
import scala.reflect.runtime.{currentMirror => m, universe => ru}

trait ScaryExt {

  implicit def AnyJSON(any: Any) : String = any.json

  implicit class SingleSQL[T <: Product](t: T) (implicit evct: ClassTag[T],
                                                evtt: ru.TypeTag[T]){
    def sql = SparkReference.sqlContext.createSchemaRDD(SparkReference.sc.makeRDD(Seq(t)))
    def insert(table: String, overwrite: Boolean = true) = sql.insertInto(table, overwrite)
  }

  implicit class SerExt(jsonSerializable: Any) {
    def json = JSON.caseClassToJson(jsonSerializable)
  }

}
