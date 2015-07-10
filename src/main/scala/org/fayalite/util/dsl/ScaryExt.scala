package org.fayalite.util.dsl

import org.apache.spark.sql.SchemaRDD
import org.fayalite.util.{JSON, SparkRef}

import scala.reflect.ClassTag
import scala.reflect.runtime._
import scala.reflect.runtime.{currentMirror => m, universe => ru}

/**
 * DANGEROUS!
 * Enter with caution.
 * These things can break a lot of stuff. I'll consider removing
 * it from some of the package level implicit imports if it gets too scary.
 */
trait ScaryExt {

  val sqlc = SparkRef.sqlContext

  implicit def AnyJSON(any: Any) : String = any.json

  implicit class BatchSQL[T <: Product](t: Seq[T]) (implicit evct: ClassTag[T],
                                                evtt: ru.TypeTag[T]){
    def sql = SparkRef.sqlContext.createSchemaRDD(SparkRef.sc.makeRDD(t))
    def insert(table: String, overwrite: Boolean = true) = sql.insertInto(table, overwrite)
  }


  implicit class AnyPrint(a: Any) {
    def p : Unit = println(a.toString)
  }

  implicit class scExt(s: String) {
    def text = {
      SparkRef.sc.textFile(s)
    }
  }

  implicit class sqlTableExt(s: String) {
    import org.apache.spark.sql.catalyst.expressions.Expression
    val table = sqlc.table(s)
    def get(byExpr: Expression) = {
      sqlc.table(s).where(byExpr).collect().toList
    }
    def getAll = {
      table.collect().toList
    }


  }

  implicit class UnsafeSQLPatches(s: SchemaRDD) {
    def mergeU(o: SchemaRDD) = {
      s.unionAll(o)
    }
    def getAll = s.collect().toList
  }

  implicit class SingleSQL[T <: Product](t: T) (implicit evct: ClassTag[T],
                                                evtt: ru.TypeTag[T]){
    val tableName = t.toString.split("\\(")(0)
  //  val targs = evtt.tpe match { case ru.TypeRef(_, _, args) => args }
  //  println(s"type of $t has type arguments $targs")
    def ++(scr : SchemaRDD) = sql.mergeU(scr)

    def rdd = SparkRef.sc.makeRDD(Seq(t))

    def sql = SparkRef.sqlContext.createSchemaRDD(rdd)
    def insert(table: String, overwrite: Boolean = true) = {
      val sq = sql
      val u = sqlc.table(tableName).unionAll(sq)
        u.insertInto(table, overwrite)
    }
    def parquet(parq: String = "tempParquetSave") = {
      import scala.sys.process._
      s"rm -rf $parq".!!
      sql.saveAsParquetFile(parq)
      val parquetFile = sqlc.parquetFile(parq)
      println("Created table: " + tableName)
      parquetFile.registerTempTable(tableName)

    }
  }

  implicit class SerExt(jsonSerializable: Any) {
    def json = JSON.caseClassToJson(jsonSerializable)
    def j = json
  }

}
