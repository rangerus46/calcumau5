package me.tomaszwojcik.calcumau5.worker

import javax.servlet.http.{HttpServlet, HttpServletRequest, HttpServletResponse}

import me.tomaszwojcik.calcumau5.util.Logging
import org.eclipse.jetty.http.HttpStatus
import org.json4s._
import org.json4s.native.JsonMethods._

import scala.util.Success

class WorkerServlet(implicit val workerStore: WorkerStore)
  extends HttpServlet with Logging {

  import me.tomaszwojcik.calcumau5.Implicits._

  override def doGet(req: HttpServletRequest, resp: HttpServletResponse): Unit = {
    log.info("GET /workers")
  }

  override def doPost(req: HttpServletRequest, resp: HttpServletResponse): Unit = {
    req.getPathInfo match {
      case null | "/" => registerWorker(req, resp)
      case _ => resp.sendError(HttpStatus.NOT_FOUND_404)
    }
  }

  private def registerWorker(req: HttpServletRequest, resp: HttpServletResponse): Unit = {
    val json = parse(req.getInputStream)
    (json \ "address").extractOpt[String] match {

      case None =>
        resp.sendError(HttpStatus.BAD_REQUEST_400)

      case Some(addr) =>
        val worker = Worker(addr)
        worker.checkIfAlive() onComplete {

          // Instance at specified address is alive. Save the worker for later.
          case Success(true) =>
            worker.save()
            resp.setStatus(HttpStatus.CREATED_201)

          // Instance at specified address failed to respond.
          case _ =>
            resp.sendError(HttpStatus.BAD_REQUEST_400, s"Failed to connect to the worker at $addr")

        }

    }
  }
}
