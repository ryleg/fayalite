package org.fayalite.agg

import dispatch.{Http, as, url}

/**
  * Created by aa on 7/2/2016.
  */
object Dispatch {

  import ProxyManager.ProxyDescr

  def getRequest(x: String)(implicit proxyD: Option[ProxyDescr] = None) = {
    val rq = url(x).GET
    proxyD.foreach { p =>
      rq.setProxyServer(// make implicit conv
        new com.ning.http.client.ProxyServer(p.host, p.port, p.user, p.pass)
      )
    }
    Http(rq OK as.String)(scala.concurrent.ExecutionContext.Implicits.global)
  }

}
