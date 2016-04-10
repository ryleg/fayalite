package org.fayalite.agg

import java.awt.event.KeyEvent
import java.awt.{Toolkit, Robot}
import java.awt.datatransfer.StringSelection

import fa.Schema._
import fa._
import org.fayalite.agg.ProxyManager.ProxyDescr
import org.openqa.selenium.chrome.{ChromeDriver, ChromeOptions}
import org.openqa.selenium.remote.{CapabilityType, DesiredCapabilities}
import rx._

import scala.collection.JavaConversions

object SeleniumChrome {

  /**
    * This works for an authenticated proxy but it forces a
    * browser prompt which is solved by a Robot
    *
    * @param desiredProxy : Ip:port string as expected from proxy lists
    * @param opt : Extra options, defaults to normal to spoof user agent.
    * @return
    */
  def mkProxy(desiredProxy: String, opt: ChromeOptions = getOpts) = {
    import org.openqa.selenium.Proxy
    val prox = new Proxy()
    prox.setHttpProxy(desiredProxy)
    prox.setFtpProxy(desiredProxy)
    prox.setSslProxy(desiredProxy)
    val cap = new DesiredCapabilities()
    cap.setCapability(CapabilityType.PROXY, prox)
    cap.setCapability("chromeOptions", opt)
    cap
  }

  /**
    * This deals with nasty browser authentication window
    * popups that aren't website based.
    *
    * Pretty unsafe but it works at least for a single
    * Selenium window if you're not allowing any other
    * input side effects.
    *
    * THIS HAS ENVIRONMENTAL DEPENDENCIES / SIDE EFFECTS
    * It is not monadic! It messes with system clipboard also
    *
    * // TODO : Fix somehow
    *
    * @param username : HTML username to login with as you would encode
    *                 for a http request
    * @param pass : Password
    */
  def browserLogin(username: String, pass: String) = {
    val user = new StringSelection(username)
    val rb = new Robot()
    Toolkit.getDefaultToolkit().getSystemClipboard().setContents(user, null)
  //  println("set clip")
    rb.keyPress(KeyEvent.VK_CONTROL)
    rb.keyPress(KeyEvent.VK_V)
    rb.keyRelease(KeyEvent.VK_V)
    rb.keyRelease(KeyEvent.VK_CONTROL)
    //tab to password entry field
    rb.keyPress(KeyEvent.VK_TAB)
    rb.keyRelease(KeyEvent.VK_TAB)
    Thread.sleep(600)
    //Enter password by ctrl-v
    val pwd = new StringSelection(pass)
    Toolkit.getDefaultToolkit().getSystemClipboard().setContents(pwd, null)
    rb.keyPress(KeyEvent.VK_CONTROL)
    rb.keyPress(KeyEvent.VK_V)
    rb.keyRelease(KeyEvent.VK_V)
    rb.keyRelease(KeyEvent.VK_CONTROL)
    //press enter
    rb.keyPress(KeyEvent.VK_ENTER)
    rb.keyRelease(KeyEvent.VK_ENTER)
  }

  /**
    * This needs to be set because otherwise you need a -D java
    * option to let Selenium know where the binary for Chrome is
    */
  setDriverProperty()

  /**
    * User agent spoof for not getting banned from everything
 *
    * @return : Options for passing onto driver
    */
  def getOpts = {
    val opts = new ChromeOptions()
    val userAgent = "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2226.0 Safari/537.36"
    opts.addArguments("user-agent=" + userAgent)
    opts
  }

  def apply = new ChromeDriver(getOpts)

  /**
    * Forcibly authenticate a proxy
 *
    * @param proxyEnc : Full proxy description for
    *                 dedication proxy
    * @return Driver logged in and ready
    */
  def driverByProxy(proxyEnc: ProxyDescr) = {
    val cap = mkProxy(proxyEnc.hostPort)
    val driver = new ChromeDriver(cap)
    driver.get(ProxyManager.getIp)
    SeleniumChrome.browserLogin(proxyEnc.user, proxyEnc.pass)
    driver
  }


  // This might also work but had issues
  /*  def driverByProxy(proxyEnc: String) = {
      val opts = getOpts
      //  opts.addArguments("--proxy-server=" + proxyEnc)
      import org.openqa.selenium.Proxy
      import JavaConversions._

      val ma = Map(
        "httpProxy" -> proxyEnc,
        "ftpProxy" -> proxyEnc,
        "sslProxy" -> proxyEnc,
        "noProxy" -> "None",
        "proxyType" -> "MANUAL",
        "class" -> "org.openqa.selenium.Proxy",
        "autodetect" -> false
      )
      val prox = new Proxy(ma)
      val cap = new DesiredCapabilities()
      cap.setCapability("proxy", prox)
      //  cap.setCapability("chromeOptions", opts)
      new ChromeDriver(opts)
    }*/

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
class SeleniumChrome(
                      startingUrl: Option[String] = None,
                      proxy: Option[ProxyDescr] = None
                    ) extends org.scalatest.selenium.WebBrowser
  with CrawlerLike{

  import SeleniumChrome._

  /**
    * This is the entrypoint to browser manipulations.
    * If this crashes this whole class becomes wonky.
    *
    * Try to just throw away instances of this class rather
    * than dealing with errors associating with trying to rebind
    * a new instance of a webdriver into some pre-existing instance
    * of this class.
    */
  implicit val webDriver = proxy.map{driverByProxy}.getOrElse{apply}

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
  def navigateToURL(tou: String) = {
    go to tou
   // numVisits() += 1
  }

  /**
    * HTML src code including stuff that only renders
    * after javascript engine executes
    *
    * @return : String of src code, save it somewhere
    */
  def getPageSource = pageSource

  /**
    * Closes the window / stops the driver process
    */
  def shutdown() = close

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
  def extractCookies: List[Cookie] = { // Move to implicits
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
  def setCookieAsActive(
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
    jc.foreach{ setCookieAsActive }
  }

  val isStarted = Var(false) // change to monad of switch on/off dag

  /**
    * Use this for reacting off of to determine when
    * the browser has actually started and loaded something
    */
  val started = startingUrl.map{q => F{navigateToURL(q)}}

  val numVisits = Var(0)



}
