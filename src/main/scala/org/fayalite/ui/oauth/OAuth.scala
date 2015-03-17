package org.fayalite.ui.oauth

import dispatch.{Http, url}

import scala.concurrent.Await

object OAuth {

  def handleAuthResponse(authResponse : String) = {
    println("handleAuthResponse \n" + authResponse)
  }


  def performGoogleOAuthRequest(access_token: String) = {
    val myRequest = url(s"https://www.googleapis.com/oauth2/v1/tokeninfo?access_token=$access_token")
    val request = Http(myRequest.GET);
    request.map{_.getResponseBody}
  }

}
