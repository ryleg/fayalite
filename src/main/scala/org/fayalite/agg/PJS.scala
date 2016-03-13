package org.fayalite.agg

import java.util
import java.util.concurrent.Executors

import org.fayalite.agg.Proxy.ProxyDescr
import org.openqa.selenium.Proxy
import org.openqa.selenium.phantomjs.{PhantomJSDriverService, PhantomJSDriver}
import org.openqa.selenium.remote.{CapabilityType, DesiredCapabilities}

import scala.concurrent.{Future, ExecutionContext}


/**
  * Created by aa on 3/11/2016.
  */
object PJS {

  def mkProxyDriver(p: ProxyDescr) = {
    val prox = new Proxy()
    val desiredProxy = p.hostPort
    prox.setHttpProxy(desiredProxy)
    prox.setFtpProxy(desiredProxy)
    prox.setSslProxy(desiredProxy)
    prox.setSocksUsername(p.user)
    prox.setSocksPassword(p.pass)

    val cliArgsCap = new util.ArrayList[String]()
    cliArgsCap.add("--proxy=" + p.hostPort)
    cliArgsCap.add(s"--proxy-auth=${p.user}:${p.pass}")
    cliArgsCap.add("--proxy-type=http")
    val capabilities = DesiredCapabilities.phantomjs();
    capabilities.setCapability(
      PhantomJSDriverService.PHANTOMJS_CLI_ARGS, cliArgsCap);
   // capabilities.setCapability(CapabilityType.PROXY, prox)
    import fa._
    val pathExec: String = if (isWindows) "phantomjs.exe"
    else "phantomjs"
    capabilities.setCapability(
      PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY,
      pathExec)
    val driver = new PhantomJSDriver(capabilities)
    driver
  }

  def launchProxyDrivers(url: String) = {
    import fa._
    val pr = Proxy.proxies
    val ec = ExecutionContext.fromExecutor(Executors.newFixedThreadPool(20))
    pr.map { p =>
      Future{
        val d = mkProxyDriver(p)
        d.get(url)
        d
      }//(ec)
    }
  }

  def main(args: Array[String]) {


  }
}
