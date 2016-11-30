package me.tomaszwojcik.calcumau5

import javax.servlet.http.{HttpServlet, HttpServletRequest, HttpServletResponse}

import me.tomaszwojcik.calcumau5.util.Logging
import org.eclipse.jetty.http.HttpStatus

class DispatcherServlet extends HttpServlet with Logging {

  override def doGet(req: HttpServletRequest, resp: HttpServletResponse): Unit = {
    parsePath(req) match {
      case Seq("jobs", s: String, "runs") if s.startsWith("a") => log.info(s)
      case Seq("jobs", i: Int, "runs") => log.info(s"GET /jobs/$i/runs")
      case _ => resp.sendError(HttpStatus.NOT_FOUND_404)
    }
  }

  private def parsePath(req: HttpServletRequest): Seq[Any] = {
    req.getPathInfo.stripPrefix("/").stripSuffix("/").split("/").map { token =>
      if (token.forall(_.isDigit)) {
        token.toInt
      } else {
        token
      }
    }.toSeq
  }
}
