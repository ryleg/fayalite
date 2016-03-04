package org.fayalite.agg

import org.openqa.selenium.chrome.{ChromeDriver, ChromeOptions}
import rx._
import rx.ops._
import fa._
import Schema._

import scala.collection.JavaConversions

// TODO : Get this to work in src/main without a strange compiler error.


object ChromeWrapper {

  val opts = new ChromeOptions()
  val userAgent = "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2226.0 Safari/537.36"
  opts.addArguments("user-agent=" + userAgent)
  def mkDriver = new ChromeDriver(opts)

}

/**
  * Basic fixes to make Selenium chrome actually useful. This
  * class should simplify using Selenium for the purposes of
  * testing parsing / aggregation frameworks for data acquisition
  * and/or application design.
  *
  * NOTE: Selenium tests require a binary of ChromeDriver
  * Download and make available during runtime by setting
  * VM opt -Dwebdriver.chrome.driver=/your_path_to/chromedriver-mac32
  *
  */
class ChromeWrapper(
                     startingUrl: Option[String] = None
                   ) extends org.scalatest.selenium.WebBrowser {

  import ChromeWrapper._

  /**
    * This is the entrypoint to browser manipulations.
    * If this crashes this whole class becomes wonky.
    *
    * Try to just throw away instances of this class rather
    * than dealing with errors associating with trying to rebind
    * a new instance of a webdriver into some pre-existing instance
    * of this class.
    */
  implicit val webDriver = mkDriver

  /**
    * Change the browser window size at any time.
    *
    * @param x : Pixels width
    * @param y : Pixels height
    */
  def size(x: Int, y: Int) = {
    webDriver.manage().window.setSize(new org.openqa.selenium.Dimension(x, y))
  }

  /**
    * Otherwise the window will start out super huge,
    * this doesn't initialize the window at one size
    * but rather forces it to change so it will on start-up
    * appear huge but should dissapear right quickly
    */
  size(800, 600)

  /**
    * Tracked version of go to or driver.get
    *
    * @param tou : To url by string
    */
  def goto(tou: String) = {
    go to tou
    numVisits() += 1
  }

  /**
    * HTML src code including stuff that only renders
    * after javascript engine executes
    *
    * @return : String of src code, save it somewhere
    */
  def src = pageSource

  /**
    * Closes the window / stops the driver process
    */
  def stop() = close

  /**
    * This deletes all cookies in the current active driver
    * session
    */
  def clearCookies() = delete all cookies

  /**
    * Pretty convenient way to dump all driver
    * cookies into a native Scala format, if you need
    * or desire more control / information make another
    * method using below as an example
    *
    * @return : Scala proper cookies
    */
  def dumpCookies = { // Move to implicits
    import JavaConversions._
    webDriver.manage().getCookies.iterator().toList.flatMap{
      q => T{Cookie(q.getName,q.getDomain, q.getPath, q.getValue,
        q.getExpiry.toString)}.toOption
    }
  }

  /**
    * Ignore expiry for now due to ser
    *
    * Note domain must be same as active session
    * otherwise this will throw an error, that could really
    * be fixed by checking domain here but then we need a domain
    * http/https regex etc. To be fixed later
    *
    * @param c : Cookie to add to current live browser session, MAKE SURE
    *          THAT YOU ON ARE THE SAME DOMAIN OTHERWISE YOU WILL THROW
    *          AN ERROR THAT DOESN'T EXPLAIN THIS TO YOU
    */
  def addCookieProper(
                       c: Cookie
                     ) = {
    add cookie(name=c.name, value=c.value, path=c.path, domain=c.domain)
  }

  /**
    * Convenience method for cookie loading according to this library's
    * serialization standard
    *
    * @param s : File, typically .cookies or something in same dir
    */
  def loadCookiesFrom(s: String) = {
    val jc = parseCookiesFromFile(s)
    jc.foreach{ addCookieProper }
  }

  val isStarted = Var(false) // change to monad of switch on/off dag

  /**
    * Use this for reacting off of to determine when
    * the browser has actually started and loaded something
    */
  val started = startingUrl.map{q => F{goto(q)}}

  val numVisits = Var(0)



}
