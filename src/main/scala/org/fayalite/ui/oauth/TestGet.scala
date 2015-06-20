package org.fayalite.ui.oauth



import dispatch._
import org.json4s.DefaultFormats
import org.json4s.jackson.JsonMethods._
import scala.concurrent.Await
import scala.concurrent.duration._
import scala.util.Try

/**
 * This is supposed to be used for testing OAuth but I don't
 * even remember what's going on here.
 */
object TestGet {
 /* implicit val formats = DefaultFormats

  def main(args: Array[String]) {
val rl = "https://www.googleapis.com/oauth2/v1/tokeninfo?access_token=ya29.uADRqOB0Ny6J1QeaRlcWrSVRMEMAWc5-3mG-Yvp_dtelyBKgWAPLAVg2B9sV7eEGLamDW3aMpiNbIQ"

    val page = url(rl)
    val request = Http(page.GET);
    val response = Await.result(request, 10 seconds);
    println(response.getResponseBody)
  /*  val resp = country.getResponseBody
    val userId = (parse(country.getResponseBody) \\ "user_id").extract[String]
    println(resp)
    println(userId)*/
  }*/
}
