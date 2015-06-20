package org.fayalite.ui.io

/**
 * I'm pretty sure there's a standard library function that does this
 * somewhere else.
 */
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
