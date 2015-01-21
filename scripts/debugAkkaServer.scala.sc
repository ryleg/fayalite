
import org.fayalite.util.{HackAkkaClient, HackAkkaServer, SparkReference}
SparkReference.getSC
val server = new HackAkkaServer()



import org.fayalite.repl.SparkREPLManager
val dr = new SparkREPLManager(1)
1 to 10 foreach { i =>
  dr.run(s"val x = $i")
}
val clientPort = 1400 + scala.util.Random.nextInt(5000)


val client = new HackAkkaClient(1, port=clientPort)

1 to 10 foreach { i =>
  val result = client.evaluate(s"val x = $i", 1, 1)

  println(result)

}
