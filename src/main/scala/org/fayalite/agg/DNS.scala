package org.fayalite.agg

import java.net.InetSocketAddress

import akka.actor.ActorSystem
import akka.io.IO
import com.github.mkroli.dns4s.akka.Dns

import fa._

/**
  * Created by aa on 3/10/2016.
  */
object DNS {

  val googleDNS: InetSocketAddress = new InetSocketAddress("8.8.8.8", 53)

  def main(args: Array[String]) {

    implicit val system = ActorSystem("DnsServer")
    import system.dispatcher
    val d = IO(Dns)


    val packet = {
      import com.github.mkroli.dns4s.dsl._
      import com.github.mkroli.dns4s.akka._
      Dns.DnsPacket(
        Query ~ Questions(QName("google.de")),
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


/*


    d.??
*/

}
