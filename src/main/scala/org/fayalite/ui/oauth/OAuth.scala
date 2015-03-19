package org.fayalite.ui.oauth

import dispatch.{Http, url}
import org.fayalite.util.JSON

import scala.concurrent.Await

import org.fayalite.repl.REPL._

import scala.util.Try

object OAuth {



  /*
  {
 "issued_to": "978142080736-jp2h3frujj891vnjh4il2ac0j59dbm11.apps.googleusercontent.com",
 "audience": "978142080736-jp2h3frujj891vnjh4il2ac0j59dbm11.apps.googleusercontent.com",
 "user_id": "106775157482038906164",
 "scope": "https://www.googleapis.com/auth/userinfo.email https://www.googleapis.com/auth/userinfo.profile",
 "expires_in": 3599,
 "email": "ryledup@gmail.com",
 "verified_email": true,
 "access_type": "online"
}
   */

  case class OAuthResponse(
                          issued_to: String,
                          audience: String,
                          user_id: String,
                          scope: String,
                          expires_in: Int,
                          email: String,
                          verified_email: Boolean,
                          access_type: String
                            )

  //def initializeOAuthDB

  case class OAuthInfo(accessToken: String, authResponse: OAuthResponse)

  def handleAuthResponse(authResponse : String, accessToken: String) = {
    println("handleAuthResponse \n" + authResponse)
    implicit val formats = JSON.formats
    val response = Try{JSON.parse4s(authResponse).extract[OAuthResponse]}.printOpt
    response.map{r => OAuthInfo(accessToken, r)}
  }


  def performGoogleOAuthRequest(access_token: String) = {
    val myRequest = url(s"https://www.googleapis.com/oauth2/v1/tokeninfo?access_token=$access_token")
    val request = Http(myRequest.GET);
    request.map{_.getResponseBody}
  }

}
