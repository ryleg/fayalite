package org.fayalite.ui

import org.fayalite.util.RemoteAkkaUtils._
import org.fayalite.repl.REPL._
import org.fayalite.util.RemoteClient

import scala.util.Try

object ParseClient {
    def parseClient() ={
        new RemoteClient(defaultPort+51)
    }
}
