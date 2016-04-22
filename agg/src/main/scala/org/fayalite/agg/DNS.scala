package org.fayalite.agg

import java.net.InetSocketAddress

import akka.actor.ActorSystem
import akka.io.IO
import com.github.mkroli.dns4s.akka.Dns
import fa._

/**
  * For querying websites DNS info easily /
  * constructing packets
  *
  * INCOMPLETE - To be revisited later
  */
object DNS {

  val googleDNS: InetSocketAddress = new InetSocketAddress("8.8.8.8", 53)

  def queryName(host: String = "google.de") = {

    implicit val system = ActorSystem("DnsServer")
    val d = IO(Dns)
    val packet = {
      import com.github.mkroli.dns4s.akka._
      import com.github.mkroli.dns4s.dsl._
      Dns.DnsPacket(
        Query ~ Questions(QName(host)),
        googleDNS
      )
    }

    import akka.pattern.ask
    val r = ask(d, packet)
    import com.github.mkroli.dns4s.dsl._
    r.onSuccess {
      case Response(Answers(answers)) =>
        answers.collect {
          case ARecord(arecord) => println(arecord.address.getHostAddress)
        }
    }(ecc)

  }

}
