package org.fayalite.ui.app.state.auth

import org.scalajs.dom._


object OAuth {

  // UPDATE TO REFLECT URL PROPERLY
  // requires integration between workbench / standard
  // spray serving.
  def oAuthCatch() = {
    val isCatch = window.location.href.contains("access")
    if (isCatch) {
      println("reloading due to oauth catch url")
      window.location.href = "http://localhost:8080"
    }
    isCatch
  }

  // TODO: Pickup from configs or DB
  val clientId = "978142080736-jp2h3frujj891vnjh4il2ac0j59dbm11.apps.googleusercontent.com"
  val redirectURI = "http://localhost:8080/oauth_callback"

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
