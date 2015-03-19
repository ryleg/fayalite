package org.fayalite.db


import java.io.FileWriter

import akka.actor.{ActorSystem, Actor, ActorRef}
import akka.io.Tcp.{Write, Received}
import akka.util.{ByteString, Timeout}
import org.apache.spark.Logging
import org.apache.spark.Logging
import org.apache.spark.repl.SparkIMain
import org.apache.spark.repl.SparkIMain
import org.fayalite.repl.SparkREPLManager
import org.fayalite.ui.ParseServer
import org.fayalite.ui.oauth.OAuth.OAuthInfo
import org.fayalite.util._
import org.fayalite.util.RemoteAkkaUtils.RemoteActorPath
import scala.concurrent.Future
import scala.concurrent.duration._
import akka.pattern._
import scala.collection.mutable.{Map => MMap}

import scala.util.{Failure, Success, Try}
import org.fayalite.repl.REPL._

import org.apache.spark.executor.Executor


object SparkDBManager{

  case class CreateDB(

                       )

  case class Insert(dbId: Int, rawJson: String)

  SparkReference.getSC
  // unsafe, make temp file.
  val oauthDB = SparkReference.sqlContext.jsonFile(ParseServer.tempOAuthLocalFileStream)

  def getOAuthTable = oauthDB //SparkReference.sqlContext.table("oauth")

  def oauthInsert(
                 oai : OAuthInfo
                   ) = {
    oai.insert("oauth")
  }

  def main(args: Array[String]) {

    val atk = "ya29.OwG-n69XyAEPL7y2F87roQjV853ISpx4QltnyQeF_O1vKi49N9RECqzCC_5hPoHWQYLkd66OKdeTWg"
    val sqlc = SparkReference.sqlContext
    import sqlc._
    println(oauthDB.cache().count())
  //  oauthDB.registerTempTable("oauth")
    println(oauthDB.collect().toList.map{_.toList})
    println(oauthDB.schema.fieldNames)
    oauthDB.printSchema()
    println(oauthDB.select('authResponse.getField("email")).collect().toList.map{_.toList})
    val atk2 = oauthDB.select('accessToken).collect().toList.map{_.getString(0)}.head
    println(atk2)
    println(atk2 == atk)
    println(oauthDB.where('accessToken.startsWith(atk)).collect().toList.map{_.toList}) //.select('authResponse).collect().headOption)
  }
    /*

    Deploy({
      case
    }, 16190)
  }*/
}