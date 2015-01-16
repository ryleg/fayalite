
import org.fayalite.util.{HackAkkaServer, SparkReference}
SparkReference.getSC
val server = new HackAkkaServer()

import org.fayalite.repl.SparkREPLManager
val dr = new SparkREPLManager(1)

dr.run("val x = 1")