package me.tomaszwojcik.calcumau5

import javax.servlet.http.{HttpServletRequest, HttpServletResponse}

import me.tomaszwojcik.calcumau5.util.Logging
import org.eclipse.jetty.server.Request
import org.eclipse.jetty.server.handler.AbstractHandler

class MainHandler extends AbstractHandler with Logging {
  override def handle(target: String, baseReq: Request, req: HttpServletRequest, res: HttpServletResponse): Unit = {
    log.info("TEST")
  }
}
