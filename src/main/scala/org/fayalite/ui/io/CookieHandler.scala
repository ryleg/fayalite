package org.fayalite.ui.io

object CookieHandler {

 def splitCookies(cookies: String) = {
      cookies.split(";").map {
        _.split("=").map {
          _.trim
        } match {
          case Array(x, y) => Some((x, y))
          case Array() => None
        }
      }.flatMap{ x => x}
    }
}
