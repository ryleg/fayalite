package org.fayalite.sjs.auth

import org.scalajs.dom._

import scala.util.Random


object OAuth {

  // UPDATE TO REFLECT URL PROPERLY
  // requires integration between workbench / standard
  // spray serving.


  val hostRedirectURL = "http://" + window.location.host

  /**
    * Check if we're on an oauth redirect page by checking
    * URL for string 'access'
    * @return : If this page load is for an oauth redirect
    *         or the main application
    */
  def catchRedirect() = {
    val isCatch = window.location.href.contains("access")
    if (isCatch) {
      println("reloading due to oauth catch url with cookies " + document.cookie)
    //  alert(document.cookie)
      window.location.href = hostRedirectURL
    }
    isCatch
  }

  // TODO: Pickup from configs or DB
  var clientId = ""
  val redirectURI = hostRedirectURL + "/oauth_callback"
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
