package org.fayalite.ui.oauth

import dispatch.{Http, url}
import org.fayalite.util.JSON

import scala.concurrent.{Future, Await}

import org.fayalite.repl.REPL._

import scala.util.Try

object OAuth {

  case class OAuthResponse(
                       //   issued_to: String,
                     //     audience: String,
                          user_id: String,
                   //       scope: String,
                  //        expires_in: Int,
                          email: String
                 //         verified_email: Boolean,
                 //         access_type: String
                            )

  //def initializeOAuthDB

  case class OAuthInfo(accessToken: String, authResponse: OAuthResponse)

  import fa._

  def handleAuthResponse(authResponse : String, accessToken: String) = {
    println("handleAuthResponse \n" + authResponse)
    OAuthInfo(accessToken, OAuthResponse("", ""))// authResponse.json[OAuthResponse])
  }


  def performGoogleOAuthRequest(access_token: String): Future[String] = {
    println("performing oauth request with at : " + access_token )
    val myRequest = url(s"https://www.googleapis.com/oauth2/v1/tokeninfo?access_token=$access_token")
    val request = Http(myRequest.GET)
    val ret = request.map{_.getResponseBody}
    ret.onComplete{q => println("oauth response" + q)}
    ret
  }

}
