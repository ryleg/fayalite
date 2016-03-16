package org.fayalite.agg

import java.awt.datatransfer.StringSelection
import java.awt.{Toolkit, Robot}
import java.awt.event.KeyEvent

import fa._
import org.openqa.selenium.Proxy
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.firefox.{FirefoxProfile, FirefoxDriver}
import org.openqa.selenium.remote.{CapabilityType, DesiredCapabilities}


/**
  * Simple example of proxy usage for PhantomJS / Selenium
  * and tests to get proxy support
  */
object Proxy {

  case class ProxyDescr(
                         hostPort: String,
                         user: String,
                         pass: String
                       )

  /**
    * This assumes you have a local text file filled with
    * colon delimited host:port:user:pass proxies
    *
    * @return Parsed usable proxies
    */
  def proxies = readLines(".proxies").toSeq.map{
    q =>
      q.split(":") match {
        case Array(x,y,z,w) =>
          ProxyDescr(x + ":" + y, z, w)
      }
  }

  val getIp: String = "http://www.whatismyipaddress.com"

  //https://Aladdin:OpenSesame@www.example.com/index.html doesn't seem to work
  // with selenium
  import SeleniumChrome.mkProxy

  def main(args: Array[String]) {

    val tes = proxies.head

    println("Testing with proxy " + tes)

    new SeleniumChrome(None, Some(tes))

    //  Thread.sleep(5000)
/*

    val (username, pass) = credentials
    browserLogin(username, pass)
    //wait
 //   Thread.sleep(5000)

        def err = driver.getErrorHandler.isIncludeServerErrors
        println(q + " is err " + err)
*/


      import org.openqa.selenium
/*

    val (host, port) = q.split("\\:")._2

    val fp = new FirefoxProfile()
    fp.setPreference("network.proxy.type", 1)
    fp.setPreference("network.proxy.socks", q)
    fp.setPreference("network.proxy.socks_port", port)

    val fd = new FirefoxDriver(fp)
*/


/*    # PhantomJS Driver
      service_args = [
    '--proxy=127.0.0.1:9050', # You can change these parameters to your liking.
    '--proxy-type=socks5', # Use socks4/socks5 based on your usage.
    ]
    driver = webdriver.PhantomJS(service_args=service_args)

    # Check if this works
      driver.get("http://ip.telize.com").source_code

/**/
    Finally I found it, here's how to do it :
      FirefoxProfile profile = new FirefoxProfile()
    //        profile.setPreference("network.proxy.http", "62.26.x.y")
    //        profile.setPreference("network.proxy.http_port", 8078)
    profile.setPreference("network.proxy.socks", "190.x.y.z")
    profile.setPreference("network.proxy.socks_port", 8**8)

    profile.setPreference("network.proxy.type", 1)         // this is used to set proxy configuration to manual, after which firefox considers the //proxy set above

    WebDriver driver = new FirefoxDriver(profile)
    driver.get("http://google.com")*/



      //  driver.close()
  //  }

    /*
    String PROXY = "localhost:8080"

org.openqa.selenium.Proxy proxy = new org.openqa.selenium.Proxy()
proxy.setHttpProxy(PROXY)
     .setFtpProxy(PROXY)
     .setSslProxy(PROXY)
DesiredCapabilities cap = new DesiredCapabilities()
cap.setCapability(CapabilityType.PROXY, proxy)

WebDriver driver = new InternetExplorerDriver(cap)
    new SeleniumChrome(Some("http://www.google.com"), Some(
      proxies.head))
*/

  }

}
