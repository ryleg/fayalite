package org.fayalite.agg

import fa.Schema.Cookie

/**
  * Created by aa on 3/3/2016.
  */
trait CrawlerLike {

  /**
    * Get the full HTML source of an active page somehow
    *
    * @return : Literal string of HTML as though a file
    */
  def getPageSource : String

  /**
    * Initiate a driver to grab a url
    *
    * @param u : URL to navigate to *Consider changing to
    *          a page trait from some established lib
    */
  def navigateToURL(u: String) : Unit

  /**
    * Cleanup / get rid of driving process
    */
  def shutdown(): Unit

  /**
    * Grab current session cookies as scala friendly object
    * @return : Collection of cookies
    */
  def exportCookies() : List[Cookie]

  /**
    * Initiate action removing cookies from driver
    * in effect allowing future page loads to be as
    * though freshly instantiated
    */
  def clearCookies() : Unit

  /**
    * Make this cookie (on this domain for driver)
    * active for future page loads
    * @param c : Cookie from standard schema
    */
  def setCookieAsActive(c: Cookie): Unit

}
