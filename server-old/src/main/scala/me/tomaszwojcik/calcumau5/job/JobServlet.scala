package me.tomaszwojcik.calcumau5.job

import javax.servlet.http.{HttpServlet, HttpServletRequest, HttpServletResponse}

import me.tomaszwojcik.calcumau5.util.Logging

class JobServlet extends HttpServlet with Logging {

  override def doGet(req: HttpServletRequest, resp: HttpServletResponse): Unit = {
    val segments = req.getPathInfo.split('/')
    log.info(segments.mkString(", "))
  }

  override def doPost(req: HttpServletRequest, resp: HttpServletResponse): Unit = ???

}
