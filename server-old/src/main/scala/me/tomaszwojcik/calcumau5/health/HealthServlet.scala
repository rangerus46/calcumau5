package me.tomaszwojcik.calcumau5.health

import javax.servlet.http.{HttpServlet, HttpServletRequest, HttpServletResponse}

import me.tomaszwojcik.calcumau5.util.Logging
import org.eclipse.jetty.http.HttpStatus

class HealthServlet extends HttpServlet with Logging {

  override def doGet(req: HttpServletRequest, resp: HttpServletResponse): Unit = {
    req.getPathInfo match {
      case "/ping" => handlePingRequest(req, resp)
      case _ => resp.sendError(HttpStatus.NOT_FOUND_404)
    }
  }

  def handlePingRequest(req: HttpServletRequest, resp: HttpServletResponse): Unit = {
    val writer = resp.getWriter
    try {
      writer.write("PONG")
      resp.setStatus(HttpStatus.OK_200)
    } finally {
      writer.close()
    }
  }
}
