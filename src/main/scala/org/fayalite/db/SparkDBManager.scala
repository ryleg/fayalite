/*
package org.fayalite.db


import java.io.FileWriter

import akka.actor.{ActorSystem, Actor, ActorRef}
import akka.io.Tcp.{Write, Received}
import akka.util.{ByteString, Timeout}
import org.apache.spark.Logging
import org.apache.spark.Logging
import org.apache.spark.repl.SparkIMain
import org.apache.spark.repl.SparkIMain
import org.apache.spark.sql.SchemaRDD
import org.fayalite.repl.SparkREPLManager
import org.fayalite.ui.ParseServer
import org.fayalite.ui.oauth.OAuth.{OAuthResponse, OAuthInfo}
import org.fayalite.util._
import org.fayalite.util.RemoteAkkaUtils.RemoteActorPath
import scala.concurrent.Future
import scala.concurrent.duration._
import akka.pattern._
import scala.collection.mutable.{Map => MMap}

import scala.util.{Failure, Success, Try}
import org.fayalite.repl.REPL._

import org.apache.spark.executor.Executor

import scala.reflect.runtime._
import scala.reflect.runtime.{currentMirror => m, universe => ru}

/**
 * This started off as a way to test whether Spark SQL is friendly
 * to modifications of SchemaRDDs
 * It isn't.
 * @param tableS
 * @param evt
 * @tparam T
 */
class SparkDBManager[T <: Product](tableS: String)(implicit evt: ru.TypeTag[T]) {

  val sqlc = SparkRef.sqlContext
  import sqlc._
  //createParquetFile[T](s"$tableS.parquet").registerTempTable(tableS)
  val schemaRDD = table(tableS)
  def insert(selectStatement: Seq[String]) =
    sql(s"INSERT INTO $tableS SELECT ${selectStatement.mkString(",")}")

}

object SparkDBManager{
/*
  SparkRef.getSC

  val sqlc = SparkRef.sqlContext
  import sqlc._

  // unsafe, make temp file.
  var oauthDB = loadLocalStream.sql.setName("oauthDB")

  def queryInfoResponse(info: OAuthInfo): Unit = {
    oauthInsert(info)
  }
  def oauthInsert(
                 oai : OAuthInfo
                   ) = {
    oauthDB = oai.++(oauthDB)
  }

  def queryAccessTokenToEmail(accessToken: String) = {
    Try {
      val q = 'accessToken.startsWith("a")
      oauthDB.where('accessToken.startsWith(accessToken))
        .select('authResponse.getField("email")).collect().toList.map {
        _.getString(0)
      }.headOption
    }.printOpt.flatten
  }

  def loadLocalStream = {
    SparkRef.sc.textFile(
      ParseServer.tempOAuthLocalFileStream).collect()
      .map { ar =>
      implicit val formats = JSON.formats
      JSON.parse4s(ar).extract[OAuthInfo]
    }.toSeq
  }

  case class A(a: String, b: String)
  // Need to update spark versions to get working.
  implicit def getTable(table: String) : SchemaRDD = sqlc.table(table)
  def testTableInsert() = {
    val a = A("q","w").sql


    var c = A("a","b").++(a)

    c = A("a","b").++(c)
    c = A("a","c").++(c)
    c = A("q", "w").++(c)

    println(c.getAll)

  //  println("A".get('a.startsWith("a")).toList)
/*

      val o2 = A("a","b").sql // sqlc.createSchemaRDD(SparkReference.sc.makeRDD(Seq(A("a", "b"))))

      val parq = ParseServer.tempOAuthLocalFileStream+ ".parquet222"
      import scala.sys.process._
      s"rm -rf $parq".!!
      o2.saveAsParquetFile(parq)
      val parquetFile = sqlc.parquetFile(parq)
      parquetFile.registerTempTable("oauth")

    val o3 = A("c", "d").sql
      o3.insertInto("oauth")


    println(sqlc.table("oauth").collect().toList)
*/

/*
    val o21r = SparkReference.sc.textFile(
      ParseServer.tempOAuthLocalFileStream).map { ar =>
      implicit val formats = JSON.formats
      JSON.parse4s(ar).extract[OAuthInfo]
    }
    val r1 = o21r.first().copy(accessToken = "new")

    val o21 = sqlc.createSchemaRDD(o21r)

        val parq2 = ParseServer.tempOAuthLocalFileStream+ ".parquet3"
        import scala.sys.process._
        s"rm -rf $parq2".!!
        o21.saveAsParquetFile(parq2)
        val parquetFile2 = sqlc.parquetFile(parq2).cache()
        parquetFile2.registerTempTable("oauth2")

   println(sqlc.table("oauth2").collect().toList)

    val o33 = sqlc.createSchemaRDD(SparkReference.sc.makeRDD(Seq(r1)))
    o33.insertInto("oauth2")
    parquetFile2.registerTempTable("oauth2")

    println(sqlc.table("oauth2").collect().toList)
    */
  }*/

  def main(args: Array[String]) {


      //import ParseServer._

/*

      val fakeu = UserCredentials(
        "fake", AWSCredentials(Some("yo"), Some("yo"), Some("yo")))

      val fakeu2= UserCredentials(
        "fake", AWSCredentials(Some("yo222"), Some("yo"), Some("yo")))


    val fakeu32= UserCredentials(
      "fake3", AWSCredentials(Some("yo222"), Some("yo"), Some("yo")))

    println(queryUser(fakeu))

      addUpdateUser(fakeu)
    println(users.collect().toList)
    addUpdateUser(fakeu32)

    addUpdateUser(fakeu2)*/
   // println(ParseServer.users.collect().toList)
//    println(oauthDB.collect().toList)



    //  testTableInsert()
    implicit val formats = JSON.formats
    val sampOAI = JSON.parse4s(rawJs).extract[OAuthInfo]
    oauthInsert(sampOAI)
    //println(sampOAI.sql.collect().toList)
 //   createParquetSchema()
   // println(oauthDB.collect().toList)
 //   sampOAI.insert("oauth")
 //   def getT = SparkReference.sqlContext.table("oauth")
  //  println(getT.collect().toList)
//    println(getT.count())
    println(queryAccessTokenToEmail(atk))

    Thread.sleep(Long.MaxValue)*/

    //new RemoteClient().getServerRef(20000) !
    /*
    val trp = SparkReference.sc.textFile(
      ParseServer.tempOAuthLocalFileStream).collect().head
      println (trp)
      //.map{ar =>
      implicit val formats = JSON.formats

      val response = JSON.parse4s(trp).extract[OAuthInfo]
      //response.insert("oauth")
      //println(response)


      response.insert("oauth")*/

   //

    /*
  //  oauthDB.registerTempTable("oauth")
    println(oauthDB.collect().toList.map{_.toList})
    println(oauthDB.schema.fieldNames)
    oauthDB.printSchema()
    println(oauthDB.select('authResponse.getField("email")).collect().toList.map{_.toList})
    val atk2 = oauthDB.select('accessToken).collect().toList.map{_.getString(0)}.head
    println(atk2)
    println(atk2 == atk)
    println(oauthDB.where('accessToken.startsWith(atk)).collect().toList.map{_.toList}) //.select('authResponse).collect().headOption)
*/
  //  println(sqlc.table("oauth"))

  }
    /*
    Deploy({
      case
    }, 16190)
  }*/
}*/
