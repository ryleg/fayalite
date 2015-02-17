package org.fayalite.util

import akka.actor.{Props, Actor, ActorSystem}
import com.typesafe.config.ConfigFactory
import org.apache.spark.Logging
import org.fayalite.repl.REPL._
import akka.pattern.ask
import org.fayalite.ui.ClientMessageToResponse
import scala.tools.nsc.interpreter
import scala.util.{Failure, Success, Try}



object RemoteAkkaUtils extends Logging {

  def main(args: Array[String]) {

    Thread.sleep(Long.MaxValue)
  }

  val serverActorSystemName = "FayaliteServer"
  val clientActorSystemName = "FayaliteClient"

  val serverActorName = "FayaliteMultiplex"
  val clientActor = "FayaliteREPLClient"

  val defaultHost = "127.0.0.1"
  val defaultPort = 16180


  def clientInitialize(
                        host: String = defaultHost,
                        port: Int = defaultPort + 1
                        ) = {

    implicit val actorSystem = createActorSystem(clientActorSystemName, host, port)
    implicit val rap = RemoteActorPath()
    val server = getActor()
    server
  }

  def serverInitialize(host: String = defaultHost, port: Int = defaultPort) = {
    val actorSystem = createActorSystem(serverActorSystemName, defaultHost, defaultPort)
    //actorSystem.actorOf(Props(new REPLHandler(), name=serverActorName)
  }

  def createActorSystem(name: String = serverActorSystemName,
                        host: String = defaultHost,
                        port: Int = defaultPort
                         ) = {


    val akkaThreads = 4
    val akkaBatchSize = 15
    val akkaTimeout = 100
    val xKB = 500
    val akkaFrameSize = xKB * 1024 * 1024
    val akkaLogLifecycleEvents = false
    val lifecycleEvents = "on"
    val logAkkaConfig = "on"
    val akkaHeartBeatPauses = 600
    val akkaFailureDetector = 300.0
    val akkaHeartBeatInterval = 1000
    val requireCookie = false
    val secureCookie = ""
    val akkaConf =
      ConfigFactory.parseString(
        s"""
      |akka.daemonic = on
      |akka.loggers = [""akka.event.slf4j.Slf4jLogger""]
      |akka.stdout-loglevel = "ERROR"
      |akka.jvm-exit-on-fatal-error = off
      |akka.remote.require-cookie = "$requireCookie"
      |akka.remote.secure-cookie = "$secureCookie"
      |akka.remote.transport-failure-detector.heartbeat-interval = $akkaHeartBeatInterval s
      |akka.remote.transport-failure-detector.acceptable-heartbeat-pause = $akkaHeartBeatPauses s
      |akka.remote.transport-failure-detector.threshold = $akkaFailureDetector
      |akka.actor.provider = "akka.remote.RemoteActorRefProvider"
      |akka.remote.netty.tcp.transport-class = "akka.remote.transport.netty.NettyTransport"
      |akka.remote.netty.tcp.hostname = "$host"
      |akka.remote.netty.tcp.port = $port
      |akka.remote.netty.tcp.tcp-nodelay = on
      |akka.remote.netty.tcp.connection-timeout = $akkaTimeout s
      |akka.remote.netty.tcp.maximum-frame-size = ${akkaFrameSize}B
      |akka.remote.netty.tcp.execution-pool-size = $akkaThreads
      |akka.actor.default-dispatcher.throughput = $akkaBatchSize
      |akka.log-config-on-start = $logAkkaConfig
      |akka.remote.log-remote-lifecycle-events = $lifecycleEvents
      |akka.log-dead-letters = $lifecycleEvents
      |akka.log-dead-letters-during-shutdown = $lifecycleEvents
      """.stripMargin)

    logInfo(s"Create actor system on port $port")
    val actorSystem = ActorSystem(name, akkaConf)

    actorSystem
  }

  case class RemoteActorPath(
                              host: String = defaultHost,
                              port: Int = defaultPort,
                              remoteActorSystemName: String = serverActorSystemName,
                              remoteActorName: String = serverActorName
                              )

  def getActor()(
    implicit localActorSystem: ActorSystem,
    remoteActorPath: RemoteActorPath
    ) = {
    val actorPath = s"akka.tcp://${
      remoteActorPath.remoteActorSystemName}" +
      s"@${remoteActorPath.host}:${remoteActorPath.port}/user/" +
      s"${remoteActorPath.remoteActorName}"

    val actor = localActorSystem.actorSelection(actorPath).resolveOne()
    val actorRef = actor.get
    actorRef
  }

}
