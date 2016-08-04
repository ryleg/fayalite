package org.fayalite.gate.server

import spray.routing.StandardRoute

/**
  * Nonstandard completions for routes for ease of use
  */
trait RoutingCompletions extends spray.routing.Directives {

  /**
    * Allows use of ScalaTags to render HTML
    * and complete a get request with a String of full html
    * for the page render
    *
    * @param html : Full HTML of page to render as if you
    *             read it from index.html
    * @return : Route with response.
    */
  def completeWith(html: String): StandardRoute = {
    import spray.http.MediaTypes._
    respondWithMediaType(`text/html`) & complete {
      html
    }
  }

  /**
    * Return an HTML response encoded with application JSON
    * Useful for direct response to POSTs / simple dumps of a page as json
    * @param rendered : Rendered JSON string
    * @return : Route to serve
    */
  def completeWithJSON(rendered: String): StandardRoute = {
    import spray.http.MediaTypes._
    respondWithMediaType(`application/json`) & complete {
      rendered
    }
  }

  /**
    * Same idea as above but allows spray to complete directly
    * with JS string file contents
    * @param js : File contents of a standard .js as a single string
    * @return : Routing directive allowing that .js to be served
    *         avoiding the use of getFromFile to allow .js to be served
    *         out of a virtual file system / DB
    */
  def completeWithJS(js: String): StandardRoute = {
    import spray.http.MediaTypes._
    respondWithMediaType(`application/javascript`) & complete {
      js
    }
  }

}
