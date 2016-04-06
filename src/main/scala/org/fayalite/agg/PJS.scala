package org.fayalite.agg

import java.util
import org.fayalite.agg.ProxyManager.ProxyDescr
import org.openqa.selenium.phantomjs.{PhantomJSDriverService, PhantomJSDriver}
import org.openqa.selenium.remote.DesiredCapabilities

import scala.concurrent.{Future, ExecutionContext}
import scala.util.Try


class PJS {



}


/**
  * PhantomJS utility stuff.
  */
object PJS {

  import scala.sys.process._

  /**
    * Phantomjs uses os.fork(). During debug if you have leftover
    * processes use this.
 *
    * @return : Result of hopefully successfully killing a bunch of dead
    *         processes left over
    */
  def windowsCleanUp = {
    Seq("taskkill", "/F", "/IM", "phantomjs.exe").!!
  }

  /**
    * Same as above, but you need priviledges. Probably
    * best not to use, just do it manually.
 *
    * @return : Kill processes in sudo mode result str
    */
  def macCleanUp = {
    Seq("sudo", "killall", "-9", "phantomjs").!!
  }

  /**
    * Get a driver with a preconfigured proxy launched
    * through os.fork
 *
    * @param p : Authenticated proxy description, for
    *          dedicated proxy as configured.
    * @return : Driver that has started but hasn't gone
    *         to any URL. This is a wrapper around a background
    *         forked process that gets communicated with
    *         to perform browser operations.
    */
  def mkProxyDriver(p: ProxyDescr) = {
    val capabilities: DesiredCapabilities = mkCapabilities(p)
    val driver = new PhantomJSDriver(capabilities)
    driver
  }

  /**
    * Take an HTTP proxy and return a Capabilities object
    * used for forming PhantomJS drivers
 *
    * @param p : Proxy dedicated description
    * @return Object that Selenium / opendriver / PhantomJS /
    *         etc expects for setting up config, in this case
    *         it's specific to PhantomJS
    */
  def mkCapabilities(p: ProxyDescr): DesiredCapabilities = {
    val cliArgsCap = new util.ArrayList[String]()
    cliArgsCap.add("--proxy=" + p.hostPort)
    cliArgsCap.add(s"--proxy-auth=${p.user}:${p.pass}")
    cliArgsCap.add("--proxy-type=http")
    val capabilities = DesiredCapabilities.phantomjs()
    capabilities.setCapability(
      PhantomJSDriverService.PHANTOMJS_CLI_ARGS, cliArgsCap)
    import fa._
    val pathExec: String = if (isWindows) "phantomjs.exe"
    else "phantomjs"
    capabilities.setCapability(
      PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY,
      pathExec)
    capabilities
  }

  /**
    * Get a convenient future to chain some action
    * on top of where you load up a URL and a number
    * of drivers.
 *
    * @param url : Page to visit initially, helps allocate
    *            memory in advance of a crawl
    * @param numDrivers : Number of instances of browsers
    *                   to launch
    * @return Their futures for reacting off
    */
  def launchProxyDrivers(
                          url: String,
                          numDrivers: Int = 5
                        ) = {
    import fa._
    val pr = ProxyManager.proxies.slice(0, numDrivers)
    pr.map { p =>
      Future{
        val d = Try{mkProxyDriver(p)}.getOrElse(mkProxyDriver(p))
        d.get(url)
        d
      }(ec)
    }
  }

}
