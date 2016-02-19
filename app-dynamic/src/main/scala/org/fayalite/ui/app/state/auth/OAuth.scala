package org.fayalite.ui.app.state.auth

import org.scalajs.dom._

import scala.util.Random


object OAuth {

  // UPDATE TO REFLECT URL PROPERLY
  // requires integration between workbench / standard
  // spray serving.


  val rdr = "http://" + window.location.host

  def catchRedirect() = {
    val isCatch = window.location.href.contains("access")
    if (isCatch) {
      println("reloading due to oauth catch url with cookies " + document.cookie)
    //  alert(document.cookie)
      window.location.href = rdr
    }
    isCatch
  }

  // TODO: Pickup from configs or DB
  val clientId = "978142080736-jp2h3frujj891vnjh4il2ac0j59dbm11.apps.googleusercontent.com"
  val redirectURI = rdr + "/oauth_callback"
  val uido = Random.nextLong().toString

  val redirect = () => {
    window.location.href = getURL()
  }

  // Pass an optional userId generated in here to the redirectUri?
  def getURL() = {
    "https://accounts.google.com/o/" +
      "oauth2/auth?response_type=token&client_id=" +
      s"$clientId" +
      s"&redirect_uri=$redirectURI" +
      s"&scope=email%20profile&output=embed" //&state=%2Ffayalite
  }

}
