package me.tomaszwojcik.calcumau5

import com.softwaremill.macwire.wire
import me.tomaszwojcik.calcumau5.health.HealthServlet
import me.tomaszwojcik.calcumau5.job.JobServlet
import me.tomaszwojcik.calcumau5.worker.{WorkerServlet, WorkerStore}
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.servlet.{ServletContextHandler, ServletHandler, ServletHolder}

object JettyRunner {

  lazy val workerStore: WorkerStore = wire[WorkerStore]

  lazy val workerServlet: WorkerServlet = wire[WorkerServlet]
  lazy val healthServlet: HealthServlet = wire[HealthServlet]
  lazy val jobServlet: JobServlet = wire[JobServlet]

  def main(args: Array[String]): Unit = {
    val server = new Server(Config.Http.Port)
    try {
//      val handler = new ServletContextHandler
//      handler.addServlet(new ServletHolder(new DispatcherServlet), "/*")
//      server.setHandler(handler)
      server.setHandler(new MainHandler)
      server.start()
      server.join()
    } finally {
      server.stop()
    }
  }
}
