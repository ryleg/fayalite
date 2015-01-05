package org.fayalite.repl

/**
 * Created by ryle on 12/10/2014.
 */
import org.fayalite.util.RemoteAkkaUtils


object AkkaClient {
  def main(args: Array[String]) {
    RemoteAkkaUtils.clientInitialize()

  }
}
