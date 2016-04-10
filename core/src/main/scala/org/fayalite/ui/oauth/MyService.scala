package org.fayalite.ui.oauth

import java.io.FileWriter

import akka.actor.Actor
import spray.routing._
import spray.http._
import MediaTypes._
import dispatch._
import Defaults._
import scala.concurrent.Await
import scala.concurrent.duration._

// TODO : Remember what this was being used for
// For real

// we don't implement our route structure directly in the service actor because
// we want to be able to test it independently, without having to spin up an actor
class MyServiceActor extends Actor with MyService {

  // the HttpService trait defines only one abstract member, which
  // connects the services environment to the enclosing actor or test
  def actorRefFactory = context

  // this actor only runs our route, but you could add
  // other things here, like request stream processing
  // or timeout handling
  def receive = runRoute(myRoute)
}
import org.json4s._
import org.json4s.jackson.JsonMethods._

// this trait defines our service behavior independently from the service actor
trait MyService extends HttpService {

  def append(msg: String) = {
    val fw = new FileWriter(s"log.txt", true)
    try {
      fw.write(msg + "\n")
    }
    finally fw.close()
  }

  implicit val formats = DefaultFormats

  val myRoute = {
    path("slinky_oauth_callback") {
      get {
        getFromResource("oauth.html")
      }
    } ~
      path("slinky_catch_oauth") {
        get {
//          parameters('state, 'access_token, 'token_type, 'expires_in) { (state, access_token, token_type, expires_in) =>
          parameters('access_token) { (access_token) =>
            complete(
              HttpResponse(entity =
                performGoogleOAuthRequest(access_token)
            )
            )
          }
        }
      }
}
/*
http://localhost:8080/slinky_catch_oauth/?
state=/slinky&
access_token=ya29.uADw2_5_1_MQmAldzC8RYQWMugis7w6Fe6WI1jAursBtIbjuThP0kSiqfSHm--s8CZ2X1CU8spktgQ
&token_type=Bearer
&expires_in=3600

https://accounts.google.com/o/oauth2/auth?response_type=token&client_id=978142080736-jp2h3frujj891vnjh4il2ac0j59dbm11.apps.googleusercontent.com&redirect_uri=http://ec2-54-165-20-153.compute-1.amazonaws.com/slinky_oauth_callback&scope=email%20profile&state=%2Fslinky&output=embed

http://localhost:8080/slinky_catch_oauth/?state=/slinky&access_token=ya29.uADw2_5_1_MQmAldzC8RYQWMugis7w6Fe6WI1jAursBtIbjuThP0kSiqfSHm--s8CZ2X1CU8spktgQ&token_type=Bearer&expires_in=3600
54.165.20.153
https://accounts.google.com/o/oauth2/auth?response_type=token&client_id=978142080736-jp2h3frujj891vnjh4il2ac0j59dbm11.apps.googleusercontent.com&redirect_uri=http://localhost:8080/slinky_oauth_callback&scope=email%20profile&state=%2Fslinky&output=embed
 */
  def performGoogleOAuthRequest(access_token: String) = {
      val myRequest = url(s"https://www.googleapis.com/oauth2/v1/tokeninfo?access_token=$access_token")
      val request = Http(myRequest.GET);
      val response = Await.result(request, 10 seconds)
      response.getResponseBody
  }


}