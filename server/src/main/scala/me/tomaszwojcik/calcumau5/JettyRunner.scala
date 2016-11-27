package me.tomaszwojcik.calcumau5

import me.tomaszwojcik.calcumau5.worker.{WorkerServlet, WorkerStore}
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.servlet.{ServletContextHandler, ServletHolder}
import com.softwaremill.macwire.wire
import me.tomaszwojcik.calcumau5.health.HealthServlet

object JettyRunner {

  lazy val workerStore: WorkerStore = wire[WorkerStore]

  lazy val workerServlet: WorkerServlet = wire[WorkerServlet]
  lazy val healthServlet: HealthServlet = wire[HealthServlet]

  def main(args: Array[String]): Unit = {
    val server = new Server(Config.Http.Port)
    try {
      server.setHandler {
        val handler = new ServletContextHandler()
        handler.addServlet(new ServletHolder(workerServlet), "/workers/*")
        handler.addServlet(new ServletHolder(healthServlet), "/health/*")
        handler
      }
      server.start()
      server.join()
    } finally {
      server.stop()
    }
  }
}
