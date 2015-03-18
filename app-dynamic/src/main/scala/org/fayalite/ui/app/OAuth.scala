package org.fayalite.ui.app

object OAuth {


  val clientId = "978142080736-jp2h3frujj891vnjh4il2ac0j59dbm11.apps.googleusercontent.com"
  val redirectURI = "http://localhost:8080/oauth_callback"

  // Pass an optional userId generated in here to the redirectUri?
  def getURL() = {
    "https://accounts.google.com/o/" +
      "oauth2/auth?response_type=token&client_id=" +
      s"$clientId" +
      s"&redirect_uri=$redirectURI" +
      s"&scope=email%20profile&output=embed" //&state=%2Ffayalite
  }

}
